package vn.com.vndirect.matchingengine;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import vn.com.vndirect.matchingengine.Order;
import vn.com.vndirect.matchingengine.OrderCleaner;
import vn.com.vndirect.matchingengine.Side;

public class OrderCleanerTest {
	
	@Test 
	public void testClearOrderWithNoOrderNeedToClear() {
		OrderCleaner cleaner = new OrderCleaner();
		List<Order> orders = new ArrayList<Order>();
		orders.add(new Order("VND", 1, 100, Side.BUY));
		orders.add(new Order("SSI", 1, 0, Side.BUY));
		orders.add(new Order("HAG", 1, 1000, Side.SELL));
		cleaner.clearnOrder(orders);
		Assert.assertEquals(3, orders.size());
	}

	@Test 
	public void testClearOrderWithOneOrderNeedToClear() {
		OrderCleaner cleaner = new OrderCleaner();
		List<Order> orders = new ArrayList<Order>();
		orders.add(new Order("VND", 1, 100, Side.BUY));
		orders.add(new Order("SSI", 1, 0, Side.BUY));
		orders.add(new Order("HAG", 1, 1000, Side.SELL));
		Order orderMatch1 = new Order("0123", "HAG", 1, 1000, Side.SELL);
		orderMatch1.setMatch(1, 1000);
		cleaner.clearnOrder(orders);
		Assert.assertEquals(3, orders.size());
	}

	@Test 
	public void testClearOrder() {
		OrderCleaner cleaner = new OrderCleaner();
		List<Order> orders = new ArrayList<Order>();
		orders.add(new Order("VND", 1, 100, Side.BUY));
		orders.add(new Order("SSI", 1, 0, Side.BUY));
		orders.add(new Order("HAG", 1, 1000, Side.SELL));
		Order orderMatch1 = new Order("0123", "HAG", 1, 1000, Side.SELL);
		orderMatch1.setMatch(1, 1000);
		orders.add(orderMatch1);
		orders.add(new Order("0123", "HAG", 1, 1, Side.SELL));
		Order orderMatch2 = new Order("0123", "HAG", 1, 2000, Side.SELL);
		orderMatch2.setMatch(1, 2000);
		orders.add(orderMatch2);
		cleaner.clearnOrder(orders);
		Assert.assertEquals(4, orders.size());
	}

}
