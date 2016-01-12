package vn.com.vndirect.matchingengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Market {

	private static final Logger logger = LoggerFactory.getLogger(Market.class);

	private Map<String, Order> historyOrders = new HashMap<String, Order>();
	private Map<String, Order> orders = new HashMap<String, Order>();
	private Map<String, MatchOrder> matchOrderMap = new HashMap<String, MatchOrder>();
	private Map<String, CallMarketMatchOrder> callMarketMatchOrderMap = new HashMap<String, CallMarketMatchOrder>();
	private OrderCleaner orderCleaner;
	private int session = Session.PREOPEN_SESSION;
	private OrderValidator orderValidator;

	public Market(OrderCleaner orderCleaner, PriceService priceService) {
		this.orderCleaner = orderCleaner;
		orderValidator = new OrderValidator(priceService);
	}

	public List<Order> place(Order order) {
		List<Order> orders = new ArrayList<Order>(1);
		validate(order);
		updateOrder(order);
		if (session == Session.CONTINUOUS_SESSION) {
			MatchOrder matchOrder = createMatchOrder(order);
			List<Order> results = matchOrder.match(order);
			updateIfMarketOrder(results, order);
			orders.addAll(results);
		} else if (session == Session.CALL_MARKET_ATO_SESSION
				|| session == Session.CALL_MARKET_ATC_SESSION) {
			CallMarketMatchOrder matchOrder = createCallMarketMatchOrder(order);
			matchOrder.store(order);
		}

		if (orders.size() == 0) {
			orders.add(order);
		}
		return orders;
	}

	private void validate(Order order) {
		orderValidator.validate(order);
	}

	private CallMarketMatchOrder createCallMarketMatchOrder(Order order) {
		CallMarketMatchOrder matchOrder = callMarketMatchOrderMap.get(order
				.getSymbol());
		if (matchOrder == null) {
			matchOrder = new CallMarketMatchOrder();
			callMarketMatchOrderMap.put(order.getSymbol(), matchOrder);
		}
		return matchOrder;
	}

	private MatchOrder createMatchOrder(Order order) {
		MatchOrder matchOrder = matchOrderMap.get(order.getSymbol());
		if (matchOrder == null) {
			matchOrder = new MatchOrder(orderCleaner);
			matchOrderMap.put(order.getSymbol(), matchOrder);
		}
		return matchOrder;
	}

	private void updateOrderStatus(Order order) {
		order.setStatus(OrderStatus.SENT);
	}

	private void updateOrder(Order order) {
		orders.put(order.getOrderId(), order);
		updateOrderStatus(order);
		if (order.getAccount() != null) {
			historyOrders.put(order.getOrderId(), order);
		}
	}

	private void updateIfMarketOrder(List<Order> results, Order order) {
		if (results.size() == 0 && order.getOrderType() == OrderType.MARKET) {
			MatchOrder matchOrder = matchOrderMap.get(order.getSymbol());
			order.setStatus(OrderStatus.EXPIRED);
			matchOrder.remove(order);
		}

	}

	public Order cancel(String orderId) {
		orderValidator.validateCancel(orderId);
		Order order = orders.remove(orderId);
		if (order == null) {
			throw new OrderException(ErrorCode.ORDER_NOT_FOUND);
		}
		if (order.isClosed()) {
			throw new OrderException(ErrorCode.ORDER_UNCANCELLABLE);
		}
		MatchOrder matchOrder = matchOrderMap.get(order.getSymbol());
		matchOrder.remove(order);
		order.setStatus(OrderStatus.CANCELLED);
		return order;
	}

	public Order getOrder(String orderId) {
		return orders.get(orderId);
	}
	
	public List<Order> getAllOrders() {
		return new ArrayList<Order>(historyOrders.values());
	}

	public MatchOrder getMatchOrder(String symbol) {
		return matchOrderMap.get(symbol);
	}

	public CallMarketMatchOrder getCallMarketMatchOrder(String symbol) {
		return callMarketMatchOrderMap.get(symbol);
	}

	public void setSession(int session) {
		this.session = session;
		onSessionChange();
	}

	private void onSessionChange() {
		orderValidator.setSession(session);
		if (session == Session.CONTINUOUS_SESSION) {
			moveOrderToContinuousSession();
		} else if (session == Session.CALL_MARKET_ATC_SESSION) {
			moveOrderToCallMarketSession();
		} else if (session == Session.CLOSE_SESSION) {
			orders.clear();
			historyOrders.clear();
			matchOrderMap.clear();
			callMarketMatchOrderMap.clear();
		}
	}

	public List<Order> endCallMarketSession() {
		List<Order> results = new ArrayList<Order>();
		for (CallMarketMatchOrder matchOrder : callMarketMatchOrderMap.values()) {
			results.addAll(matchOrder.match());
		}
		return results;
	}

	public List<Order> expiredOrderAfterEndCallMarketSession() {
		List<Order> results = new ArrayList<Order>();
		for (Order order : orders.values()) {
			if (!order.isClosed()
					&& (order.getOrderType() == OrderType.ATO || order.getOrderType() == OrderType.ATC)) {
				order.setStatus(OrderStatus.EXPIRED);
				results.add(order);
			}
		}
		return results;
	}

	private void moveOrderToContinuousSession() {
		for (Order order : orders.values()) {
			if (!order.isClosed()) {
				MatchOrder matchOrder = createMatchOrder(order);
				matchOrder.store(order);
			}
		}
	}

	private void moveOrderToCallMarketSession() {
		for (Order order : orders.values()) {
			if (!order.isClosed()) {
				CallMarketMatchOrder callMarketMatchOrder = createCallMarketMatchOrder(order);
				callMarketMatchOrder.store(order);
			}
		}
	}
	
	public int getSession() {
		return session;
	}
}
