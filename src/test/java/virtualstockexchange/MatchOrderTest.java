package virtualstockexchange;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class MatchOrderTest {

	private MatchOrder matchOrder;

	@Before
	public void setUp() {
		matchOrder = new MatchOrder();
	}

	@Test
	public void testMatchOrderShouldRun() {
		Assert.assertFalse(false);
	}

	@Test
	public void testMatchOrderWithOneOrderShouldReturnEmpty() {
		List<Order> orders = matchOrder.match(new Order("VND", 12300, 1000,
				Side.BUY));
		Assert.assertEquals(0, orders.size());
	}

	@Test
	public void testMatchOrderWith2OrderSameSideShouldReturnEmptyWhenNoMatch() {
		List<Order> orders1 = matchOrder.match(new Order("VND", 12300, 1000,
				Side.BUY));
		List<Order> orders2 = matchOrder.match(new Order("VND", 12300, 1000,
				Side.BUY));
		Assert.assertEquals(0, orders1.size());
		Assert.assertEquals(0, orders2.size());
	}

	@Test
	public void testMatchOrderWith2OrderMatch() {
		List<Order> orders1 = matchOrder.match(new Order("VND", 12300, 1000,
				Side.BUY));
		List<Order> orders2 = matchOrder.match(new Order("VND", 12300, 1000,
				Side.SELL));
		Assert.assertEquals(0, orders1.size());
		Assert.assertEquals(2, orders2.size());
		Assert.assertEquals(12300, orders2.get(0).getMatchPrice());
		Assert.assertEquals(1000, orders2.get(0).getMatchQuantity());
		Assert.assertEquals(12300, orders2.get(1).getMatchPrice());
		Assert.assertEquals(1000, orders2.get(1).getMatchQuantity());
	}

	@Test
	public void testMatchOrderWith2OrderMatchAndPriceBuyBiggerThanSell() {
		List<Order> orders1 = matchOrder.match(new Order("VND", 12300, 1000,
				Side.BUY));
		List<Order> orders2 = matchOrder.match(new Order("VND", 12200, 1000,
				Side.SELL));
		Assert.assertEquals(0, orders1.size());
		Assert.assertEquals(2, orders2.size());
		Assert.assertEquals(12200, orders2.get(0).getMatchPrice());
		Assert.assertEquals(1000, orders2.get(0).getMatchQuantity());
		Assert.assertEquals(12200, orders2.get(1).getMatchPrice());
		Assert.assertEquals(1000, orders2.get(1).getMatchQuantity());
	}

	@Test
	public void testMatchOrderPartialFill() {
		List<Order> orders1 = matchOrder.match(new Order("VND", 12300, 300,
				Side.BUY));
		List<Order> orders2 = matchOrder.match(new Order("VND", 12200, 1000,
				Side.SELL));
		Assert.assertEquals(0, orders1.size());
		Assert.assertEquals(2, orders2.size());
		Assert.assertEquals(12200, orders2.get(0).getMatchPrice());
		Assert.assertEquals(300, orders2.get(0).getMatchQuantity());
		Assert.assertEquals(12200, orders2.get(1).getMatchPrice());
		Assert.assertEquals(300, orders2.get(1).getMatchQuantity());
	}

	@Test
	public void testMatchOrderWithOneBuyAndManySellsFill() {
		List<Order> orders1 = matchOrder.match(new Order("VND", 12400, 6000,
				Side.BUY));
		List<Order> orders2 = matchOrder.match(new Order("VND", 12300, 3000,
				Side.SELL));
		List<Order> orders3 = matchOrder.match(new Order("VND", 12200, 2000,
				Side.SELL));
		List<Order> orders4 = matchOrder.match(new Order("VND", 12100, 1000,
				Side.SELL));
		Assert.assertEquals(0, orders1.size());
		Assert.assertEquals(2, orders2.size());
		Assert.assertEquals(12300, orders2.get(1).getMatchPrice());
		Assert.assertEquals(3000, orders2.get(1).getMatchQuantity());

		Assert.assertEquals(2, orders3.size());
		Assert.assertEquals(12200, orders3.get(1).getMatchPrice());
		Assert.assertEquals(2000, orders3.get(1).getMatchQuantity());

		Assert.assertEquals(2, orders4.size());
		Assert.assertEquals(12100, orders4.get(0).getMatchPrice());
		Assert.assertEquals(6000, orders4.get(0).getMatchQuantity());
		Assert.assertEquals(12100, orders4.get(1).getMatchPrice());
		Assert.assertEquals(1000, orders4.get(1).getMatchQuantity());
	}

	@Test
	public void testMatchOrderWithManySellsAndOneBuyFill() {
		matchOrder.match(new Order("VND", 12300, 3000, Side.SELL));
		List<Order> orders1 = matchOrder.match(new Order("VND", 12100, 1000,
				Side.SELL));
		List<Order> orders2 = matchOrder.match(new Order("VND", 12200, 2000,
				Side.SELL));
		List<Order> orders3 = matchOrder.match(new Order("VND", 12400, 6000,
				Side.BUY));
		Assert.assertEquals(0, orders1.size());
		Assert.assertEquals(0, orders2.size());

		Assert.assertEquals(4, orders3.size());
		Assert.assertEquals(12100, orders3.get(0).getMatchPrice());
		Assert.assertEquals(1000, orders3.get(0).getMatchQuantity());
		Assert.assertEquals(12200, orders3.get(1).getMatchPrice());
		Assert.assertEquals(2000, orders3.get(1).getMatchQuantity());
		Assert.assertEquals(12300, orders3.get(2).getMatchPrice());
		Assert.assertEquals(3000, orders3.get(2).getMatchQuantity());
		Assert.assertEquals(12300, orders3.get(3).getMatchPrice());
		Assert.assertEquals(6000, orders3.get(3).getMatchQuantity());
	}

	@Test
	public void putBuyOrdersAndCheckPosition() {
		matchOrder.putOrder(new Order("", 1, 1, Side.BUY));
		matchOrder.putOrder(new Order("", 3, 1, Side.BUY));
		matchOrder.putOrder(new Order("", 4, 1, Side.BUY));
		matchOrder.putOrder(new Order("", 2, 1, Side.BUY));
		matchOrder.putOrder(new Order("", 5, 1, Side.BUY));
		List<Order> buyOrders = matchOrder.getBuys();
		Assert.assertEquals(5, buyOrders.get(0).getPrice());
		Assert.assertEquals(4, buyOrders.get(1).getPrice());
		Assert.assertEquals(3, buyOrders.get(2).getPrice());
		Assert.assertEquals(2, buyOrders.get(3).getPrice());
		Assert.assertEquals(1, buyOrders.get(4).getPrice());
	}

	@Test
	public void putSellOrdersAndCheckPosition() {
		matchOrder.putOrder(new Order("", 4, 1, Side.SELL));
		matchOrder.putOrder(new Order("", 3, 1, Side.SELL));
		matchOrder.putOrder(new Order("", 1, 1, Side.SELL));
		matchOrder.putOrder(new Order("", 2, 1, Side.SELL));
		matchOrder.putOrder(new Order("", 5, 1, Side.SELL));
		List<Order> sellOrders = matchOrder.getSells();
		Assert.assertEquals(1, sellOrders.get(0).getPrice());
		Assert.assertEquals(2, sellOrders.get(1).getPrice());
		Assert.assertEquals(3, sellOrders.get(2).getPrice());
		Assert.assertEquals(4, sellOrders.get(3).getPrice());
		Assert.assertEquals(5, sellOrders.get(4).getPrice());
	}
	
	@Test
	public void putSellOrdersWithSomeOrderInSamePriceAndCheckPosition() {
		matchOrder.putOrder(new Order("", 4, 1, Side.SELL));
		matchOrder.putOrder(new Order("", 3, 1, Side.SELL));
		matchOrder.putOrder(new Order("", 2, 500, Side.SELL));
		matchOrder.putOrder(new Order("", 1, 1, Side.SELL));
		matchOrder.putOrder(new Order("", 2, 200, Side.SELL));
		matchOrder.putOrder(new Order("", 5, 1, Side.SELL));
		matchOrder.putOrder(new Order("", 2, 300, Side.SELL));
		List<Order> sellOrders = matchOrder.getSells();
		Assert.assertEquals(1, sellOrders.get(0).getPrice());
		Assert.assertEquals(2, sellOrders.get(1).getPrice());
		Assert.assertEquals(500, sellOrders.get(1).getQuantity());
		Assert.assertEquals(2, sellOrders.get(2).getPrice());
		Assert.assertEquals(200, sellOrders.get(2).getQuantity());
		Assert.assertEquals(2, sellOrders.get(3).getPrice());
		Assert.assertEquals(300, sellOrders.get(3).getQuantity());
		Assert.assertEquals(3, sellOrders.get(4).getPrice());
		Assert.assertEquals(4, sellOrders.get(5).getPrice());
		Assert.assertEquals(5, sellOrders.get(6).getPrice());
	}
}