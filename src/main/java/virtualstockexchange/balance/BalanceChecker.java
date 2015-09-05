package virtualstockexchange.balance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import virtualstockexchange.price.PriceService;

@Component
public class BalanceChecker {
	long DEFAULT_MONEY = 50000000;
	Map<String, List<Balance>> balanceMap = new HashMap<String, List<Balance>>();

	public BalanceChecker() {
		try {
			initBalanceMap();
		} catch (BalanceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void initAccount(String account) throws BalanceException {
		if (balanceMap.containsKey(account))
			throw new BalanceException("Account already exist: " + account);
		Balance moneyBalance = new Balance();
		List<Balance> balances = new ArrayList<Balance>();
		moneyBalance.setAmount(DEFAULT_MONEY);
		balances.add(moneyBalance);
		balanceMap.put(account, balances);
	}

	public Balance initSecBalance(String account, String symbol, int quantity)
			throws BalanceException {
		if (!isValidAccount(account))
			throw new BalanceException("Account is not exist: " + account);
		if (quantity <= 0)
			throw new BalanceException("Quantity smaller than 0 or equal 0: "
					+ quantity);
		Balance secBalance = new Balance();
		secBalance.setAmount(quantity);
		secBalance.setSecCode(symbol);

		return secBalance;
	}
	
	public void initBalanceMap() throws BalanceException {
		String user1 = "ha_nguyen";
		initAccount(user1);
		Balance secBalance1 = initSecBalance(user1, "VND", 2000);
		Balance secBalance2 = initSecBalance(user1, "SSI", 5000);
		Balance secBalance3 = initSecBalance(user1, "ACB", 1000);
		
		List<Balance> user1Balances = balanceMap.get(user1);
		user1Balances.add(secBalance1);
		user1Balances.add(secBalance2);
		user1Balances.add(secBalance3);
		balanceMap.put(user1, user1Balances);

		String user2 = "giang_vu";
		initAccount(user2);
		List<Balance> user2Balances = balanceMap.get(user2);
		Balance secBalance4 = initSecBalance(user2, "VND", 3000);
		Balance secBalance5 = initSecBalance(user2, "SHB", 7000);
		user2Balances.add(secBalance4);
		user2Balances.add(secBalance5);
		balanceMap.put(user2, user2Balances);
	}

	public void holdMoney(String account, long money) throws BalanceException {
		if (!isValidAccount(account))
			throw new BalanceException("Account is not exist: " + account);
		List<Balance> balances = balanceMap.get(account);
		Balance moneyBalance = getMoneyBalance(balances);
		if (!isEnoughMoney(money, moneyBalance))
			throw new BalanceException("Money too much: " + money
					+ " - Not enough money of: " + account);

		moneyBalance.setHold(money);
	}

	public void addMoney(String account, long money) throws BalanceException {
		if (!isValidAccount(account))
			throw new BalanceException("Account is not exist: " + account);
		if (money <= 0)
			throw new BalanceException("Money smaller than 0 or equal 0: "
					+ money);
		List<Balance> balances = balanceMap.get(account);
		Balance moneyBalance = getMoneyBalance(balances);
		moneyBalance.setT0(money);
	}

	public void holdSecurity(String account, String secCode, int quantity)
			throws BalanceException {
		if (!isValidAccount(account))
			throw new BalanceException("Account is not exist: " + account);
		Balance secBalance = getSecBalance(account, secCode);
		if (quantity <= 0)
			throw new BalanceException("Quantity smaller than 0 or equal 0: "
					+ quantity);
		if (!isEnoughQuantity(quantity, secBalance))
			throw new BalanceException("Not enough quantity: " + account);

		secBalance.setSecCode(secCode);
		secBalance.setHold(quantity);
	}

	public void addSecurity(String account, String secCode, int quantity)
			throws BalanceException {
		if (quantity <= 0)
			throw new BalanceException("Quantity smaller than 0 or equal 0: "
					+ quantity);
		if (isExistSymbol(account, secCode)) {
			Balance secBalance = getSecBalance(account, secCode);
			secBalance.setT0(quantity);
		} else {
			Balance secBalanceNew = new Balance();
			secBalanceNew.setT0(quantity);
			secBalanceNew.setSecCode(secCode);
			List<Balance> userBalance = balanceMap.get(account);
			userBalance.add(secBalanceNew);			
		}
	}
	
	public void cancelHoldMoney(String account, long money)
			throws BalanceException {
		if (!isValidAccount(account))
			throw new BalanceException("Account is not exist: " + account);
		List<Balance> balances = balanceMap.get(account);
		Balance moneyBalance = getMoneyBalance(balances);
		moneyBalance.setAmount(moneyBalance.getAmount() + money);
	}

	public void cancelHoldSecurity(String account, String secCode, long quantity)
			throws BalanceException {
		if (!isValidAccount(account))
			throw new BalanceException("Account is not exist: " + account);
		Balance secBalance = getSecBalance(account, secCode);
		if (quantity <= 0)
			throw new BalanceException("Quantity smaller than 0 or equal 0: "
					+ quantity);
		secBalance.setAmount(secBalance.getAmount() + quantity);
	}
	
	private boolean isExistSymbol(String account, String symbol) {
		List<Balance> balances = balanceMap.get(account);
		int length = balances.size();
		int count = 0;
		for (int i = 0; i < length; i++) {
			if (symbol.equals(balances.get(i).getSecCode())) {
				count++;
			}
		}
		if (count > 0)
			return true;
		
		return false;
	}

	private boolean isEnoughMoney(long money, Balance moneyBalance) {
		if (moneyBalance.getAmount() >= money)
			return true;
		return false;
	}

	private boolean isEnoughQuantity(int quantity, Balance secBalance)
			throws BalanceException {
		if (quantity <= 0)
			throw new BalanceException("Quantity smaller than 0 or equal 0: "
					+ quantity);
		if (secBalance.getAmount() >= quantity)
			return true;
		return false;
	}

	private boolean isValidAccount(String account) {
		if (balanceMap.containsKey(account))
			return true;
		return false;
	}

	public Map<String, List<Balance>> getAllBalance() {
		return balanceMap;
	}

	public Balance getMoneyBalance(List<Balance> balances)
			throws BalanceException {
		return balances.get(0);
	}

	public Balance getSecBalance(String account, String symbol)
			throws BalanceException {
		List<Balance> balances = balanceMap.get(account);
		for (Balance balance : balances) {
			if (symbol.equals(balance.getSecCode())) {
				return balance;
			}
		}
		throw new BalanceException("No balance for: " + symbol + " of " + account);
	}
	
	public Balance getMoneyBalance(String account) {
		List<Balance> balances = balanceMap.get(account);
		if (balances.size() > 0) {
			return balances.get(0);
		}
		return null;
	}

	public List<Balance> getBalances(String account) {		
		return balanceMap.get(account);
	}
	
	public long getTotalAssets(String account) {
		Balance moneyBalance = getMoneyBalance(account);
		long currentMoney = 0;
		currentMoney += moneyBalance.getAmount() + moneyBalance.getT0()
				 + moneyBalance.getT1() + moneyBalance.getT2()
				 - moneyBalance.getHold();
		
		List<Balance> balances = balanceMap.get(account);
		PriceService priceService = new PriceService();

		for (Balance balance : balances) {
			if (balance.getSecCode() != null) {
				currentMoney += (balance.getAmount() + balance.getT0() + balance.getT1() + balance.getT2() - balance.getHold()) * priceService.getPrice(balance.getSecCode());
			}
		}
		return currentMoney;
	}

	public void nextMoney(String account) {
		Balance balance = getMoneyBalance(account);
		switchDay(balance);
	}
	
	public void nextSecurity(String account, String symbol) throws BalanceException {
		Balance balance = getSecBalance(account, symbol);
		switchDay(balance);
	}
	
	public void switchDay (Balance balance) {
		if (balance.getT0() > 0) {
			balance.setT1(balance.getT0());
			balance.setT0(0);
		} else if (balance.getT1() > 0) {
			balance.setT2(balance.getT1());
			balance.setT1(0);
		} else if (balance.getT2() > 0) {
			balance.setAmount(balance.getAmount() + balance.getT2());
			balance.setT2(0);
		}
	}
}