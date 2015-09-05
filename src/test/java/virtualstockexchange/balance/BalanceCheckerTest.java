package virtualstockexchange.balance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class BalanceCheckerTest {

	private Map<String, List<Balance>> balanceMap;
	private Balance balance;
	private Balance secBalance;
	private Balance secBalance1;
	private List<Balance> balances;
	private BalanceChecker checker;
	
	
	@Before
	public void initTest() {
		balance = new Balance();
		secBalance = new Balance();
		secBalance1 = new Balance();
		balances = new ArrayList<Balance>();
		checker = new BalanceChecker();
		balanceMap = checker.getAllBalance();
	}
	
	
	@Test 
	public void testHoldMoneyRunRight() throws BalanceException {
		balance.setAmount(200000);
		balances.add(balance);
		balanceMap.put("abc", balances);

		checker.holdMoney("abc", 1000);
		checker.holdMoney("abc", 2000);
		
		List<Balance> testBalances = balanceMap.get("abc");
		Balance testBalance = checker.getMoneyBalance(testBalances);
		Assert.assertEquals(3000, testBalance.getHold());
	}
	
	@Test (expected = BalanceException.class)
	public void throwsExceptionWhenHoldMoneyAccountNotExsit() throws BalanceException  {
		balance.setAmount(200000);
		balances.add(balance);
		balanceMap.put("abc", balances);

		checker.holdMoney("abc1", 1000);
		checker.holdMoney("abc", 2000);
	}
	
	@Test (expected = BalanceException.class)
	public void throwsExceptionWhenHoldMoneyNotEnoughMoney() throws BalanceException  {
		balance.setAmount(200000);
		balances.add(balance);
		balanceMap.put("abc", balances);
		
		checker.holdMoney("abc", 1000);
		checker.holdMoney("abc", 2000);
		checker.holdMoney("abc", 500000);
	}
	
	@Test
	public void testHoldSecurityRunRight() throws BalanceException {
		secBalance.setSecCode("VND");
		secBalance.setAmount(1000);
		balances.add(secBalance);
		
		secBalance1.setSecCode("SSI");
		secBalance1.setAmount(200);
		balances.add(secBalance1);
		
		balanceMap.put("abc", balances);
		
		checker.holdSecurity("abc", "VND", 100);
		checker.holdSecurity("abc", "VND", 300);

		checker.holdSecurity("abc", "SSI", 100);
		checker.holdSecurity("abc", "SSI", 100);
		
		Balance testSecBalance = checker.getSecBalance("abc", "VND");
		Assert.assertEquals(400, testSecBalance.getHold());
	}
	
	@Test (expected = BalanceException.class)
	public void throwsExceptionWhenHoldSecurityNotMatchSymbol() throws BalanceException {
		secBalance.setSecCode("VND");
		secBalance.setAmount(1000);
		balances.add(secBalance);
		balanceMap.put("abc", balances);
		
		checker.holdSecurity("abc", "VND", 100);
		checker.holdSecurity("abc", "SSI", 1000);
	}
	
	@Test (expected = BalanceException.class)
	public void throwsExceptionWhenHoldSecurityNotValidQuantity() throws BalanceException {
		secBalance.setSecCode("VND");
		secBalance.setAmount(1000);
		balances.add(secBalance);
		balanceMap.put("abc", balances);
		
		checker.holdSecurity("abc", "VND", 12000);
	}
	
	@Test (expected = BalanceException.class)
	public void throwsExceptionWhenHoldSecurityQuantitySmallerThan0() throws BalanceException {
		secBalance.setSecCode("VND");
		secBalance.setAmount(1000);
		balances.add(secBalance);
		balanceMap.put("abc", balances);
		
		checker.holdSecurity("abc", "VND", -12000);
	}
	
	@Test (expected = BalanceException.class)
	public void throwsExceptionWhenAddSecurityQuantitySmallerThan0() throws BalanceException {
		secBalance.setSecCode("VND");
		secBalance.setAmount(1000);
		
		balances.add(secBalance);
		balanceMap.put("abc", balances);
		
		checker.addSecurity("abc", "VND", -200);
		checker.addSecurity("abc", "VND", 300);
		checker.addSecurity("abc", "VND", 300);
	}
	
//	@Test
//	public void testAddSecurity() throws BalanceException {
//		secBalance.setSecCode("VND");
//		secBalance.setAmount(1000);
//		
//		balances.add(secBalance);
//		balanceMap.put("abc", balances);
//		
//		checker.addSecurity("abc", "VND", 200);
//		checker.addSecurity("abc", "VND", 300);
//		checker.addSecurity("abc", "VND", 300);
//		Balance testSecBalance = checker.getSecBalance("abc", "VND");
//
//		Assert.assertEquals(800, testSecBalance.getT0());
//	}
	
	@Test
	public void testAddMoney() throws BalanceException {
		balance.setAmount(200000);
		balances.add(balance);
		balanceMap.put("abc", balances);
		
		checker.addMoney("abc", 5000000);
		List<Balance> testBalances = balanceMap.get("abc");
		Balance testBalance = checker.getMoneyBalance(testBalances);
		Assert.assertEquals(5000000, testBalance.getT0());
	}
	
	@Test
	public void testCancelHoldMoneyRunRight() throws BalanceException {
		balance.setAmount(200000);
		balances.add(balance);
		balanceMap.put("abc", balances);
		
		checker.cancelHoldMoney("abc", 5000000);
		checker.cancelHoldMoney("abc", 5000000);
		List<Balance> testBalances = balanceMap.get("abc");
		Balance testBalance = checker.getMoneyBalance(testBalances);
		Assert.assertEquals(10200000, testBalance.getAmount());
	}
	
	@Test
	public void testCancelHoldSecurityRunRight() throws BalanceException {
		secBalance.setSecCode("VND");
		secBalance.setAmount(1000);
		
		balances.add(secBalance);
		balanceMap.put("abc", balances);
		
		checker.cancelHoldSecurity("abc", "VND", 200);
		checker.cancelHoldSecurity("abc", "VND", 300);
		Balance testSecBalance = checker.getSecBalance("abc", "VND");

		Assert.assertEquals(1500, testSecBalance.getAmount());
	}
	
	@Test
	public void testInitAccountRunRight() throws BalanceException {
		checker.initAccount("testAccount");
		List<Balance> testBalances = balanceMap.get("testAccount");
		Balance testBalance = checker.getMoneyBalance(testBalances);
		Assert.assertEquals(50000000, testBalance.getAmount());
	}
}
