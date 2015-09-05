package virtualstockexchange;

public class Order {

	private int side;
	private long price;
	private int quantity;
	private long matchPrice;
	private int matchQuantity;
	private int lastMatchQuantity;

	public Order(String symbol, long price, int quantity, int side) {
		this.side = side;
		this.price = price;
		this.quantity = quantity;
	}

	public int getSide() {
		return side;
	}

	public long getPrice() {
		return price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setMatch(long matchPrice, int matchQuantity) {
		this.matchPrice = matchPrice;
		this.matchQuantity +=matchQuantity;
		this.lastMatchQuantity = matchQuantity;
	}

	public int getMatchQuantity() {
		return matchQuantity;
	}

	public int getLastMatchQuantity() {
		return lastMatchQuantity;
	}

	public long getMatchPrice() {
		return matchPrice;
	}

}
