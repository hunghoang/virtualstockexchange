package virtualstockexchange.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import virtualstockexchange.balance.BalanceException;
import virtualstockexchange.matchengine.Market;
import virtualstockexchange.matchengine.Order;
import virtualstockexchange.matchengine.OrderExecution;

@RestController
@RequestMapping("/orders")
public class OrderController {
	
	private OrderExecution orderExecution;
	private Market market;
	
	@Autowired
	public OrderController(Market market, OrderExecution orderExecution) {
		this.market = market;
		this.orderExecution = orderExecution;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public @ResponseBody List<Order> getOrders() {
		return market.getAllOrders();
	}

	
	@RequestMapping(value = "/{orderid}", method = RequestMethod.GET)
	public @ResponseBody Object getOrder(@PathVariable String orderid,
			ModelMap model) {
		List<Order> orders = market.getAllOrders();
		for (Order order : orders) {
			if (order.getOrderId().equals(orderid)) {
				return order;
			}
		}
		return "Order not found: " + orderid;
	}
	
	@RequestMapping(value = "/unHoldAll", method = RequestMethod.POST)
	public @ResponseBody String unHoldAllOrders() {
		try {
			orderExecution.unHoldAllOrder();
		} catch (BalanceException e) {
			return e.getMessage();
		}
		return "OK";
	}
	
}