package virtualstockexchange.matchengine;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import vn.com.vndirect.priceservice.datamodel.SecInfo;

public class OrderCreatorTest {

	private OrderCreator orderCreator = new OrderCreator();
	
	@Test
	public void testOrderCreatorWithEmptyBidOffer() {
		SecInfo secInfo = new SecInfo();
		secInfo.setCode("VND");
		List<Order> orders = orderCreator.createOrder(secInfo);
		Assert.assertEquals(0, orders.size());
	}

	@Test
	public void testOrderCreatorWithBid1Value() {
		SecInfo secInfo = new SecInfo();
		secInfo.setCode("VND");
		secInfo.setBidPrice01(1200);
		secInfo.setBidQtty01(1500);
		List<Order> orders = orderCreator.createOrder(secInfo);
		Assert.assertEquals(1, orders.size());
	}

	@Test
	public void testOrderCreatorWithBid1Bid2Value() {
		SecInfo secInfo = new SecInfo();
		secInfo.setCode("VND");
		secInfo.setBidPrice01(1200);
		secInfo.setBidQtty01(1500);
		secInfo.setBidPrice02(1100);
		secInfo.setBidQtty02(500);
		List<Order> orders = orderCreator.createOrder(secInfo);
		Assert.assertEquals(2, orders.size());
	}

	@Test
	public void testOrderCreatorWithBid1Bid2Bid3Value() {
		SecInfo secInfo = new SecInfo();
		secInfo.setCode("VND");
		secInfo.setBidPrice01(1200);
		secInfo.setBidQtty01(1500);
		secInfo.setBidPrice02(1100);
		secInfo.setBidQtty02(500);
		secInfo.setBidPrice03(1000);
		secInfo.setBidQtty03(500);
		List<Order> orders = orderCreator.createOrder(secInfo);
		Assert.assertEquals(3, orders.size());
	}
	
	@Test
	public void testOrderCreatorWithOffer1Value() {
		SecInfo secInfo = new SecInfo();
		secInfo.setCode("VND");
		secInfo.setOfferPrice01(1200);
		secInfo.setOfferQtty01(1500);
		List<Order> orders = orderCreator.createOrder(secInfo);
		Assert.assertEquals(1, orders.size());
	}

	@Test
	public void testOrderCreatorWithOffer1Offer2Value() {
		SecInfo secInfo = new SecInfo();
		secInfo.setCode("VND");
		secInfo.setOfferPrice01(1200);
		secInfo.setOfferQtty01(1500);
		secInfo.setOfferPrice02(1300);
		secInfo.setOfferQtty02(1500);
		List<Order> orders = orderCreator.createOrder(secInfo);
		Assert.assertEquals(2, orders.size());
	}
	
	@Test
	public void testOrderCreatorWithOffer1Offer2Offer3Value() {
		SecInfo secInfo = new SecInfo();
		secInfo.setCode("VND");
		secInfo.setOfferPrice01(1200);
		secInfo.setOfferQtty01(1500);

		secInfo.setOfferPrice02(1300);
		secInfo.setOfferQtty02(1500);
		

		secInfo.setOfferPrice03(1400);
		secInfo.setOfferQtty03(1500);
		
		List<Order> orders = orderCreator.createOrder(secInfo);
		Assert.assertEquals(3, orders.size());
	}
	
	@Test
	public void testOrderCreatorCreateOrderWhenNoChangeOffer1() {
		SecInfo secInfo = new SecInfo();
		secInfo.setCode("VND");
		secInfo.setOfferPrice01(1200);
		secInfo.setOfferQtty01(1500);
		
		SecInfo secInfo2 = new SecInfo();
		secInfo2.setCode("VND");
		secInfo2.setOfferPrice01(1200);
		secInfo2.setOfferQtty01(1500);
		
		List<Order> orders = orderCreator.createOrder(secInfo);
		List<Order> orders2 = orderCreator.createOrder(secInfo2);
		Assert.assertEquals(1, orders.size());	
		Assert.assertEquals(0, orders2.size());	
	}

	@Test
	public void testOrderCreatorCreateOrderWhenChangeOffer1() {
		SecInfo secInfo = new SecInfo();
		secInfo.setCode("VND");
		secInfo.setOfferPrice01(1200);
		secInfo.setOfferQtty01(1500);
		
		SecInfo secInfo2 = new SecInfo();
		secInfo2.setCode("VND");
		secInfo2.setOfferPrice01(1300);
		secInfo2.setOfferQtty01(1500);
		
		List<Order> orders = orderCreator.createOrder(secInfo);
		List<Order> orders2 = orderCreator.createOrder(secInfo2);
		Assert.assertEquals(1, orders.size());	
		Assert.assertEquals(1, orders2.size());	
	}
	
	@Test
	public void testOrderCreatorCreateOrderWhenChangeOfferQty1() {
		SecInfo secInfo = new SecInfo();
		secInfo.setCode("VND");
		secInfo.setOfferPrice01(1200);
		secInfo.setOfferQtty01(1500);
		
		SecInfo secInfo2 = new SecInfo();
		secInfo2.setCode("VND");
		secInfo2.setOfferPrice01(1200);
		secInfo2.setOfferQtty01(1300);
		
		List<Order> orders = orderCreator.createOrder(secInfo);
		List<Order> orders2 = orderCreator.createOrder(secInfo2);
		Assert.assertEquals(1, orders.size());	
		Assert.assertEquals(1, orders2.size());	
	}

	@Test
	public void testOrderCreatorCreateOrderWhenChangeOffer2() {
		SecInfo secInfo = new SecInfo();
		secInfo.setCode("VND");
		secInfo.setOfferPrice02(1200);
		secInfo.setOfferQtty02(1500);
		
		SecInfo secInfo2 = new SecInfo();
		secInfo2.setCode("VND");
		secInfo2.setOfferPrice02(1300);
		secInfo2.setOfferQtty02(1500);
		
		List<Order> orders = orderCreator.createOrder(secInfo);
		List<Order> orders2 = orderCreator.createOrder(secInfo2);
		Assert.assertEquals(1, orders.size());	
		Assert.assertEquals(1, orders2.size());	
	}

	@Test
	public void testOrderCreatorCreateOrderWhenChangeOffer3() {
		SecInfo secInfo = new SecInfo();
		secInfo.setCode("VND");
		secInfo.setOfferPrice03(1200);
		secInfo.setOfferQtty03(1500);
		
		SecInfo secInfo2 = new SecInfo();
		secInfo2.setCode("VND");
		secInfo2.setOfferPrice03(1300);
		secInfo2.setOfferQtty03(1500);
		
		List<Order> orders = orderCreator.createOrder(secInfo);
		List<Order> orders2 = orderCreator.createOrder(secInfo2);
		Assert.assertEquals(1, orders.size());	
		Assert.assertEquals(1, orders2.size());	
	}
	
	@Test
	public void testOrderCreatorCreateOrderWhenNoChangeBid1() {
		SecInfo secInfo = new SecInfo();
		secInfo.setCode("VND");
		secInfo.setBidPrice01(1200);
		secInfo.setBidQtty01(1500);
		
		SecInfo secInfo2 = new SecInfo();
		secInfo2.setCode("VND");
		secInfo2.setBidPrice01(1200);
		secInfo2.setBidQtty01(1500);
		
		List<Order> orders = orderCreator.createOrder(secInfo);
		List<Order> orders2 = orderCreator.createOrder(secInfo2);
		Assert.assertEquals(1, orders.size());	
		Assert.assertEquals(0, orders2.size());	
	}

	@Test
	public void testOrderCreatorCreateOrderWhenChangeBid1() {
		SecInfo secInfo = new SecInfo();
		secInfo.setCode("VND");
		secInfo.setBidPrice01(1200);
		secInfo.setBidQtty01(1500);
		
		SecInfo secInfo2 = new SecInfo();
		secInfo2.setCode("VND");
		secInfo2.setBidPrice01(1300);
		secInfo2.setBidQtty01(1500);
		
		List<Order> orders = orderCreator.createOrder(secInfo);
		List<Order> orders2 = orderCreator.createOrder(secInfo2);
		Assert.assertEquals(1, orders.size());	
		Assert.assertEquals(1, orders2.size());	
	}

	@Test
	public void testOrderCreatorCreateOrderWhenChangeBid2() {
		SecInfo secInfo = new SecInfo();
		secInfo.setCode("VND");
		secInfo.setBidPrice02(1200);
		secInfo.setBidQtty02(1500);
		
		SecInfo secInfo2 = new SecInfo();
		secInfo2.setCode("VND");
		secInfo2.setBidPrice02(1300);
		secInfo2.setBidQtty02(1500);
		
		List<Order> orders = orderCreator.createOrder(secInfo);
		List<Order> orders2 = orderCreator.createOrder(secInfo2);
		Assert.assertEquals(1, orders.size());	
		Assert.assertEquals(1, orders2.size());	
	}
	
	@Test
	public void testOrderCreatorCreateOrderWhenChangeBid3() {
		SecInfo secInfo = new SecInfo();
		secInfo.setCode("VND");
		secInfo.setBidPrice03(1200);
		secInfo.setBidQtty03(1500);
		
		SecInfo secInfo2 = new SecInfo();
		secInfo2.setCode("VND");
		secInfo2.setBidPrice03(1300);
		secInfo2.setBidQtty03(1500);
		
		List<Order> orders = orderCreator.createOrder(secInfo);
		List<Order> orders2 = orderCreator.createOrder(secInfo2);
		Assert.assertEquals(1, orders.size());	
		Assert.assertEquals(1, orders2.size());	
	}
}
