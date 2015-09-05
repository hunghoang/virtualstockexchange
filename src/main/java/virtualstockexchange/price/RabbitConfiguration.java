package virtualstockexchange.price;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
	
	@Value("${amqp.port:5672}") 
	private int port = 5672;
	
	@Value("${rabbitmq.addresses}") 
	private String rabbitmqAddresses;
	
	
	@Value("${rabbitmq.username}") 
	private String username;
	
	@Value("${rabbitmq.password}") 
	private String password;
	
	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setAddresses(rabbitmqAddresses);
		connectionFactory.setUsername(username);
		connectionFactory.setPassword(password);
		return connectionFactory;
	}

	@Bean
	public MessageListenerAdapter messageListener() {
		MessageListenerAdapter listener = new MessageListenerAdapter(this, jsonMessageConverter());
		return listener;
	}
	
	@Bean 
	public RabbitTemplate rabbitTemplate() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory());
		template.setMessageConverter(jsonMessageConverter());
		return template;
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new JsonMessageConverter();
	}
	
	@Bean
	public AmqpAdmin amqpAdmin() {
		RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
		return rabbitAdmin ;
	}

}
