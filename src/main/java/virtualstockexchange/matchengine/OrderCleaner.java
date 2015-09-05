package virtualstockexchange.matchengine;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class OrderCleaner {

	public void clearOrder(List<Order> orders) {
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
