package virtualstockexchange.price;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import virtualstockexchange.matchengine.Market;
import virtualstockexchange.matchengine.Order;
import virtualstockexchange.matchengine.OrderCreator;
import virtualstockexchange.matchengine.OrderReportHandler;
import vn.com.vndirect.priceservice.datamodel.SecInfo;

@Component
@DependsOn("orderReportHandler")
public class StockMessageRabbitConfigurationListener extends
		MessageRabbitConfigurationListener {
	
	private static final Logger logger = Logger.getLogger(StockMessageRabbitConfigurationListener.class);
	
	@Autowired
	private OrderCreator orderCreator;
	
	@Autowired
	private Market market;
	
	@Autowired
	private OrderReportHandler orderReportHandler;
	
	@Autowired
	public StockMessageRabbitConfigurationListener(
			@Value("${secinfo_external_queue}") String queueName,
			@Value("${secinfo_fanout_exchange}") String exchageName) {
		super(queueName, exchageName);
	}

	@PostConstruct
	public void init() {
		super.init();
	}

	@Bean
	public SimpleMessageListenerContainer stockListenerContainer() {
		return super.createListenerContainer();
	}
	
	@Override
	public void handleMessage(Object object) {
		List<Order> orders = orderCreator.createOrder((SecInfo) object);
		for (Order order : orders) {
			List<Order> reports = market.place(order);
			orderReportHandler.update(reports);
		}
		
	}
}
