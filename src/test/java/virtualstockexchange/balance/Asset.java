package virtualstockexchange.balance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import virtualstockexchange.exception.SystemException;

public class Asset {
	private static final Logger logger = Logger.getLogger(Asset.class);

	private Map<String, Long> moneyAccount = new HashMap<>();
	private Map<String, List<Securities>> securitiesAccount = new HashMap<>();
	List<Securities> secBalanceList = new ArrayList<Securities>();

	public long getMoneyByAccount(String account) {
		return moneyAccount.get(account);
	}

	public void initMoneyForAccount(String account, long money) {
		moneyAccount.put(account, money);
	}

	public void holdMoney(String account, long holdMoney) throws Exception {
		
		long currentMoney = getMoneyByAccount(account);
		if (holdMoney > currentMoney) {
			throw new Exception();
		}
		currentMoney -= holdMoney;
		moneyAccount.put(account, currentMoney);
	}

	public void addMoney(String account, long addMoney) {
		long currentMoney = getMoneyByAccount(account);
		currentMoney += addMoney;
		moneyAccount.put(account, currentMoney);
	}

	public void addSecurity(String account, String secCode, int quantity) throws Exception {
		//FIXME: add duplicate secCode shoud add more quantity
		if (accIsExistSecCode(account, secCode)) {
			Securities secBalance = getSecurity(account, secCode);
			secBalance.setQuantity(quantity+secBalance.getQuantity());
		} else {
			Securities newBalance = new Securities();
			newBalance.setSecCode(secCode);
			newBalance.setQuantity(quantity);
			secBalanceList.add(newBalance);
			securitiesAccount.put(account, secBalanceList);
		}
	}

	public List<Securities> getAllSecuritiesByAccount(String account) {
		List<Securities> secBalanceList = securitiesAccount.get(account);
		return secBalanceList;
	}

	public boolean accIsExistSecCode(String account, String secCode) {
		List<Securities> secBalanceList = getAllSecuritiesByAccount(account);
		for (Securities secBalance : safe(secBalanceList)) {
			if (secBalance.getSecCode().equals(secCode))
				return true;
		}
		return false;
	}

	public int getQuantitySecByAccount(String account, String secCode) throws Exception {
		if (!accIsExistSecCode(account, secCode)) 
			throw new SystemException("SEC_CODE_NOT_EXIST");
		List<Securities> secBalanceList = getAllSecuritiesByAccount(account);
		int quantity = 0;
		for (Securities secBalance : safe(secBalanceList)) {
			if (secBalance.getSecCode().equals(secCode))
				quantity = secBalance.getQuantity();
		}
		return quantity;
	}
	
	public Securities getSecurity (String account, String secCode) throws SystemException {
		List<Securities> secBalanceList = getAllSecuritiesByAccount(account);
		for (Securities secBalance : safe(secBalanceList)) {
			if (secBalance.getSecCode().equals(secCode))
				return secBalance;
		}
		throw new SystemException("SEC_CODE_NOT_EXIST");
	}
	
	public static <T> Iterable<T> safe(Iterable<T> iterable) {
	    return iterable == null ? Collections.<T>emptyList() : iterable;
	}

	public void holdSecurity(String account, String secCode, int quantity) throws SystemException {
		Securities secBalance = getSecurity(account, secCode);
		if (secBalance.getQuantity() < quantity)
			throw new SystemException("SECURITY_NOT_ENOUGH_QUANTITY");
		secBalance.setQuantity(secBalance.getQuantity() - quantity);
	}

	public void unHoldMoney(String account, long money) {
		long currentMoney = getMoneyByAccount(account);
		currentMoney += money;
		moneyAccount.put(account, currentMoney);
	}

	public void unHoldSecurity(String account, String secCode, int unHoldQuantity) throws SystemException {
		Securities secBalance = getSecurity(account, secCode);
		secBalance.setQuantity(unHoldQuantity+secBalance.getQuantity());
	}
	
	
}
