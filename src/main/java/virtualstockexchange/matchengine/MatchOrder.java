package virtualstockexchange.matchengine;

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
				if (price >= sell.getPrice()) {
					matchSell(results, order, sell);
				}
			}
			orderCleaner.clearOrder(sellOrders);
		} else {
			for (Order buy : buyOrders) {
				if (price <= buy.getPrice()) {
					matchBuy(results, buy, order);
				}
			}
			orderCleaner.clearOrder(buyOrders);
		}

		if (isMatch(results)) {
			results.add(order);
		}

		if (!order.isClosed()) {
			putOrder(order);
		}

		return results;
	}


	private void matchSell(List<Order> results, Order buy, Order sell) {
		int matchQuantity = getMatchQuantity(buy, sell);
		if (matchQuantity > 0) {
			match(buy, sell, matchQuantity);
			results.add(sell);
		}
		
	}

	private int getMatchQuantity(Order buy, Order sell) {
		return buy.getRemainQuantity() >= sell.getRemainQuantity() ? sell
				.getRemainQuantity() : buy.getRemainQuantity();
	}

	private boolean isMatch(List<Order> results) {
		return results.size() > 0;
	}
	
	private void matchBuy(List<Order> results, Order buy, Order sell) {
		int matchQuantity = getMatchQuantity(buy, sell);
		if (matchQuantity > 0) {
			match(buy, sell, matchQuantity);
			results.add(buy);
		}
		
	}

	private void match(Order buy, Order sell, int matchQuantity) {
		long matchPrice = sell.getPrice();
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
		long nextPriceBuy = -1;
		for (int i = 0; i < size; i++) {
			long priceBuy = buyOrders.get(i).getPrice();
			if ( i < size - 1) {
				nextPriceBuy = buyOrders.get(i + 1).getPrice();
			}
			if (price >= priceBuy && price > nextPriceBuy) {
				if ( i < size - 1 && price == priceBuy) {
					buyOrders.add(i + 1, order);
				} else {
					buyOrders.add(i, order);
				}
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
				if ( i < size - 1 && price == priceSell) {
					sellOrders.add(i + 1, order);
				} else {
					sellOrders.add(i, order);
				}
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

	public void remove(Order order) {
		if (order.getSide() == Side.BUY) {
			buyOrders.remove(order);
		} else {
			sellOrders.remove(order);
		}
	}

}
