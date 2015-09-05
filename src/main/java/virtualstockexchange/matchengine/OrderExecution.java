package virtualstockexchange.matchengine;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import virtualstockexchange.balance.BalanceChecker;
import virtualstockexchange.balance.BalanceException;

@Component
public class OrderExecution {

	private static final Logger logger = Logger.getLogger(OrderExecution.class);
	
	private BalanceChecker balanceChecker;

	private Market market;

	@Autowired
	public OrderExecution(BalanceChecker balanceChecker, Market market) {
		this.balanceChecker = balanceChecker;
		this.market = market;
	}

	public List<Order> executeOrder(Order order) throws BalanceException {
		if (order.getSide() == Side.BUY) {
			long hold = order.getPrice() * order.getQuantity();
			balanceChecker.holdMoney(order.getAccount(), hold);
		} else {
			balanceChecker.holdSecurity(order.getAccount(), order.getSymbol(),
					order.getQuantity());
		}
		List<Order> orders = market.place(order);
		fillOrder(orders);
		return orders;
	}

	public void fillOrder(List<Order> orders) throws BalanceException {
		for (Order result : orders) {
			if (result.getAccount() != null) {
				if (result.getLastMatchQuantity() > 0) {
					if (result.getSide() == Side.BUY) {
						logger.info("add security: " + result.getAccount() + " - " +  result.getSymbol() + " - " +  result.getLastMatchQuantity());
						balanceChecker.addSecurity(result.getAccount(), result.getSymbol(), result.getLastMatchQuantity());
					} else {
						long money = result.getMatchPrice() * result.getLastMatchQuantity();
						logger.info("add money: " + result.getAccount() + " - " +  money);
						balanceChecker.addMoney(result.getAccount(), money);
					}
				}
			}
		}
	}

	public Order executeCancelledOrder(Order order) throws BalanceException {
		Order cancelledOrder = market.cancel(order.getOrderId());
		if (cancelledOrder != null) {
			if (cancelledOrder.getSide() == Side.BUY) {
				long hold = cancelledOrder.getPrice()
						* cancelledOrder.getRemainQuantity();
				balanceChecker.holdMoney(cancelledOrder.getAccount(), -hold);
			} else {
				balanceChecker.holdSecurity(cancelledOrder.getAccount(),
						cancelledOrder.getSymbol(),
						-cancelledOrder.getRemainQuantity());
			}
		}
		return cancelledOrder;
	}

}
