package virtualstockexchange;

import java.util.ArrayList;
import java.util.List;

public class MatchOrder {

	private List<Order> buyOrders = new ArrayList<Order>();
	private List<Order> sellOrders = new ArrayList<Order>();

	public List<Order> match(Order order) {
		List<Order> results = new ArrayList<Order>();
		int side = order.getSide();
		long price = order.getPrice();
		if (side == Side.BUY) {
			for (Order sell : sellOrders) {
				if (price >= sell.getPrice()) {
					match(order, sell);
					results.add(sell);
				}
			}
		} else {
			for (Order buy : buyOrders) {
				if (price <= buy.getPrice()) {
					match(buy, order);
					results.add(buy);
				}
			}
		}

		if (isMatch(results)) {
			results.add(order);
		}

		putOrder(order);

		return results;
	}

	private boolean isMatch(List<Order> results) {
		return results.size() > 0;
	}

	private void match(Order buy, Order sell) {
		long matchPrice = sell.getPrice();
		int matchQuantity = buy.getQuantity() >= sell.getQuantity() ? sell
				.getQuantity() : buy.getQuantity();
		buy.setMatch(matchPrice, matchQuantity);
		sell.setMatch(matchPrice, matchQuantity);

	}

	public void putOrder(Order order) {
		long price = order.getPrice();
		if (order.getSide() == Side.BUY) {
			putBuyOrder(order, price);
		} else {
			putSellOrder(order, price);
		}
	}

	private void putBuyOrder(Order order, long price) {
		int size = buyOrders.size();
		for (int i = 0; i < size; i++) {
			long priceBuy = buyOrders.get(i).getPrice();
			if (price >= priceBuy) {
				buyOrders.add(i, order);
				return;
			}
		}
		buyOrders.add(order);
	}

	private void putSellOrder(Order order, long price) {
		int size = sellOrders.size();
		long nextPriceSell = Integer.MAX_VALUE;
		for (int i = 0; i < size; i++) {
			long priceSell = sellOrders.get(i).getPrice();
			if ( i < size - 1) {
				nextPriceSell = sellOrders.get(i + 1).getPrice();
			}
			
			if (price <= priceSell && price < nextPriceSell) {
				sellOrders.add(i, order);
				return;
			}
		}
		sellOrders.add(order);
	}

	protected List<Order> getBuys() {
		return buyOrders;
	}

	protected List<Order> getSells() {
		return sellOrders;
	}

}
