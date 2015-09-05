package virtualstockexchange.matchengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import vn.com.vndirect.priceservice.datamodel.SecInfo;

@Component
public class OrderCreator {
	private Map<String, SecInfo> secInfoSnapShot = new HashMap<String, SecInfo>();

	public List<Order> createOrder(SecInfo secInfo) {
		List<Order> orders = new ArrayList<Order>();
		SecInfo lastSecInfo = secInfoSnapShot.get(secInfo.getCode());
		
		createOrderForBid1(orders, lastSecInfo, secInfo);
		createOrderForBid2(orders, lastSecInfo, secInfo);
		createOrderForBid3(orders, lastSecInfo, secInfo);
		createOrderForOffer1(orders, lastSecInfo, secInfo);
		createOrderForOffer2(orders, lastSecInfo, secInfo);
		createOrderForOffer3(orders, lastSecInfo, secInfo);
		saveSnapshot(secInfo);
		return orders;
	}
	
	private void saveSnapshot(SecInfo secInfo) {
		secInfoSnapShot.put(secInfo.getCode(), secInfo);
		
	}
	private void createOrderForBid1(List<Order> orders, SecInfo lastSecInfo, SecInfo secInfo) {
		if (secInfo.getBidPrice01() > 0) {
			if (bid01Change(lastSecInfo, secInfo)) {
				Order order = new Order(secInfo.getCode(), new Double(
						secInfo.getBidPrice01() * 1000).longValue(), new Double(
								secInfo.getBidQtty01() * 10).intValue(), Side.BUY);
				orders.add(order);
			}
		}
	}

	private boolean bid01Change(SecInfo lastSecInfo, SecInfo secInfo) {
		return lastSecInfo == null || lastSecInfo.getBidPrice01() != secInfo.getBidPrice01() || lastSecInfo.getBidQtty01() != secInfo.getBidQtty01();
	}



	private void createOrderForBid2(List<Order> orders, SecInfo lastSecInfo, SecInfo secInfo) {
		if (secInfo.getBidPrice02() > 0) {
			if (bid02Change(lastSecInfo, secInfo)) {
				Order order = new Order(secInfo.getCode(), new Double(
						secInfo.getBidPrice02() * 1000).longValue(), new Double(
								secInfo.getBidQtty02() * 10).intValue(), Side.BUY);
				orders.add(order);
			}
		}
	}

	private boolean bid02Change(SecInfo lastSecInfo, SecInfo secInfo) {
		return lastSecInfo == null || lastSecInfo.getBidPrice02() != secInfo.getBidPrice02() || lastSecInfo.getBidQtty02() != secInfo.getBidQtty02();
	}

	private void createOrderForBid3(List<Order> orders, SecInfo lastSecInfo, SecInfo secInfo) {
		if (secInfo.getBidPrice03() > 0) {
			if (bid03Change(lastSecInfo, secInfo)) {
				Order order = new Order(secInfo.getCode(), new Double(
						secInfo.getBidPrice03() * 1000).longValue(), new Double(
								secInfo.getBidQtty03() * 10).intValue(), Side.BUY);
				orders.add(order);
			}
		}
	}
	
	private boolean bid03Change(SecInfo lastSecInfo, SecInfo secInfo) {
		return lastSecInfo == null || lastSecInfo.getBidPrice03() != secInfo.getBidPrice03() || lastSecInfo.getBidQtty03() != secInfo.getBidQtty03();
	}

	
	private void createOrderForOffer1(List<Order> orders, SecInfo lastSecInfo, SecInfo secInfo) {
		if (secInfo.getOfferPrice01() > 0) {
			if (offer01Change(lastSecInfo, secInfo)) {
				Order order = new Order(secInfo.getCode(), new Double(
						secInfo.getOfferPrice01() * 1000).longValue(), new Double(
								secInfo.getOfferQtty01() * 10).intValue(), Side.SELL);
				orders.add(order);
			}
		}
	}

	private boolean offer01Change(SecInfo lastSecInfo, SecInfo secInfo) {
		return lastSecInfo == null || lastSecInfo.getOfferPrice01() != secInfo.getOfferPrice01() || lastSecInfo.getOfferQtty01() != secInfo.getOfferQtty01();
	}
	
	private void createOrderForOffer2(List<Order> orders, SecInfo lastSecInfo, SecInfo secInfo) {
		if (secInfo.getOfferPrice02() > 0) {
			if (offer02Change(lastSecInfo, secInfo)) {
				Order order = new Order(secInfo.getCode(), new Double(
						secInfo.getOfferPrice02() * 1000).longValue(), new Double(
								secInfo.getOfferQtty02() * 10).intValue(), Side.SELL);
				orders.add(order);
			}
		}
	}


	private boolean offer02Change(SecInfo lastSecInfo, SecInfo secInfo) {
		return lastSecInfo == null || lastSecInfo.getOfferPrice02() != secInfo.getOfferPrice02() || lastSecInfo.getOfferQtty02() != secInfo.getOfferQtty02();
	}
	
	
	private void createOrderForOffer3(List<Order> orders, SecInfo lastSecInfo, SecInfo secInfo) {
		if (secInfo.getOfferPrice03() > 0) {
			if (offer03Change(lastSecInfo, secInfo)) {
				Order order = new Order(secInfo.getCode(), new Double(
						secInfo.getOfferPrice03() * 1000).longValue(), new Double(
								secInfo.getOfferQtty03() * 10).intValue(), Side.SELL);
				orders.add(order);
			}
		}
	}
	
	private boolean offer03Change(SecInfo lastSecInfo, SecInfo secInfo) {
		return lastSecInfo == null || lastSecInfo.getOfferPrice03() != secInfo.getOfferPrice03() || lastSecInfo.getOfferQtty03() != secInfo.getOfferQtty03();
	}
	
}
