package virtualstockexchange.matchengine;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderExecution {

	private BalanceChecker balanceChecker;

	private Market market;

	@Autowired
	public OrderExecution(BalanceChecker balanceChecker, Market market) {
		this.balanceChecker = balanceChecker;
		this.market = market;
	}

	public List<Order> executeOrder(Order order) {
		try {
			if (order.getSide() == Side.BUY) {
				long hold = order.getPrice() * order.getQuantity();
				balanceChecker.holdMoney(order.getAccount(), hold);
			} else {
				balanceChecker.holdSecurity(order.getAccount(),
						order.getSymbol(), order.getQuantity());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return market.place(order);
	}

	public Order executeCancelledOrder(Order order) {
		Order cancelledOrder = market.cancel(order.getOrderId());
		if (cancelledOrder != null) {
			if (cancelledOrder.getSide() == Side.BUY) {
				long hold = cancelledOrder.getPrice() * cancelledOrder.getRemainQuantity();
				balanceChecker.holdMoney(cancelledOrder.getAccount(), -hold);
			} else {
				balanceChecker.holdSecurity(cancelledOrder.getAccount(),
						cancelledOrder.getSymbol(), -cancelledOrder.getRemainQuantity());
			}
		}
		return cancelledOrder;
	}

}
