package virtualstockexchange.price;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import vn.com.vndirect.priceservice.datamodel.SecInfo;

@Component
public class PriceService {

	private Map<String, SecInfo> secInfoMap = new HashMap<String, SecInfo>();
	
	public void updateSec(SecInfo sec) {
		secInfoMap.put(sec.getCode(), sec);
	}
	
	public long getPrice(String symbol) {
		SecInfo sec = secInfoMap.get(symbol);
		if (sec != null) {
			if (sec.getMatchPrice() > 0) {
				return new Double(sec.getMatchPrice() * 1000).longValue();
			}
			return new Double(sec.getBasicPrice() * 1000).longValue();
		}
		return 0;
				
	}
}
