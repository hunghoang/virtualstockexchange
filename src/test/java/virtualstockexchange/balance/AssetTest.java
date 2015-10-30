package virtualstockexchange.balance;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import virtualstockexchange.exception.ExceptionCode;
import virtualstockexchange.exception.SystemException;

public class AssetTest {
	
	private Asset asset = new Asset();
	
	@Test
	public void testInitMoney() {
		verifyInitMoneyForAccount(10000000);
		verifyInitMoneyForAccount(11000000);
	}

	private void verifyInitMoneyForAccount(long expectedMoney) {
		asset.initMoneyForAccount("1234", expectedMoney);
		long moneyOfAccount = asset.getMoneyByAccount("1234");
		assertEquals(expectedMoney, moneyOfAccount);
	}

	@Test
	public void testDuplicateInitMoney() {
		asset.initMoneyForAccount("1234", 1000);
		asset.initMoneyForAccount("1234", 2000);
		long moneyOfAccount = asset.getMoneyByAccount("1234");
		assertEquals(2000, moneyOfAccount);
	}
	
	@Test
	public void testInitMoneyForManyAccountShouldSetMoneyForRightAccount() {
		long expectedMoneyOfAcc1 = 1000;
		long expectedMoneyOfAcc2 = 2000;
		asset.initMoneyForAccount("1234", expectedMoneyOfAcc1);
		asset.initMoneyForAccount("5678", expectedMoneyOfAcc2);
		long moneyOfAcc1 = asset.getMoneyByAccount("1234");
		long moneyOfAcc2 = asset.getMoneyByAccount("5678");
		assertEquals(moneyOfAcc1, expectedMoneyOfAcc1);
		assertEquals(moneyOfAcc2, expectedMoneyOfAcc2);
	}
	
	@Test
	public void testHoldMoneyShouldHoldRightValue() throws Exception {
		asset.initMoneyForAccount("1234", 2500);
		long moneyBeforeHold = asset.getMoneyByAccount("1234");
		long hold = 1000;
		asset.holdMoney("1234", hold);
		long money = asset.getMoneyByAccount("1234");
		assertEquals(moneyBeforeHold, money + hold);
	}
	
	@Test(expected = Exception.class)
	public void testHoldMoneyWithHoldMoneyBiggerThanCurrentMoney() throws Exception {
		asset.initMoneyForAccount("1234", 2500);
		long hold = 3000;
		asset.holdMoney("1234", hold);
	}
	
	@Test
	public void testHoldMoneyWithHoldMoneyEqualCurrentMoney() throws Exception {
		long initMoney = 2500;
		asset.initMoneyForAccount("1234", initMoney);
		long moneyBeforeHold = asset.getMoneyByAccount("1234");
		long hold = initMoney;
		asset.holdMoney("1234", hold);
		long money = asset.getMoneyByAccount("1234");
		assertEquals(0, money);
		assertEquals(0, moneyBeforeHold - hold);
	}
	
	@Test(expected = Exception.class)
	public void testHoldMoneyWithWrongAccountShouldFail() throws Exception {
		long initMoney = 2500;
		asset.initMoneyForAccount("1234", initMoney);
		long hold = 1500;
		asset.holdMoney("8989", hold);
	}
	
	@Test(expected = Exception.class)
	public void testHoldMoneyWithHoldMoneyTwiceTimeBiggerThanCurrentMoney () throws Exception {
		long initMoney = 2500;
		asset.initMoneyForAccount("1234", initMoney);
		long hold1 = 1000;
		long hold2 = 2000;
		asset.holdMoney("1234", hold1);
		asset.holdMoney("1234", hold2);
	}
	
	
	@Test
	public void testUnHoldMoneyShouldRunRight () {
		long initMoney = 2500;
		asset.initMoneyForAccount("1234", initMoney);
		long moneyBeforeUnHold = asset.getMoneyByAccount("1234");
		long unHoldMoney = 2000;
		asset.unHoldMoney("1234", unHoldMoney);
		assertEquals(asset.getMoneyByAccount("1234"), unHoldMoney+moneyBeforeUnHold);
	}
	
	@Test(expected = Exception.class)
	public void testUnHoldMoneyWithAccountNotExistShouldFail() {
		long initMoney = 2500;
		asset.initMoneyForAccount("1234", initMoney);

		long unHoldMoney = 1000;
		asset.unHoldMoney("8989", unHoldMoney);
	}
	
	@Test
	public void testAddMoneyShouldRight() {
		long initMoney = 2500;
		asset.initMoneyForAccount("1234", initMoney);

		long addMoney = 1000;
		long moneyBeforeAdd = asset.getMoneyByAccount("1234");
		asset.addMoney("1234", addMoney);
		assertEquals(asset.getMoneyByAccount("1234"), moneyBeforeAdd + addMoney);
	}
	
	@Test(expected = Exception.class)
	public void testAddMoneyWithAccountNotExistShouldFail() {
		long initMoney = 2500;
		asset.initMoneyForAccount("1234", initMoney);

		long addMoney = 1000;
		asset.addMoney("8989", addMoney);
	}
	
	@Test
	public void testAddSecurities () throws Exception {
		String secCode = "ACB";
		int quantity = 200;
		
		asset.addSecurity("1234", secCode, quantity);
		
		List<Securities> secBalanceList = asset.getAllSecuritiesByAccount("1234");
		String secResult = "null";
		for (Securities secBalance : secBalanceList) {
			if (secBalance.getSecCode().equals(secCode))
				secResult = secBalance.getSecCode();
		}
		
		assertEquals(true, asset.accIsExistSecCode("1234", "ACB"));
		assertEquals(false, asset.accIsExistSecCode("1234", "VND"));
		assertEquals(false, asset.accIsExistSecCode("1234", "ACBD"));
		assertEquals(secCode, secResult);
		assertEquals(quantity, asset.getQuantitySecByAccount("1234", "ACB"));
		try {
			asset.getQuantitySecByAccount("1234", "ACBB");
		} catch (SystemException e) {
			assertEquals(ExceptionCode.SEC_CODE_NOT_EXIST.code(), e.getCode());
		}
	}
	
	@Test
	public void testAddSecuritiesWithListSec() throws Exception {
		String secCode1 = "ACB";
		int quantity1 = 200;
		asset.addSecurity("1234", secCode1, quantity1);
		
		String secCode2 = "VND";
		int quantity2 = 500;
		asset.addSecurity("1234", secCode2, quantity2);
		
		List<Securities> secBalanceList = asset.getAllSecuritiesByAccount("1234");
		assertEquals(2, secBalanceList.size());
		assertEquals(true, asset.accIsExistSecCode("1234", "ACB"));
		assertEquals(true, asset.accIsExistSecCode("1234", "VND"));
		assertEquals(quantity1, asset.getQuantitySecByAccount("1234", "ACB"));
		assertEquals(quantity2, asset.getQuantitySecByAccount("1234", "VND"));
	}
	
	@Test
	public void testAddSecuritiesWithDuplicateSecCodeShouldBeAddMoreQuantity () throws Exception {
		String secCode1 = "ACB";
		int quantity1 = 200;
		int quantityNew =  150;
		asset.addSecurity("1234", secCode1, quantity1);
		asset.addSecurity("1234", secCode1, quantityNew);
		
		String secCode2 = "VND";
		int quantity2 = 500;
		asset.addSecurity("1234", secCode2, quantity2);
		
		List<Securities> secBalanceList = asset.getAllSecuritiesByAccount("1234");
		assertEquals(2, secBalanceList.size());
		assertEquals(quantity1 + quantityNew, asset.getQuantitySecByAccount("1234", "ACB"));
		assertEquals(quantity2, asset.getQuantitySecByAccount("1234", "VND"));
	}

	@Test
	public void testAddSecurityWithAccountNotInitMoneyYet () throws Exception {
		String secCode2 = "VND";
		int quantity2 = 500;
		asset.addSecurity("2222", secCode2, quantity2);
		List<Securities> secBalanceList = asset.getAllSecuritiesByAccount("2222");
		assertEquals(1, secBalanceList.size());
		assertEquals(quantity2, asset.getQuantitySecByAccount("2222", "VND"));
		assertEquals(true, asset.accIsExistSecCode("2222", "VND"));
	}
	
	@Test
	public void testHoldSecurityShouldRunRight() throws Exception {
		String secCode1 = "ACB";
		int quantity1 = 200;
		int quantityNew =  150;
		asset.addSecurity("1234", secCode1, quantity1);
		asset.addSecurity("1234", secCode1, quantityNew);
		
		String secCode2 = "VND";
		int quantity2 = 500;
		asset.addSecurity("1234", secCode2, quantity2);
		
		asset.holdSecurity("1234", "ACB", 100);
		assertEquals(250, asset.getQuantitySecByAccount("1234", "ACB"));
	}
	
	@Test(expected = SystemException.class)
	public void testHoldSecurityWithQuantityBiggerThanAccount() throws Exception {
		String secCode1 = "ACB";
		int quantity1 = 200;
		int quantityNew =  150;
		asset.addSecurity("1234", secCode1, quantity1);
		asset.addSecurity("1234", secCode1, quantityNew);
		
		String secCode2 = "VND";
		int quantity2 = 500;
		asset.addSecurity("1234", secCode2, quantity2);
		
		asset.holdSecurity("1234", "ACB", 9999);
		assertEquals(250, asset.getQuantitySecByAccount("1234", "ACB"));
	}
	
	@Test(expected=SystemException.class)
	public void testHoldSecurityWithAccountNotExist() throws Exception {
		String secCode1 = "ACB";
		int quantity1 = 200;
		int quantityNew =  150;
		asset.addSecurity("1234", secCode1, quantity1);
		asset.addSecurity("1234", secCode1, quantityNew);
		
		String secCode2 = "VND";
		int quantity2 = 500;
		asset.addSecurity("1234", secCode2, quantity2);
		
		asset.holdSecurity("22222", "ACB", 100);
	}
	
	@Test
	public void testUnHoldSecurityShouldRunRight() throws Exception {
		String secCode1 = "ACB";
		int quantity1 = 200;
		int quantityNew =  150;
		asset.addSecurity("1234", secCode1, quantity1);
		asset.addSecurity("1234", secCode1, quantityNew);
		
		String secCode2 = "VND";
		int quantity2 = 500;
		asset.addSecurity("1234", secCode2, quantity2);
		
		int unHoldQuantity = 100;
		asset.unHoldSecurity("1234", "ACB", unHoldQuantity);
		
		assertEquals(asset.getQuantitySecByAccount("1234", secCode1), quantity1+quantityNew+unHoldQuantity);
	}
	
	@Test(expected=SystemException.class)
	public void testUnHoldSecurityWithAccountNotExistShouldFail() throws Exception {
		String secCode1 = "ACB";
		int quantity1 = 200;
		int quantityNew =  150;
		asset.addSecurity("1234", secCode1, quantity1);
		asset.addSecurity("1234", secCode1, quantityNew);
		
		String secCode2 = "VND";
		int quantity2 = 500;
		asset.addSecurity("1234", secCode2, quantity2);
		
		int unHoldQuantity = 100;
		asset.unHoldSecurity("2222", "ACB", unHoldQuantity);
	}
	//TODO : add test getAllSecuritiesByAccount, getMoneyByAccount together shoud right

}


