package vn.com.vndirect.matchingengine;

import java.util.List;

public class OrderCleaner {

	public void clearnOrder(List<Order> orders) {
		int size = orders.size();
		for (int i = 0; i < size; i++) {
			Order order = orders.get(i);
			if (order.isClosed()) {
				orders.remove(i);
				if (i > 1) {
					i--;
					size--;
				} else {
					return;
				}
			}
		}
	}

}
