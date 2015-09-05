package virtualstockexchange.price;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.eaio.uuid.UUID;

public class MessageRabbitConfigurationListener {

	protected String nameQueue;
	protected String nameFanoutExchange;
	
	@Autowired
	protected MessageConverter messageConverter;

	@Autowired
	protected ConnectionFactory amqpConnectionFactory;

	@Autowired
	protected ApplicationContext applicationContext;

	@Autowired
	protected AmqpAdmin amqpAdmin;

	private Queue queue;

	public MessageRabbitConfigurationListener(String queueName,
			String exchageName) {
		this.nameQueue = queueName;
		this.nameFanoutExchange = exchageName;
	}

	public void init() {
		queue = declareNameQueue();
		FanoutExchange exchange = createFanoutExchange();
		bindingQueueToExchange(queue, exchange);
	}

	private Queue declareNameQueue() {
		String queueName = nameQueue + new UUID();
		Queue queue = new Queue(queueName, false);
		amqpAdmin.declareQueue(queue);
		return queue;
	}

	private void bindingQueueToExchange(Queue queue, FanoutExchange exchange) {
		Binding binding = BindingBuilder.bind(queue).to(exchange);
		amqpAdmin.declareBinding(binding);
	}

	private FanoutExchange createFanoutExchange() {
		FanoutExchange exchange = new FanoutExchange(nameFanoutExchange, false,
				false);
		amqpAdmin.declareExchange(exchange);
		return exchange;
	}

	public SimpleMessageListenerContainer createListenerContainer() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(
				amqpConnectionFactory);
		container.setQueues(queue);
		container.setMessageListener(createListenerAdapter());
		container.setAcknowledgeMode(AcknowledgeMode.AUTO);
		return container;
	}

	public MessageListenerAdapter createListenerAdapter() {
		return new MessageListenerAdapter(this, messageConverter);
	}
	
	
	public void setAmqpAdmin(AmqpAdmin amqpAdmin) {
		this.amqpAdmin = amqpAdmin;
	}
	

	public void handleMessage(Object object) {
		System.out.println(object);

	}
}
