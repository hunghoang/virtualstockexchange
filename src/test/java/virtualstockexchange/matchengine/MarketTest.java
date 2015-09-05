package virtualstockexchange.matchengine;

import org.junit.Assert;
import org.junit.Test;

public class MarketTest {

	private Market market = new Market(new OrderCleaner());
	
	@Test
	public void testPlaceOrderInMarket() {
		market.place(new Order("abc", "VND", 1, 2, Side.BUY));
		market.place(new Order("abc", "SSI", 1, 2, Side.BUY));
		market.place(new Order("abc", "ACB", 1, 2, Side.SELL));
		Assert.assertEquals(3, market.getAllOrders().size());
	}
	
	@Test
	public void testPlaceOrderInMarketAndCheckMatchOrderIsCreated() {
		market.place(new Order("VND", 1, 2, Side.BUY));
		MatchOrder matchOrder = market.getMatchOrder("VND");
		Assert.assertEquals(1, matchOrder.getBuys().size());
	}

	@Test
	public void testPlaceOrderInMarketAndCheck2MatchOrderIsCreated() {
		market.place(new Order("VND", 1, 2, Side.BUY));
		market.place(new Order("VND", 1, 2, Side.BUY));
		market.place(new Order("SSI", 1, 2, Side.BUY));
		MatchOrder matchOrder = market.getMatchOrder("VND");
		Assert.assertEquals(2, matchOrder.getBuys().size());
		MatchOrder matchOrder2 = market.getMatchOrder("SSI");
		Assert.assertEquals(1, matchOrder2.getBuys().size());
	}
	
	@Test
	public void testCancelBuyOrderInMarketAndCheckHistory() {
		Order order = new Order("abc", "VND", 1, 2, Side.BUY);
		market.place(order);
		String orderId = order.getOrderId();
		market.cancel(orderId);
		Assert.assertEquals(1, market.getAllOrders().size());
		MatchOrder matchOrder = market.getMatchOrder("VND");
		Assert.assertEquals(0, matchOrder.getBuys().size());
		Assert.assertEquals("CANCELLED", order.getStatus());
	}
	
	@Test
	public void testCancelSellOrderInMarket() {
		Order order = new Order("abc", "VND", 1, 2, Side.SELL);
		market.place(order);
		String orderId = order.getOrderId();
		market.cancel(orderId);
		Assert.assertEquals(1, market.getAllOrders().size());
		MatchOrder matchOrder = market.getMatchOrder("VND");
		Assert.assertEquals(0, matchOrder.getBuys().size());
		Assert.assertEquals("CANCELLED", order.getStatus());
	}
}


