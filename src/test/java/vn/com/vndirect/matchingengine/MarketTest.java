package vn.com.vndirect.matchingengine;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MarketTest {

	private PriceService priceService = new PriceService();
	private Market market = new Market(new OrderCleaner(), priceService);
	
	@Before
	public void setup() {
		priceService.setPrice("SSI", 100, 1);
		priceService.setPrice("VND", 100, 1);
		market.setSession(Session.CONTINUOUS_SESSION);
	}
	
	@Test
	public void testPlaceOrderInMarketShouldReturnOrderWithStatusSent() {
		List<Order> results = market.place(new Order("abc", "VND", 1, 2, Side.BUY));
		Order order = results.get(0);
		Assert.assertEquals(OrderStatus.SENT, order.getStatus());
	}
	
	@Test
	public void testPlaceMarketOrderInMarketShouldReturnOrderWithStatusExpiredWhenNoMatch() {
		Order order = new Order("abc", "VND", 1, 2, Side.BUY);
		order.setOrderType(OrderType.MARKET);
		List<Order> results = market.place(order);
		Order result = results.get(0);
		Assert.assertEquals(OrderStatus.EXPIRED, result.getStatus());
	}
	
	public void testPlaceOrderInMarketShouldStoreOrder() {
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
	public void testPlaceOrdersInMarketAndCheck2MatchOrderIsCreated() {
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
		Order cancelOrder = market.cancel(orderId);
		Assert.assertEquals(1, market.getAllOrders().size());
		MatchOrder matchOrder = market.getMatchOrder("VND");
		Assert.assertEquals(0, matchOrder.getSells().size());
		Assert.assertEquals("CANCELLED", cancelOrder.getStatus());
		Assert.assertEquals(2, cancelOrder.getRemainQuantity());
	}
	
	@Test
	public void testPlaceMarketOrderShouldRemoveOrderThisOrderFromMatchOrderStorageWhenNoMatch() {
		Order order = new Order("abc", "VND", 1, 2, Side.SELL);
		order.setOrderType(OrderType.MARKET);
		market.place(order);
		Assert.assertEquals(1, market.getAllOrders().size());
		MatchOrder matchOrder = market.getMatchOrder("VND");
		Assert.assertEquals(0, matchOrder.getSells().size());
	}
	
	@Test
	public void testPlaceATOOrderShouldKeepOrderInCallMarketMatch() {
		Order order = new Order("abc", "VND", 1, 2, Side.SELL);
		order.setOrderType(OrderType.ATO);
		market.setSession(Session.CALL_MARKET_ATO_SESSION);
		market.place(order);
		Assert.assertEquals(1, market.getAllOrders().size());
		CallMarketMatchOrder callMarketMatchOrder = market.getCallMarketMatchOrder("VND");
		Assert.assertEquals(1, callMarketMatchOrder.getSells().size());	
	}
	
	@Test
	public void testWhenSessionIsCloseThenAllOrderShouldBeCleared() {
		market.setSession(Session.CALL_MARKET_ATO_SESSION);
		Order order = new Order("abc", "VND", 1, 2, Side.SELL);
		order.setOrderType(OrderType.ATO);
		market.place(order);
		market.setSession(Session.CONTINUOUS_SESSION);
		order = new Order("abc", "VND", 1, 2, Side.SELL);
		order.setOrderType(OrderType.MARKET);
		market.place(order);
		order = new Order("abc", "VND", 1, 2, Side.SELL);
		order.setOrderType(OrderType.LIMIT);
		market.place(order);
		market.setSession(Session.CALL_MARKET_ATC_SESSION);
		order = new Order("abc", "VND", 1, 2, Side.SELL);
		order.setOrderType(OrderType.ATC);
		market.place(order);
		market.setSession(Session.CLOSE_SESSION);
		
		Assert.assertEquals(0, market.getAllOrders().size());
		Assert.assertNull(market.getMatchOrder("VND"));
		Assert.assertNull(market.getCallMarketMatchOrder("VND"));
	}
}


