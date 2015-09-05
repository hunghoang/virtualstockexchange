package virtualstockexchange.websocket.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class NotifyWebSocketHandler extends TextWebSocketHandler {

	private static final Logger logger = Logger
			.getLogger(NotifyWebSocketHandler.class);

	private List<WebSocketSession> sessions = new ArrayList<WebSocketSession>();

	private ObjectMapper objectMapper = new ObjectMapper();

	public synchronized void send(Object object) {
		try {
			String orderText = objectMapper.writeValueAsString(object);
			logger.info("send msg to client:" + orderText);
			for (WebSocketSession webSocketSession : sessions) {
				try {
					webSocketSession.sendMessage(new TextMessage(orderText));
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		} catch (JsonProcessingException e) {
			logger.error("", e);
		}
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message)
			throws Exception {
		session.sendMessage(new TextMessage("hello"));
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session)
			throws Exception {
		sessions.add(session);
		super.afterConnectionEstablished(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session,
			CloseStatus status) throws Exception {
		sessions.remove(session);
		super.afterConnectionClosed(session, status);
	}
}
