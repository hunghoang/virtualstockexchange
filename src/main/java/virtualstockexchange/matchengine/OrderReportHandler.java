package virtualstockexchange.matchengine;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import virtualstockexchange.balance.BalanceException;
import virtualstockexchange.websocket.config.NotifyWebSocketHandler;

@Component
@DependsOn("notifyWebSocketHandler")
public class OrderReportHandler {

	private static final Logger logger = Logger.getLogger(OrderReportHandler.class);
	
	private BlockingQueue<Order> queue = new LinkedBlockingQueue<Order>();
	
	private OrderExecution orderExecution;
	
	@Autowired
	public OrderReportHandler(final NotifyWebSocketHandler notifyWebSocketHandler, OrderExecution orderExecution) {
		logger.info("Noti:" + notifyWebSocketHandler);
		this.orderExecution = orderExecution; 
		new Thread() {
			@Override
			public void run() {
				while(true) {
					try {
						Order order = queue.take();
						notifyWebSocketHandler.send(order);
					} catch (InterruptedException e) {
						logger.error("Interrupted", e);
					}
				}
			}
		}.start();
		logger.info("start thread");
	}
	
	public void update(List<Order> orders) {
		try {
			orderExecution.fillOrder(orders);
		} catch (BalanceException e) {
			logger.error("Error when fill order", e);
		}
		for (Order order : orders) {
			if (order.getAccount() != null) {
				logger.info("report for real order: " + order);
				try {
					
					queue.put(order);
				} catch (InterruptedException e) {
					logger.error("Interrupted", e);
				}
			}
		}
	}
	
}
