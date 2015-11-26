package virtualstockexchange.balance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import virtualstockexchange.exception.ExceptionCode;
import virtualstockexchange.exception.SystemException;

public class AssetTest {
	
	private Asset asset = new Asset();
	
	@Test
	public void testInitMoney() throws SystemException {
		verifyInitMoneyForAccount(10000000);
	}

	private void verifyInitMoneyForAccount(long expectedMoney) throws SystemException {
		asset.initMoneyForAccount("1234", expectedMoney);
		long moneyOfAccount = asset.getMoneyByAccount("1234");
		assertEquals(expectedMoney, moneyOfAccount);
	}

	@Test(expected=RuntimeException.class)
	public void testDuplicateInitMoney() throws SystemException {
		asset.initMoneyForAccount("1234", 1000);
		asset.initMoneyForAccount("1234", 2000);
		long moneyOfAccount = asset.getMoneyByAccount("1234");
		assertEquals(2000, moneyOfAccount);
	}
	
	@Test
	public void testInitMoneyForManyAccountShouldSetMoneyForRightAccount() throws SystemException {
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
	
	@Test
	public void testHoldMoneyWithHoldMoneyBiggerThanCurrentMoney() throws SystemException {
		asset.initMoneyForAccount("1234", 2500);
		long hold = 3000;
        try {
    		asset.holdMoney("1234", hold);
        	fail("Should throw");
        } catch(SystemException e) {
			Assert.assertEquals(ExceptionCode.MONEY_NOT_ENOUGH.code(), e.getCode());
        }
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
	
	@Test
	public void testHoldMoneyWithWrongAccountShouldFail() throws SystemException {
		long initMoney = 2500;
		asset.initMoneyForAccount("1234", initMoney);
		long hold = 1500;
        try {
        	asset.holdMoney("8989", hold);
        	fail("Should throw");
        } catch(SystemException e) {
			Assert.assertEquals(ExceptionCode.ACCOUNT_NOT_EXIST.code(), e.getCode());
        }
	}
	
	@Test
	public void testHoldMoneyWithHoldMoneyTwiceTimeBiggerThanCurrentMoney () throws SystemException {
		long initMoney = 2500;
		asset.initMoneyForAccount("1234", initMoney);
		long hold1 = 1000;
		long hold2 = 2000;
        try {
    		asset.holdMoney("1234", hold1);
    		asset.holdMoney("1234", hold2);
    		fail("Should throw");
        } catch(SystemException e) {
			Assert.assertEquals(ExceptionCode.MONEY_NOT_ENOUGH.code(), e.getCode());
        }
	}
	
	
	@Test
	public void testUnHoldMoneyShouldRunRight () throws SystemException {
		long initMoney = 2500;
		asset.initMoneyForAccount("1234", initMoney);
		long moneyBeforeUnHold = asset.getMoneyByAccount("1234");
		long unHoldMoney = 2000;
		asset.unHoldMoney("1234", unHoldMoney);
		assertEquals(asset.getMoneyByAccount("1234"), unHoldMoney+moneyBeforeUnHold);
	}
	
	@Test
	public void testUnHoldMoneyWithAccountNotExistShouldFail() throws SystemException {
		long initMoney = 2500;
		asset.initMoneyForAccount("1234", initMoney);

		long unHoldMoney = 1000;
        try {
    		asset.unHoldMoney("8989", unHoldMoney);
        	fail("Should throw");
        } catch(SystemException e) {
			Assert.assertEquals(ExceptionCode.ACCOUNT_NOT_EXIST.code(), e.getCode());
        }
	}
	
	@Test
	public void testAddMoneyShouldRight() throws SystemException {
		long initMoney = 2500;
		asset.initMoneyForAccount("1234", initMoney);

		long addMoney = 1000;
		long moneyBeforeAdd = asset.getMoneyByAccount("1234");
		asset.addMoney("1234", addMoney);
		assertEquals(asset.getMoneyByAccount("1234"), moneyBeforeAdd + addMoney);
	}
	
	@Test
	public void testAddMoneyWithAccountNotExistShouldFail() throws SystemException {
		long initMoney = 2500;
		asset.initMoneyForAccount("1234", initMoney);
		long addMoney = 1000;
        try {
    		asset.addMoney("8989", addMoney);
        	fail("Should throw");
        } catch(SystemException e) {
        	assertTrue(true);
			Assert.assertEquals(ExceptionCode.ACCOUNT_NOT_EXIST.code(), e.getCode());
        }
	}
	
	@Test
	public void testAddSecurities () throws Exception {
		String secCode = "ACB";
		int quantity = 200;
		asset.initMoneyForAccount("1234", 0);
		asset.addSecurity("1234", secCode, quantity);
		
		List<Security> secBalanceList = asset.getAllSecuritiesByAccount("1234");
		String secResult = "";
		for (Security secBalance : secBalanceList) {
			if (secBalance.getSecCode().equals(secCode))
				secResult = secBalance.getSecCode();
		}
		assertEquals(secCode, secResult);
		assertEquals(quantity, asset.getQuantitySecByAccount("1234", "ACB"));
	}
	
	@Test
	public void testGetQuantitySecByAccountShouldRunRight() throws Exception {
		try {
			String secCode = "ACB";
			int quantity = 200;
			asset.initMoneyForAccount("1234", 0);
			asset.addSecurity("1234", secCode, quantity);
			assertEquals(quantity, asset.getQuantitySecByAccount("1234", "ACB"));
			asset.getQuantitySecByAccount("1234", "ACBB");
		} catch (SystemException e) {
			assertEquals(ExceptionCode.SEC_CODE_NOT_EXIST.code(), e.getCode());
		}
	}
	
	@Test
	public void testAccIsExistSecCodeShoudRunRight () throws Exception {
		String secCode = "ACB";
		int quantity = 200;
		asset.initMoneyForAccount("1234", 0);
		asset.addSecurity("1234", secCode, quantity);
		assertEquals(true, asset.accIsExistSecCode("1234", "ACB"));
		assertEquals(false, asset.accIsExistSecCode("1234", "VND"));
		assertEquals(false, asset.accIsExistSecCode("1234", "ACBD"));
	}
	
	@Test
	public void testAddSecuritiesWithListSec() throws Exception {
		String secCode1 = "ACB";
		int quantity1 = 200;
		asset.initMoneyForAccount("1234", 0);
		asset.addSecurity("1234", secCode1, quantity1);
		
		String secCode2 = "VND";
		int quantity2 = 500;
		asset.addSecurity("1234", secCode2, quantity2);
		
		List<Security> secBalanceList = asset.getAllSecuritiesByAccount("1234");
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
		asset.initMoneyForAccount("1234", 0);
		asset.addSecurity("1234", secCode1, quantity1);
		asset.addSecurity("1234", secCode1, quantityNew);
		
		String secCode2 = "VND";
		int quantity2 = 500;
		asset.addSecurity("1234", secCode2, quantity2);
		
		List<Security> secBalanceList = asset.getAllSecuritiesByAccount("1234");
		assertEquals(2, secBalanceList.size());
		assertEquals(quantity1 + quantityNew, asset.getQuantitySecByAccount("1234", "ACB"));
		assertEquals(quantity2, asset.getQuantitySecByAccount("1234", "VND"));
	}

	@Test
	public void testAddSecurityWithAccountNotInitMoneyYet () throws Exception {
		String secCode2 = "VND";
		int quantity2 = 500;
		try {
			asset.addSecurity("2222", secCode2, quantity2);
		} catch (SystemException e) {
			assertEquals(ExceptionCode.ACCOUNT_NOT_EXIST.code(), e.getCode());
		}
	}
	
	@Test
	public void testHoldSecurityShouldRunRight() throws Exception, SystemException {
		String secCode1 = "ACB";
		int quantity1 = 200;
		int quantityNew =  150;
		asset.initMoneyForAccount("1234", 0);
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
	
	@Test
	public void testHoldSecurityWithAccountNotExist() throws Exception, SystemException {
		String secCode1 = "ACB";
		int quantity1 = 200;
		int quantityNew =  150;

		
        try {
    		asset.addSecurity("1234", secCode1, quantity1);
    		asset.addSecurity("1234", secCode1, quantityNew);
    		
    		String secCode2 = "VND";
    		int quantity2 = 500;
    		asset.addSecurity("1234", secCode2, quantity2);
    		asset.holdSecurity("22222", "ACB", 100);
        	fail("Should throw");
        } catch(SystemException e) {
        	assertTrue(true);
			Assert.assertEquals(ExceptionCode.ACCOUNT_NOT_EXIST.code(), e.getCode());
        }
	}
	
	@Test
	public void testUnHoldSecurityShouldRunRight() throws Exception {
		String secCode1 = "ACB";
		int quantity1 = 200;
		int quantityNew =  150;
		asset.initMoneyForAccount("1234", 0);
		asset.addSecurity("1234", secCode1, quantity1);
		asset.addSecurity("1234", secCode1, quantityNew);
		
		String secCode2 = "VND";
		int quantity2 = 500;
		asset.addSecurity("1234", secCode2, quantity2);
		
		int unHoldQuantity = 100;
		asset.unHoldSecurity("1234", "ACB", unHoldQuantity);
		
		assertEquals(asset.getQuantitySecByAccount("1234", secCode1), quantity1+quantityNew+unHoldQuantity);
	}
	
	@Test
	public void testUnHoldSecurityWithAccountNotExistShouldFail() throws Exception, SystemException {
		String secCode1 = "ACB";
		int quantity1 = 200;
		int quantityNew =  150;
		asset.initMoneyForAccount("1234", 0);

		asset.addSecurity("1234", secCode1, quantity1);
		asset.addSecurity("1234", secCode1, quantityNew);
		
		String secCode2 = "VND";
		int quantity2 = 500;
		asset.addSecurity("1234", secCode2, quantity2);
		
		int unHoldQuantity = 100;
        try {
    		asset.unHoldSecurity("2222", "ACB", unHoldQuantity);
        	fail("Should throw");
        } catch(SystemException e) {
			Assert.assertEquals(ExceptionCode.ACCOUNT_NOT_EXIST.code(), e.getCode());
        }
	}
	
//	@Test
//	public void testSecurityNextDayShouldRunRight () throws Exception {
//		String secCode1 = "ACB";
//		int quantity1 = 200;
//		String secCode2 = "VND";
//		int quantity2 = 150;
//		asset.initMoneyForAccount("1234", 0);
//		asset.addSecurity("1234", secCode1, quantity1);
//		asset.addSecurity("1234", secCode2, quantity2);
//		
//		asset.nextDay("1234", secCode1);
//		asset.nextDay("1234", secCode1);
//		asset.nextDay("1234", secCode1);
//		Security security = asset.getSecurity("1234", secCode1);
//		assertEquals(quantity1, security.getQuantity());
//		
//	}
	
	//TODO : add test getAllSecuritiesByAccount, getMoneyByAccount together shoud right

}


