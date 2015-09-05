package virtualstockexchange.matchengine;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import virtualstockexchange.balance.BalanceChecker;
import virtualstockexchange.balance.BalanceException;

public class OrderExecutionTest {

	private String curAccount;
	private String curSec;
	private long curHold;
	private Order fakeOrder;
	
	private BalanceChecker balanceChecker = new BalanceChecker() {
		@Override
		public void holdMoney(String account, long money)  throws BalanceException {
			curAccount = account;
			curHold = money;
		}

		@Override
		public void holdSecurity(String account, String sec, int quantity)   throws BalanceException {
			curAccount = account;
			curHold = quantity;
			curSec = sec;
		}
	};

	@Before
	public void setup() {
		fakeOrder = new Order("abcd", "VND", 1200, 100, Side.BUY);
	}
	
	private OrderExecution orderExecution = new OrderExecution(balanceChecker, new Market(new OrderCleaner()) {
		public Order cancel(String orderId) { return fakeOrder;};
	});

	@Test
	public void testOrderExecutionPlaceBuyOrder() {
		orderExecution.executeOrder(fakeOrder);
		Assert.assertEquals("SENT", fakeOrder.getStatus());
		Assert.assertEquals("abcd", curAccount);
		Assert.assertEquals(1200 * 100, curHold);
	}

	@Test
	public void testOrderExecutionPlaceSellOrder() {
		fakeOrder = new Order("abcd", "VND", 1200, 100, Side.SELL);
		orderExecution.executeOrder(fakeOrder);
		Assert.assertEquals("SENT", fakeOrder.getStatus());
		Assert.assertEquals("abcd", curAccount);
		Assert.assertEquals(100, curHold);
		Assert.assertEquals("VND", curSec);
	}

	@Test
	public void testOrderExecutionCancelledSellOrder() {
		fakeOrder = new Order("abcd", "VND", 1200, 100, Side.SELL);
		orderExecution.executeCancelledOrder(fakeOrder);
		Assert.assertEquals("abcd", curAccount);
		Assert.assertEquals(-100, curHold);
		Assert.assertEquals("VND", curSec);
	}
}
