package virtualstockexchange.matchengine;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {

	private int side;
	private long price;
	private int quantity;
	private long matchPrice;
	private int matchQuantity;
	private int lastMatchQuantity;
	private String account;
	private String symbol;

	private String status;

	private String orderId;
	
	private Date date;

	public static final Order EMPTY = new Order();

	private List<Order> childs = new ArrayList<Order>();
	
	private Order() {
		this.date = new Date();
	}

	public Order(String account, String symbol, long price, int quantity,
			int side) {
		this(symbol, price, quantity, side);
		this.setAccount(account);
		createOrderId(account, symbol);
	}

	private void createOrderId(String account, String symbol) {
		this.orderId = account + "-" + symbol + "-" + System.nanoTime();
	}

	public Order(String symbol, long price, int quantity, int side) {
		this();
		this.side = side;
		this.price = price;
		this.quantity = quantity;
		this.symbol = symbol;
		createOrderId(account, symbol);
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

	public int getRemainQuantity() {
		return quantity - matchQuantity;
	}

	public void setMatch(long matchPrice, int matchQuantity) {
		this.matchPrice = matchPrice;
		this.matchQuantity += matchQuantity;
		this.lastMatchQuantity = matchQuantity;
		Order order = new Order();
		order.matchPrice = matchPrice;
		order.matchQuantity = matchQuantity;
		updateStatusWhenMatch();
		childs.add(order);
	}

	private void updateStatusWhenMatch() {
		if (matchQuantity == quantity) {
			status = OrderStatus.FILLED;
		} else {
			status =  OrderStatus.PARTIAL_FILLED;
		}
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

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public boolean isClosed() {
		return OrderStatus.CANCELLED.equals(status) || (quantity == matchQuantity && account != null);
	}

	public String getSymbol() {
		return symbol;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	

	@Override
	public String toString() {
		return "Order [side=" + side + ", price=" + price + ", quantity="
				+ quantity + ", matchPrice=" + matchPrice + ", matchQuantity="
				+ matchQuantity + ", lastMatchQuantity=" + lastMatchQuantity
				+ ", account=" + account + ", symbol=" + symbol + ", status="
				+ status + ", orderId=" + orderId + "]";
	}

	public List<Order> getChilds() {
		return childs;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public static Order getDefaultOrder() {
		return new Order();
	}
}
