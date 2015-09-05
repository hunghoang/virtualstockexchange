package virtualstockexchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BalanceChecker {
	long DEFAULT_MONEY = 50000000;
	Map<String, List<Balance>> balanceMap = new HashMap<String, List<Balance>>();
	
	public void holdMoney(String account, long money) throws BalanceException {
		if (!isValidAccount(account))
			throw new BalanceException("Account is not exsit: " + account);
		List<Balance> balances = balanceMap.get(account);
		Balance moneyBalance = getMoneyBalance(balances);
		if (!isEnoughMoney(money, moneyBalance))
			throw new BalanceException("Money too much: " + money +" - Not enough money of: " + account);
		
		moneyBalance.setHold(money);
	}

	public void addMoney(String account, long money) throws BalanceException {
		if (!isValidAccount(account))
			throw new BalanceException("Account is not exsit: " + account);
		if (money <= 0) 
			throw new BalanceException("Money smaller than 0 or equal 0: " + money);
		List<Balance> balances = balanceMap.get(account);
		Balance moneyBalance = getMoneyBalance(balances);
		moneyBalance.setT0(money);
	}
	
	public void holdSecurity(String account, String secCode, int quantity) throws BalanceException {
		if (!isValidAccount(account))
			throw new BalanceException("Account is not exsit: " + account);
		Balance secBalance = getSecBalance(account, secCode);
		if (quantity <= 0) 
			throw new BalanceException("Quantity smaller than 0 or equal 0: " + quantity);
		if (!isEnoughQuantity(quantity, secBalance))
			throw new BalanceException("Not enough quantity: " + account);
		
		secBalance.setSecCode(secCode);
		secBalance.setHold(quantity);
	}
	
	public void addSecurity(String account, String secCode, int quantity) throws BalanceException {
		if (quantity <= 0) 
			throw new BalanceException("Quantity smaller than 0 or equal 0: " + quantity);
		Balance secBalance = getSecBalance(account, secCode);
		secBalance.setT0(quantity);
	}
	
	public void cancelHoldMoney (String account, long money) throws BalanceException {
		if (!isValidAccount(account))
			throw new BalanceException("Account is not exsit: " + account);
		List<Balance> balances = balanceMap.get(account);
		Balance moneyBalance = getMoneyBalance(balances);
		moneyBalance.setAmount(moneyBalance.getAmount() + money);
	}
	
	public void cancelHoldSecurity (String account, String secCode, long quantity) throws BalanceException {
		if (!isValidAccount(account))
			throw new BalanceException("Account is not exsit: " + account);
		Balance secBalance = getSecBalance(account, secCode);
		if (quantity <= 0) 
			throw new BalanceException("Quantity smaller than 0 or equal 0: " + quantity);
		secBalance.setAmount(secBalance.getAmount() + quantity);
	}
	
	public void initAccount (String account) throws BalanceException {
		if (balanceMap.containsKey(account))
			throw new BalanceException("Account already exsit: " + account);
		Balance moneyBalance = new Balance();
		List<Balance> balances = new ArrayList<Balance>();
		moneyBalance.setAmount(DEFAULT_MONEY);
		balances.add(moneyBalance);
		balanceMap.put(account, balances);
	}
	
	private boolean isEnoughMoney(long money, Balance moneyBalance) {
		if (moneyBalance.getAmount() >= money)
			return true;
		return false;
	}
	
	private boolean isEnoughQuantity (int quantity, Balance secBalance) throws BalanceException {
		if (quantity <= 0) 
			throw new BalanceException("Quantity smaller than 0 or equal 0: " + quantity);
		if (secBalance.getAmount() >= quantity)
			return true;
		return false;
	}
	
	private boolean isValidAccount (String account) {
		if (balanceMap.containsKey(account))
			return true;
		return false;
	}
	
	public Map<String, List<Balance>> getAllBalance () {
		return balanceMap;
	}
	
	public Balance getMoneyBalance(List<Balance> balances) throws BalanceException {
		return balances.get(0);
	}
	
	public Balance getSecBalance(String account, String symbol) throws BalanceException {
		List<Balance> balances = balanceMap.get(account);
		for (Balance balance : balances) {
			if (symbol.equals(balance.getSecCode())) {
				return balance;
			}
		}
		throw new BalanceException("No balance for: " + symbol + " of " + account);
	}
	
	public void switchDay (int day, Balance balance) {
		switch (day) {
			case 0:
				balance.setT1(balance.getT0());
				balance.setT0(0);
			break;
			case 1:
				balance.setT2(balance.getT1());
				balance.setT1(0);
			break;
			case 2:
				balance.setT3(balance.getT2());
				balance.setT2(0);
			break;
			case 3:
				balance.setAmount(balance.getAmount() + balance.getT3());
				balance.setT3(0);
			break;
			default:
			break;
		}
	}
}
