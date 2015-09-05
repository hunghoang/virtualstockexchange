package virtualstockexchange.rest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
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


	@RequestMapping(value = "/{account}/orders/new_order_requests", method = RequestMethod.POST)
	public Order placeOrder(@RequestBody NewOrderRequest orderRequest,
			@PathVariable("account") String account) {
		System.out.println("Place order " + orderRequest + " for account: " + account);
		int side = Side.BUY;
		if (orderRequest.getSide().equals("NS")) {
			side = Side.SELL;
		}
		Order order = new Order(account, orderRequest.getSymbol(),
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
	
	@RequestMapping(value = "/{account}/orders/{orderid}", method = RequestMethod.DELETE)
	public Order cancel(@PathVariable("account") String account, @PathVariable("orderid") String orderId) {
		System.out.println("cancel order for account: " + account + " with orderid " + orderId);
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
	
	@RequestMapping(value = "/{account}/orders", method = RequestMethod.GET)
	public @ResponseBody List<Order> getOrders(@PathVariable String account,
			ModelMap model) {
		List<Order> results = new ArrayList<Order>();
		List<Order> orders = market.getAllOrders();
		for (Order order : orders) {
			if (order.getAccount().equals(account)) {
				results.add(order);
			}
		}
		return results;
	}
}