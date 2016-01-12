package vn.com.vndirect.matchingengine;

import java.util.ArrayList;
import java.util.List;

public class MatchOrder {

	private final OrderCleaner orderCleaner;

	private List<Order> buyOrders = new ArrayList<Order>();
	private List<Order> sellOrders = new ArrayList<Order>();

	public MatchOrder(OrderCleaner orderCleaner) {
		this.orderCleaner = orderCleaner;
	}

	public List<Order> match(Order order) {
		List<Order> results = new ArrayList<Order>();
		int side = order.getSide();
		long price = order.getPrice();
		if (side == Side.BUY) {
			for (Order sell : sellOrders) {
				if (price == 0 || price >= sell.getPrice()) {
					match2Orders(results, order, sell);
				}
			}
			orderCleaner.clearnOrder(sellOrders);
		} else {
			for (Order buy : buyOrders) {
				if (price <= buy.getPrice()) {
					match2Orders(results, order, buy);
				}
			}
			orderCleaner.clearnOrder(buyOrders);
		}

		if (isMatch(results)) {
			updatePriceIfMarketOrder(order);
		}
		

		if (!order.isClosed()) {
			store(order);
		}

		return results;
	}

	private void updatePriceIfMarketOrder(Order order) {
		if (order.getOrderType() == OrderType.MARKET) {
			long matchPrice = order.getMatchPrice();
			long priceStep = 100;
			if (matchPrice > 100000) {
				priceStep = 1000;
			} else if (matchPrice > 50000) {
				priceStep = 500;
			}
			
			if (order.getSide() == Side.BUY) {
				order.setPrice(matchPrice + priceStep);
			} else {
				order.setPrice(matchPrice - priceStep);
			}
		}
	}

	private void match2Orders(List<Order> results, Order newOrder, Order existing) {
		int matchQuantity = getMatchQuantity(newOrder, existing);
		if (matchQuantity > 0) {
			matchPriceAndQuantity(newOrder, existing, matchQuantity);
			results.add(existing.clone());
			results.add(newOrder.clone());
		}
	}

	private int getMatchQuantity(Order buy, Order sell) {
		return Math.min(buy.getRemainQuantity(), sell.getRemainQuantity());
	}

	private boolean isMatch(List<Order> results) {
		return results.size() > 0;
	}

	private void matchPriceAndQuantity(Order newOrder, Order existingOrder,
			int matchQuantity) {
		long matchPrice = existingOrder.getPrice();
		newOrder.setMatch(matchPrice, matchQuantity);
		existingOrder.setMatch(matchPrice, matchQuantity);
	}

	public void store(Order order) {
		long price = order.getPrice();
		if (order.getSide() == Side.BUY) {
			SortOrder.putBuyOrder(order, price, buyOrders);
		} else {
			SortOrder.putSellOrder(order, price, sellOrders);
		}
	}

	public void remove(Order order) {
		if (order.getSide() == Side.BUY) {
			buyOrders.remove(order);
		} else {
			sellOrders.remove(order);
		}
	}

	protected List<Order> getBuys() {
		return buyOrders;
	}

	protected List<Order> getSells() {
		return sellOrders;
	}

}
