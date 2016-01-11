package vn.com.vndirect.matchingengine;

public class OrderValidator {

	
	private PriceService priceService;
	private int session;
	
	public OrderValidator(PriceService priceService) {
		this.priceService = priceService;
	}
	
	public void validate(Order order) {
		validatePrice(order);
		validateSession(order);
	}

	private void validateSession(Order order) {
		if (session == Session.PREOPEN_SESSION || session == Session.CLOSE_SESSION) {
			throw new OrderException(ErrorCode.SESSION_CLOSE);
		} else if (session == Session.CONTINUOUS_SESSION) {
			if (order.getOrderType() == OrderType.ATO || order.getOrderType() == OrderType.ATC) {
				throw new OrderException(ErrorCode.INVALID_SESSION_FOR_ORDER);
			}
		} else if (session == Session.CALL_MARKET_ATO_SESSION || session == Session.CALL_MARKET_ATC_SESSION) {
			if (order.getOrderType() == OrderType.MARKET) {
				throw new OrderException(ErrorCode.INVALID_SESSION_FOR_ORDER);
			}
		}
		
	}

	private void validatePrice(Order order) {
		if (order.getOrderType() == OrderType.LIMIT) {
			String symbol = order.getSymbol();
			Price priceRange = priceService.getPrice(symbol);
			if (priceRange != null && (priceRange.ceiling < order.getPrice() || priceRange.floor > order.getPrice())) {
				throw new OrderException(ErrorCode.INVALID_PRICE);
			}
		}
	}

	public void setSession(int session) {
		this.session = session;
	}

	public void validateCancel(String orderId) {
		if (session != Session.CONTINUOUS_SESSION) {
			throw new OrderException(ErrorCode.INVALID_SESSION_FOR_ORDER);
		}
	}
}
