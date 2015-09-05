package virtualstockexchange.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import virtualstockexchange.matchengine.Market;
import virtualstockexchange.matchengine.Order;

@RestController
@RequestMapping("/orders")
public class OrderController {
	
	private Market market;
	
	@Autowired
	public OrderController(Market market) {
		this.market = market;
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
}