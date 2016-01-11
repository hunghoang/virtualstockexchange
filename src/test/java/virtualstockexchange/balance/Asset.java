package virtualstockexchange.balance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import virtualstockexchange.exception.ExceptionCode;
import virtualstockexchange.exception.SystemException;

public class Asset {
	private static final Logger logger = Logger.getLogger(Asset.class);

	private Map<String, Money> moneyAccount = new HashMap<>();
	private Map<String, List<Security>> securitiesAccount = new HashMap<>();
	List<Security> secBalanceList = new ArrayList<Security>();
	Money moneyObj = new Money();

	public Money getMoney(String account) throws SystemException {
		if (!isAccountExist(account)) {
			throw new SystemException(ExceptionCode.ACCOUNT_NOT_EXIST.code(), ExceptionCode.ACCOUNT_NOT_EXIST.message());
		}
		moneyObj = moneyAccount.get(account);
		return moneyObj;
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
		Money moneyObj = new Money();
		moneyObj.setMoney(money);
		moneyAccount.put(account, moneyObj);
	}

	public void holdMoney(String account, long holdMoney) throws SystemException {
		moneyObj = getMoney(account);
		long currentMoney = moneyObj.getMoney();
		if (holdMoney > currentMoney) {
			throw new SystemException(ExceptionCode.MONEY_NOT_ENOUGH.code(), ExceptionCode.MONEY_NOT_ENOUGH.message());
		}
		currentMoney -= holdMoney;
		moneyObj.setMoney(currentMoney);
        moneyObj.setHold(holdMoney);

		moneyAccount.put(account, moneyObj);
	}

	public void unHoldMoney(String account, long money) throws SystemException {
		moneyObj = getMoney(account);
		long currentMoney = moneyObj.getMoney();
		currentMoney += money;
		moneyObj.setHold(moneyObj.getHold() - money);
		moneyObj.setMoney(currentMoney);
		moneyAccount.put(account, moneyObj);
	}
	
	public void addMoney(String account, long addMoney) throws SystemException {
		moneyObj = getMoney(account);
		long currentMoney = moneyObj.getT0();
		currentMoney += addMoney;
		moneyObj.setT0(currentMoney);
		moneyAccount.put(account, moneyObj);
	}

	public void addSecurity(String account, String secCode, int quantity) throws Exception {
		//FIXME: add duplicate secCode shoud add more quantity
		if (accIsExistSecCode(account, secCode)) {
			Security secBalance = getSecurity(account, secCode);
			secBalance.setT0(quantity+secBalance.getT0());
		} else {
			Security newBalance = new Security();
			newBalance.setSecCode(secCode);
			newBalance.setT0(quantity);
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
		secBalance.setHold(quantity+secBalance.getHold());
	}

	public void unHoldSecurity(String account, String secCode, int unHoldQuantity) throws SystemException {
		Security secBalance = getSecurity(account, secCode);
		if (secBalance.getHold() > 0) {
			secBalance.setHold(secBalance.getHold() - unHoldQuantity);
		}
		//FIXME : doan nay cam giac so ho o cho neu hold >0 nhung hold < unhold thi se bi am??? - testUnholdSecurityDuplicateShouldRunRight()
		secBalance.setQuantity(unHoldQuantity+secBalance.getQuantity());
	}
	
	public void nextSecurity (String account, String secCode) throws SystemException {
		if (!isAccountExist(account)) {
			throw new SystemException(ExceptionCode.ACCOUNT_NOT_EXIST.code(), ExceptionCode.ACCOUNT_NOT_EXIST.message());
		}
		if (!accIsExistSecCode(account, secCode)) 
			throw new SystemException(ExceptionCode.SEC_CODE_NOT_EXIST.code(), ExceptionCode.SEC_CODE_NOT_EXIST.message());
		Security security = getSecurity(account, secCode);
		if (security.getT2() > 0) {
			security.setQuantity(security.getQuantity() + security.getT2());
			security.setT2(0);
		}		
		if (security.getT1() > 0) {
			security.setT2(security.getT1());
			security.setT1(0);
		}
		if (security.getT0() > 0) {
			security.setT1(security.getT0());
			security.setT0(0);
		}
	}
	
	//for test
	//TODO: co can dung mock ko?
	public void initSecurity (String account, String secCode, int quantity) {
		Security security = new Security();
		security.setQuantity(quantity);
		security.setSecCode(secCode);
		secBalanceList.add(security); 
		securitiesAccount.put(account, secBalanceList);
	}

	public void nextMoney(String account) throws SystemException {
		if (!isAccountExist(account)) {
			throw new SystemException(ExceptionCode.ACCOUNT_NOT_EXIST.code(), ExceptionCode.ACCOUNT_NOT_EXIST.message());
		}
		Money money = getMoney(account);
		if (money.getT2() > 0) {
			money.setMoney(money.getMoney() + money.getT2());
			money.setT2(0);
		}
		if (money.getT1() > 0) {
			money.setT2(money.getT1());
			money.setT1(0);
		}
		if (money.getT0() > 0) {
			money.setT1(money.getT0());
			money.setT0(0);
		}		
	}	
}
