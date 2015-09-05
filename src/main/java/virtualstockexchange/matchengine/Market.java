package virtualstockexchange.matchengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Market {

	private static final Logger logger = Logger.getLogger(Market.class);
	
	private Map<String, Order> historyOrders = new HashMap<String, Order>();
	private Map<String, Order> orders = new HashMap<String, Order>();
	private Map<String, MatchOrder> matchOrderMap = new HashMap<String, MatchOrder>();
	private OrderCleaner orderCleaner;

	@Autowired
	public Market(OrderCleaner orderCleaner) {
		this.orderCleaner = orderCleaner;
	}
	
	public List<Order> place(Order order) {
		MatchOrder matchOrder = matchOrderMap.get(order.getSymbol());
		if (matchOrder == null) {
			matchOrder = new MatchOrder(orderCleaner);
			matchOrderMap.put(order.getSymbol(), matchOrder);
		}
		orders.put(order.getOrderId(), order);
		updateHistory(order);
		order.setStatus("SENT");
		return matchOrder.match(order);
	}

	private void updateHistory(Order order) {
		if (order.getAccount() != null) {
			historyOrders.put(order.getOrderId(), order);
		}
	}

	public Order cancel(String orderId) {
		Order order = orders.remove(orderId);
		if (order == null) {
			Order error = Order.getDefaultOrder();
			error.setStatus("Khong tim thay order: " + orderId);
			logger.info(error.getStatus());
			return error;
		}
		if (order.isClosed()) {
			Order error = Order.getDefaultOrder();
			error.setStatus("Trang thai order: " + order.getStatus() + " khong phu hop de huy");
			logger.info("Order status" + error.getStatus() +" is not valid for cancel:");
			return error;
		}
		MatchOrder matchOrder = matchOrderMap.get(order.getSymbol());
		matchOrder.remove(order);
		order.setStatus("CANCELLED");
		return order;
	}
	
	public List<Order> getAllOrders() {
		return new ArrayList<Order>(historyOrders.values());
	}

	public MatchOrder getMatchOrder(String symbol) {
		return matchOrderMap.get(symbol);
	}


}
