package vn.com.vndirect.matchingengine;

import java.util.List;

public class SortOrder {
	public static void putBuyOrder(Order order, long price, List<Order> buyOrders) {
		int size = buyOrders.size();
		long nextPriceBuy = -1;
		if (price == 0) {
			int callMarketIndex = -1;
			for (int i = 0; i < size; i++) {
				long priceBuy = buyOrders.get(i).getPrice();
				if (priceBuy > 0) {
					buyOrders.add(i, order);
					return;
				} else {
					callMarketIndex = i;
				}
			}
			if (callMarketIndex < size - 1) {
				buyOrders.add(callMarketIndex + 1, order);
			} else {
				buyOrders.add(order);
			}
			return;
		}
		
		for (int i = 0; i < size; i++) {
			long priceBuy = buyOrders.get(i).getPrice();
			if (i < size - 1) {
				nextPriceBuy = buyOrders.get(i + 1).getPrice();
			}
			if (price >= priceBuy && price > nextPriceBuy) {
				if (i < size - 1 && price == priceBuy) {
					buyOrders.add(i + 1, order);
				} else {
					buyOrders.add(i, order);
				}
				return;
			}
		}
		buyOrders.add(order);
	}

	public static void putSellOrder(Order order, long price, List<Order> sellOrders) {
		int size = sellOrders.size();
		long nextPriceSell = Integer.MAX_VALUE;
		for (int i = 0; i < size; i++) {
			long priceSell = sellOrders.get(i).getPrice();
			if (i < size - 1) {
				nextPriceSell = sellOrders.get(i + 1).getPrice();
			}

			if (price <= priceSell && price < nextPriceSell) {
				if (i < size - 1 && price == priceSell) {
					sellOrders.add(i + 1, order);
				} else {
					sellOrders.add(i, order);
				}
				return;
			}
		}
		sellOrders.add(order);
	}
}
