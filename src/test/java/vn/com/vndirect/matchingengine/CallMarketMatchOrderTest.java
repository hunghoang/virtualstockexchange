package vn.com.vndirect.matchingengine;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CallMarketMatchOrderTest {
	private CallMarketMatchOrder matchOrder;

	@Before
	public void setUp() {
		matchOrder = new CallMarketMatchOrder();
	}

	@Test
	public void testMatchOrderShouldRun() {
		Assert.assertFalse(false);
	}

	@Test
	public void testMatchOrderWithOneOrderShouldReturnEmpty() {
		Order order = new Order("VND", 12300, 1000, Side.BUY);
		matchOrder.store(order);
		List<Order> orders = matchOrder.match();
		Assert.assertEquals(0, orders.size());
	}
	
	@Test
	public void testMatchOrderWith2OrderShouldReturnMatchResult() {
		Order order = new Order("VND", 12300, 1000, Side.BUY);
		matchOrder.store(order);
		Order order2 = new Order("VND", 12300, 1000, Side.SELL);
		matchOrder.store(order2);
		List<Order> orders = matchOrder.match();
		Assert.assertEquals(2, orders.size());
		Assert.assertEquals(12300, orders.get(1).getMatchPrice());
		Assert.assertEquals(1000, orders.get(1).getMatchQuantity());
	}

	@Test
	public void testMatchOrderWithSomeATOOrderInBuySideShouldReturnMatchResult() {
		Order order0 = new Order("VND", 0, 1200, Side.BUY, OrderType.ATO);
		matchOrder.store(order0);
		Order order1 = new Order("VND", 0, 800, Side.BUY, OrderType.ATO);
		matchOrder.store(order1);
		Order order2 = new Order("VND", 12200, 200, Side.BUY);
		matchOrder.store(order2);
		Order order3 = new Order("VND", 12300, 1000, Side.SELL);
		matchOrder.store(order3);
		List<Order> orders = matchOrder.match();
		Assert.assertEquals(2, orders.size());
		Assert.assertEquals(12300, orders.get(0).getMatchPrice());
		Assert.assertEquals(1000, orders.get(0).getMatchQuantity());
	}

	@Test
	public void testMatchOrderWithSomeATOOrderInSellSideShouldReturnMatchResult() {
		Order order0 = new Order("VND", 0, 1000, Side.SELL, OrderType.ATO);
		matchOrder.store(order0);
		Order order2 = new Order("VND", 12200, 200, Side.SELL);
		matchOrder.store(order2);
		Order order3 = new Order("VND", 12300, 1200, Side.BUY);
		matchOrder.store(order3);
		List<Order> orders = matchOrder.match();
		Assert.assertEquals(4, orders.size());
		Assert.assertEquals(12300, orders.get(0).getMatchPrice());
		Assert.assertEquals(1000, orders.get(0).getMatchQuantity());
		Assert.assertEquals(12300, orders.get(1).getMatchPrice());
		Assert.assertEquals(1000, orders.get(1).getMatchQuantity());
		Assert.assertEquals(12300, orders.get(2).getMatchPrice());
		Assert.assertEquals(200, orders.get(2).getMatchQuantity());
		Assert.assertEquals(12300, orders.get(3).getMatchPrice());
		Assert.assertEquals(200, orders.get(3).getMatchQuantity());
		Assert.assertEquals(12300, order3.getMatchPrice());
		Assert.assertEquals(1200, order3.getMatchQuantity());
	}
	
	@Test
	public void testMatchOrderWithSomeATOOrderAndSomeLimitOrderShouldMatchATOOrderFirst() {
		Order order0 = new Order("VND", 12100, 3000, Side.SELL);
		matchOrder.store(order0);
		Order order1 = new Order("VND", 0, 900, Side.SELL, OrderType.ATO);
		matchOrder.store(order1);
		Order order2 = new Order("VND", 0, 200, Side.SELL, OrderType.ATO);
		matchOrder.store(order2);
		Order order3 = new Order("VND", 12300, 1200, Side.BUY);
		matchOrder.store(order3);
		List<Order> orders = matchOrder.match();
		Assert.assertEquals(6, orders.size());
		Assert.assertEquals(12300, orders.get(0).getMatchPrice());
		Assert.assertEquals(900, orders.get(0).getMatchQuantity());
		
		Assert.assertEquals(12300, orders.get(1).getMatchPrice());
		Assert.assertEquals(900, orders.get(1).getMatchQuantity());
		
		Assert.assertEquals(12300, orders.get(2).getMatchPrice());
		Assert.assertEquals(200, orders.get(2).getMatchQuantity());
		
		Assert.assertEquals(12300, orders.get(3).getMatchPrice());
		Assert.assertEquals(200, orders.get(3).getMatchQuantity());

		Assert.assertEquals(12300, orders.get(4).getMatchPrice());
		Assert.assertEquals(100, orders.get(4).getMatchQuantity());

		Assert.assertEquals(12300, orders.get(5).getMatchPrice());
		Assert.assertEquals(100, orders.get(5).getMatchQuantity());
		
		Assert.assertEquals(12300, order3.getMatchPrice());
		Assert.assertEquals(1200, order3.getMatchQuantity());
	}

}
