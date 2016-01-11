package vn.com.vndirect.matchingengine;

import java.util.HashMap;
import java.util.Map;

public class PriceService {

	private Map<String, Price> prices = new HashMap<String, Price>();
	
	public void setPrice(String symbol, long ceiling, long floor) {
		prices.put(symbol, new Price(ceiling, floor));
	}
	
	public Price getPrice(String symbol) {
		return prices.get(symbol);
	}
	
}
	
