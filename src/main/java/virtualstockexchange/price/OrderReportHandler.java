package virtualstockexchange.price;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import virtualstockexchange.matchengine.Order;

@Component
public class OrderReportHandler {

	private static final Logger logger = Logger.getLogger(OrderReportHandler.class);
	
	public void update(List<Order> orders) {
		for (Order order : orders) {
			logger.info("report when push order: " + order);
			if (order.getAccount() != null) {
				logger.info("report for real order: " + order);
			}
		}
	}
}
