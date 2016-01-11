package vn.com.vndirect.matchingengine;

import java.util.ArrayList;
import java.util.List;

public class CallMarketMatchOrder {

	private List<Order> buyOrders = new ArrayList<Order>();
	private List<Order> sellOrders = new ArrayList<Order>();
	
	private List<Long> priceList = new ArrayList<Long>();

	public List<Order> getSells() {
		return sellOrders;
	}

	public List<Order> getBuys() {
		return buyOrders;
	}

	public void store(Order order) {
		priceList.add(order.getPrice());
		if (order.getSide() == Side.BUY) {
			SortOrder.putBuyOrder(order, order.getPrice(), buyOrders);
		} else {
			SortOrder.putSellOrder(order, order.getPrice(), sellOrders);
		}
	}

	public List<Order> match() {
		List<Order> results = new ArrayList<Order>();
		long bestPrice = findBestPrice();
		if (bestPrice > 0) {
			List<Order> couldMatchBuyOrder = couldMatchBuyOrderAtPrice(bestPrice);
			List<Order> couldMatchSellOrder = couldMatchSellOrderAtPrice(bestPrice);
			return matchOrders(bestPrice, couldMatchBuyOrder, couldMatchSellOrder);
		}
		return results;
	}

	private List<Order> matchOrders(long bestPrice, List<Order> couldMatchBuyOrder, List<Order> couldMatchSellOrder) {
		List<Order> results = new ArrayList<Order>();
		for(Order buyOrder: couldMatchBuyOrder) {
			for(Order sellOrder: couldMatchSellOrder) {
				results.addAll(match(bestPrice, buyOrder, sellOrder));
			}
		}
		return results;
	}

	private List<Order> match(long bestPrice, Order buyOrder, Order sellOrder) {
		List<Order> results = new ArrayList<Order>();
		int matchQuantity = Math.min(buyOrder.getRemainQuantity(), sellOrder.getRemainQuantity());
		if (matchQuantity > 0) {
			buyOrder.setMatch(bestPrice, matchQuantity);
			sellOrder.setMatch(bestPrice, matchQuantity);
			results.add(buyOrder);
			results.add(sellOrder);
		}
		return results;
	}

	private List<Order> couldMatchSellOrderAtPrice(long bestPrice) {
		List<Order> results = new ArrayList<Order>();
		for(Order order: sellOrders) {
			if (order.getPrice() <= bestPrice) {
				results.add(order);
			}
		}
		return results;
	}

	private List<Order> couldMatchBuyOrderAtPrice(long bestPrice) {
		List<Order> results = new ArrayList<Order>();
		for(Order order: buyOrders) {
			if (order.getPrice() == 0 || order.getPrice() >= bestPrice) {
				results.add(order);
			}
		}
		return results;
	}

	private long findBestPrice() {
		long bestPrice = 0;
		int maxQuantity =  0;
		for(Long price: priceList) {
			int quantity = calculateMatchQuantityAtPrice(price);
			if (maxQuantity <= quantity) {
				maxQuantity = quantity;
				bestPrice = price;
			}
		}
		if (maxQuantity > 0 && bestPrice > 0) {
			return bestPrice;
		}
		return 0;
	}

	private int calculateMatchQuantityAtPrice(Long price) {
		int buyQuantity = 0;
		int sellQuantity = 0;
		for(Order order: buyOrders) {
			if (order.getPrice() == 0 || order.getPrice() >= price) {
				buyQuantity += order.getQuantity();
			}
		}
		for(Order order: sellOrders) {
			if (order.getPrice() <= price) {
				sellQuantity += order.getQuantity();
			}
		}
		return Math.min(buyQuantity, sellQuantity);
	}

}
