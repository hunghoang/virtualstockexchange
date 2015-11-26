package virtualstockexchange.balance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import virtualstockexchange.exception.ExceptionCode;
import virtualstockexchange.exception.SystemException;

public class Asset {
	private static final Logger logger = Logger.getLogger(Asset.class);

	private Map<String, Long> moneyAccount = new HashMap<>();
	private Map<String, List<Security>> securitiesAccount = new HashMap<>();
	List<Security> secBalanceList = new ArrayList<Security>();

	public long getMoneyByAccount(String account) throws SystemException {
		if (!isAccountExist(account)) {
			throw new SystemException(ExceptionCode.ACCOUNT_NOT_EXIST.code(), ExceptionCode.ACCOUNT_NOT_EXIST.message());
		}
		return moneyAccount.get(account);
	}
	
	public boolean isAccountExist(String account) {
		if (moneyAccount.containsKey(account)) {
			return true;
		}
		return false;
	}

	public void initMoneyForAccount(String account, long money) {
		if (moneyAccount.containsKey(account)) {
			throw new RuntimeException();
		}
		moneyAccount.put(account, money);
	}

	public void holdMoney(String account, long holdMoney) throws SystemException {
		long currentMoney = getMoneyByAccount(account);
		if (holdMoney > currentMoney) {
			throw new SystemException(ExceptionCode.MONEY_NOT_ENOUGH.code(), ExceptionCode.MONEY_NOT_ENOUGH.message());
		}
		currentMoney -= holdMoney;
		moneyAccount.put(account, currentMoney);
	}

	public void addMoney(String account, long addMoney) throws SystemException {
		long currentMoney = getMoneyByAccount(account);
		currentMoney += addMoney;
		moneyAccount.put(account, currentMoney);
	}

	public void addSecurity(String account, String secCode, int quantity) throws Exception {
		//FIXME: add duplicate secCode shoud add more quantity
		if (accIsExistSecCode(account, secCode)) {
			Security secBalance = getSecurity(account, secCode);
			secBalance.setQuantity(quantity+secBalance.getQuantity());
		} else {
			Security newBalance = new Security();
			newBalance.setSecCode(secCode);
			newBalance.setQuantity(quantity);
			secBalanceList.add(newBalance);
			securitiesAccount.put(account, secBalanceList);
		}
	}

	public List<Security> getAllSecuritiesByAccount(String account) throws SystemException {
		if (!isAccountExist(account)) {
			throw new SystemException(ExceptionCode.ACCOUNT_NOT_EXIST.code(), ExceptionCode.ACCOUNT_NOT_EXIST.message());
		}
		List<Security> secBalanceList = new ArrayList<Security>();
		secBalanceList = securitiesAccount.get(account);
		return secBalanceList;
	}

	public boolean accIsExistSecCode(String account, String secCode) throws SystemException {
		List<Security> secBalanceList = getAllSecuritiesByAccount(account);
		if (secBalanceList == null) {
			return false;
		} else {
			for (Security secBalance : secBalanceList) {
				if (secBalance.getSecCode().equals(secCode))
					return true;
			}
		}
		return false;
	}

	public int getQuantitySecByAccount(String account, String secCode) throws Exception {
		if (!accIsExistSecCode(account, secCode)) 
			throw new SystemException(ExceptionCode.SEC_CODE_NOT_EXIST.code(), ExceptionCode.SEC_CODE_NOT_EXIST.message());
		List<Security> secBalanceList = getAllSecuritiesByAccount(account);
		int quantity = 0;
		for (Security secBalance : secBalanceList) {
			if (secBalance.getSecCode().equals(secCode))
				quantity = secBalance.getQuantity();
		}
		return quantity;
	}
	
	public Security getSecurity (String account, String secCode) throws SystemException {
		if (!accIsExistSecCode(account, secCode)) 
			throw new SystemException(ExceptionCode.SEC_CODE_NOT_EXIST.code(), ExceptionCode.SEC_CODE_NOT_EXIST.message());
		List<Security> secBalanceList = getAllSecuritiesByAccount(account);
		for (Security secBalance : secBalanceList) {
			if (secBalance.getSecCode().equals(secCode))
				return secBalance;
		}
		throw new SystemException(ExceptionCode.SEC_CODE_NOT_EXIST.code(), ExceptionCode.SEC_CODE_NOT_EXIST.message());
	}

	public void holdSecurity(String account, String secCode, int quantity) throws SystemException {
		Security secBalance = getSecurity(account, secCode);
		if (secBalance.getQuantity() < quantity)
			throw new SystemException(ExceptionCode.SECURITY_NOT_ENOUGH_QUANTITY.code(), ExceptionCode.SECURITY_NOT_ENOUGH_QUANTITY.message());
		secBalance.setQuantity(secBalance.getQuantity() - quantity);
	}

	public void unHoldMoney(String account, long money) throws SystemException {
		long currentMoney = getMoneyByAccount(account);
		currentMoney += money;
		moneyAccount.put(account, currentMoney);
	}

	public void unHoldSecurity(String account, String secCode, int unHoldQuantity) throws SystemException {
		Security secBalance = getSecurity(account, secCode);
		secBalance.setQuantity(unHoldQuantity+secBalance.getQuantity());
	}
	
	
}
