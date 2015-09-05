package virtualstockexchange.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import virtualstockexchange.balance.BalanceException;
import virtualstockexchange.matchengine.Market;
import virtualstockexchange.matchengine.Order;
import virtualstockexchange.matchengine.OrderExecution;
import virtualstockexchange.matchengine.Side;

@RestController
@RequestMapping("/accounts")
public class AccountOrderController {

	private Market market;
	
	private OrderExecution orderExecution;

	@Autowired
	public AccountOrderController(Market market, OrderExecution orderExecution) {
		this.market = market;
		this.orderExecution = orderExecution;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public @ResponseBody List<Order> getOrders() {
		return market.getAllOrders();
	}

	@RequestMapping(value = "/{accountNumber}/orders/new_order_requests", method = RequestMethod.POST)
	public Order placeOrder(@RequestBody NewOrderRequest orderRequest,
			@PathVariable("accountNumber") String accountNumber) {
		System.out.println("Place order " + orderRequest + " for account: " + accountNumber);
		int side = Side.BUY;
		if (orderRequest.getSide() == "NS") {
			side = Side.SELL;
		}
		Order order = new Order(accountNumber, orderRequest.getSymbol(),
				orderRequest.getPrice(), orderRequest.getQuantity(), side);
		String orderId = order.getOrderId();
		List<Order> orders;
		try {
			orders = orderExecution.executeOrder(order);
		} catch (BalanceException e) {
			Order error = Order.getDefaultOrder();
			error.setStatus("Tai khoan khong du de dat lenh");
			return error;
		}

		for (Order result : orders) {
			if (result.getOrderId().equals(orderId)) {
				return result;
			}
		}
		return order;
	}
	
	@RequestMapping(value = "/{accountNumber}/orders/{orderid}", method = RequestMethod.DELETE)
	public Order cancel(@PathVariable("accountNumber") String accountNumber, @PathVariable("orderid") String orderId) {
		System.out.println("cancel order for account: " + accountNumber + " with orderid " + orderId);
		Order order = new Order("", 0, 0 ,0);
		order.setOrderId(orderId);
		try {
			return orderExecution.executeCancelledOrder(order);
		} catch (BalanceException e) {
			Order error = Order.getDefaultOrder();
			error.setStatus("Co loi khi dat lenh");
			return error;
		}
	}
}