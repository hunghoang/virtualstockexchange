package vn.com.vndirect.matchingengine;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OrderValiatorTest {

	private PriceService priceService;
	private OrderValidator validator;

	@Before
	public void setup() {
		priceService = new PriceService();
		validator = new OrderValidator(priceService);
	}

	@Test
	public void testOrderValidatorValidatePriceShouldReturnSuccessWithoutExceptionWhenNoDataIsSetForPriceService() {
		Order order = new Order("VND", 0, 100, 1, OrderType.LIMIT);
		validator.setSession(Session.CONTINUOUS_SESSION);
		validator.validate(order);
	}

	@Test
	public void testOrderValidatorValidatePriceShouldReturnSuccessWhenPriceIsInRange() {
		priceService.setPrice("VND", 20, 10);
		Order order = new Order("VND", 10, 100, 1, OrderType.LIMIT);
		Order order2 = new Order("VND", 11, 100, 1, OrderType.LIMIT);
		validator.setSession(Session.CONTINUOUS_SESSION);
		validator.validate(order);
		validator.validate(order2);
	}

	@Test
	public void testOrderValidatorValidatePriceShouldReturnFailWhenPriceSmallerThenFloor() {
		priceService.setPrice("VND", 20, 10);
		Order order = new Order("VND", 9, 100, 1, OrderType.LIMIT);
		validator.setSession(Session.CONTINUOUS_SESSION);
		try {
			validator.validate(order);
			Assert.fail();
		} catch (Exception e) {
			String msg = e.getMessage();
			Assert.assertEquals(ErrorCode.INVALID_PRICE, msg);
		}
	}

	@Test
	public void testOrderValidatorValidatePriceShouldReturnFailWhenPriceBiggerThanCeiling() {
		priceService.setPrice("VND", 20, 10);
		Order order = new Order("VND", 21, 100, 1, OrderType.LIMIT);
		validator.setSession(Session.CONTINUOUS_SESSION);
		try {
			validator.validate(order);
			Assert.fail();
		} catch (Exception e) {
			String msg = e.getMessage();
			Assert.assertEquals(ErrorCode.INVALID_PRICE, msg);
		}
	}

	@Test
	public void testOrderValidatorValidateSessionWhenPlaceATOOrderInContinuousSessionShouldReturnFail() {
		Order order = new Order("VND", 21, 100, 1, OrderType.ATO);
		validator.setSession(Session.CONTINUOUS_SESSION);
		try {
			validator.validate(order);
			Assert.fail();
		} catch (Exception e) {
			String msg = e.getMessage();
			Assert.assertEquals(ErrorCode.INVALID_SESSION_FOR_ORDER, msg);
		}
	}

	@Test
	public void testOrderValidatorValidateSessionWhenPlaceATCOrderInContinuousSessionShouldReturnFail() {
		Order order = new Order("VND", 21, 100, 1, OrderType.ATO);
		validator.setSession(Session.CONTINUOUS_SESSION);
		try {
			validator.validate(order);
			Assert.fail();
		} catch (Exception e) {
			String msg = e.getMessage();
			Assert.assertEquals(ErrorCode.INVALID_SESSION_FOR_ORDER, msg);
		}
	}

	@Test
	public void testOrderValidatorValidateSessionWhenPlaceMarketOrderInContinuousSessionShouldReturnPass() {
		Order order = new Order("VND", 21, 100, 1, OrderType.MARKET);
		validator.setSession(Session.CONTINUOUS_SESSION);
		validator.validate(order);
	}

	@Test
	public void testOrderValidatorValidateSessionWhenPlaceLimitOrderInContinuousSessionShouldReturnPass() {
		Order order = new Order("VND", 21, 100, 1, OrderType.LIMIT);
		validator.setSession(Session.CONTINUOUS_SESSION);
		validator.validate(order);
	}

	@Test
	public void testOrderValidatorValidateSessionWhenPlaceLimitOrderInATOSessionShouldReturnPass() {
		Order order = new Order("VND", 21, 100, 1, OrderType.LIMIT);
		validator.setSession(Session.CALL_MARKET_ATO_SESSION);
		validator.validate(order);
	}
	
	@Test
	public void testOrderValidatorValidateSessionWhenPlaceLimitOrderInATCSessionShouldReturnPass() {
		Order order = new Order("VND", 21, 100, 1, OrderType.LIMIT);
		validator.setSession(Session.CALL_MARKET_ATC_SESSION);
		validator.validate(order);
	}

	@Test
	public void testOrderValidatorValidateSessionWhenPlaceMarketOrderInPreOpenSessionShouldThrowException() {
		Order order = new Order("VND", 21, 100, 1, OrderType.MARKET);
		validator.setSession(Session.PREOPEN_SESSION);
		try {
			validator.validate(order);
			Assert.fail();
		} catch (Exception e) {
			String msg = e.getMessage();
			Assert.assertEquals(ErrorCode.SESSION_CLOSE, msg);
		}
	}

	@Test
	public void testOrderValidatorValidateSessionWhenPlaceMarketOrderInCloseSessionShouldThrowException() {
		Order order = new Order("VND", 21, 100, 1, OrderType.MARKET);
		validator.setSession(Session.CLOSE_SESSION);
		try {
			validator.validate(order);
			Assert.fail();
		} catch (Exception e) {
			String msg = e.getMessage();
			Assert.assertEquals(ErrorCode.SESSION_CLOSE, msg);
		}
	}

	@Test
	public void testOrderValidatorValidateSessionWhenPlaceMarketOrderInATOSessionShouldThrowException() {
		Order order = new Order("VND", 21, 100, 1, OrderType.MARKET);
		validator.setSession(Session.CALL_MARKET_ATO_SESSION);
		try {
			validator.validate(order);
			Assert.fail();
		} catch (Exception e) {
			String msg = e.getMessage();
			Assert.assertEquals(ErrorCode.INVALID_SESSION_FOR_ORDER, msg);
		}
	}

	@Test
	public void testOrderValidatorValidateSessionWhenPlaceLimitOrderInATCSessionShouldThrowException() {
		Order order = new Order("VND", 21, 100, 1, OrderType.MARKET);
		validator.setSession(Session.CALL_MARKET_ATC_SESSION);
		try {
			validator.validate(order);
			Assert.fail();
		} catch (Exception e) {
			String msg = e.getMessage();
			Assert.assertEquals(ErrorCode.INVALID_SESSION_FOR_ORDER, msg);
		}
	}
	
	@Test
	public void testOrderValidatorValidateSessionWhenCancelInATCSessionShouldThrowException() {
		validator.setSession(Session.CALL_MARKET_ATC_SESSION);
		try {
			validator.validateCancel("");
			Assert.fail();
		} catch (Exception e) {
			String msg = e.getMessage();
			Assert.assertEquals(ErrorCode.INVALID_SESSION_FOR_ORDER, msg);
		}
	}

	@Test
	public void testOrderValidatorValidateSessionWhenCancelInLOSessionShouldOK() {
		validator.setSession(Session.CONTINUOUS_SESSION);
		validator.validateCancel("");
	}
}
