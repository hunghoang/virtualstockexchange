package virtualstockexchange.matchengine;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import virtualstockexchange.websocket.config.NotifyWebSocketHandler;

@Component
@DependsOn("notifyWebSocketHandler")
public class OrderReportHandler {

	private static final Logger logger = Logger.getLogger(OrderReportHandler.class);
	
	private BlockingQueue<Order> queue = new LinkedBlockingQueue<Order>();
	
	
	@Autowired
	public OrderReportHandler(final NotifyWebSocketHandler notifyWebSocketHandler) {
		logger.info("Noti:" + notifyWebSocketHandler);
		System.out.println("hello");
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
