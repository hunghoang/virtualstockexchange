package virtualstockexchange.matchengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Market {

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
		historyOrders.put(order.getOrderId(), order);
		order.setStatus("SENT");
		return matchOrder.match(order);
	}

	public Order cancel(String orderId) {
		Order order = orders.remove(orderId);
		if (order == null) {
			throw new RuntimeException("Not found order: " + orderId);
		}
		if (order.isClosed()) {
			throw new RuntimeException("Order status" + order.getStatus() +" is not valid for cancel:");	
		}
		MatchOrder matchOrder = matchOrderMap.get(order.getSymbol());
		matchOrder.remove(order);
		order.setStatus("CANCELLED");
		return order;
	}
	
	public List<Order> getAllOrders() {
		return new ArrayList<Order>(orders.values());
	}

	public MatchOrder getMatchOrder(String symbol) {
		return matchOrderMap.get(symbol);
	}


}
