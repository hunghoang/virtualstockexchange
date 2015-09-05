package virtualstockexchange.rest.controller;

public class NewOrderRequest {
	private String symbol;
	private long price;
	private int quantity;
	private String orderType;
	private String side;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getSide() {
		return side;
	}

	public void setSide(String side) {
		this.side = side;
	}

	@Override
	public String toString() {
		return String
				.format("{side: %s, symbol: %s, orderType: %s, limitPrice: %s, quantity: %s}",
						side, symbol, orderType, price, quantity);
	}
}
