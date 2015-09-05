package virtualstockexchange.websocket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.AbstractJsonpResponseBodyAdvice;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebMvc
@EnableWebSocket
@PropertySource("classpath:/config/app.properties")
public class WebConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

	@Autowired
	private ApplicationContext applicationContext;
	
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

	}

	@ControllerAdvice
	static class JsonpAdvice extends AbstractJsonpResponseBodyAdvice {
		public JsonpAdvice() {
			super("jsonp");
		}
	}
	
	@Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
       return new PropertySourcesPlaceholderConfigurer();
    }
	
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

}
