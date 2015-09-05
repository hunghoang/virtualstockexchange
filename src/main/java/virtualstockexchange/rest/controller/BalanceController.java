package virtualstockexchange.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import virtualstockexchange.balance.Balance;
import virtualstockexchange.balance.BalanceChecker;
import virtualstockexchange.balance.BalanceException;


@RestController
@RequestMapping("/balances")
public class BalanceController {
	
	@Autowired
	private BalanceChecker balanceChecker;
	
	@RequestMapping(value = "/{account}/sec/{symbol}", method = RequestMethod.GET)
	public Balance showSecBalance(@PathVariable("account") String account, @PathVariable("symbol") String symbol) throws BalanceException {
		System.out.println("Show info sec code " + symbol + " for account: " + account);
		Balance secBalance = balanceChecker.getSecBalance(account, symbol);
		return secBalance;
	}

	@RequestMapping(value = "/{account}/money/", method = RequestMethod.GET)
	public Balance showMoneyBalance(@PathVariable("account") String account) throws BalanceException {
		System.out.println("Show info money balance for account: " + account);
		Balance moneyBalance = balanceChecker.getMoneyBalance(account);
		return moneyBalance;
	}
	
	@RequestMapping(value = "/{account}/", method = RequestMethod.POST)
	public List<Balance> initAccount(@PathVariable("account") String account) throws BalanceException {
		balanceChecker.initAccount(account);
		return balanceChecker.getBalances(account);
	}
	
	@RequestMapping(value = "/{account}/", method = RequestMethod.GET)
	public List<Balance> getAllBalances(@PathVariable("account") String account) throws BalanceException {
		return balanceChecker.getBalances(account);
	}
	
	@RequestMapping(value = "/{account}/next_money/", method = RequestMethod.POST)
	public List<Balance> nextMoney(@PathVariable("account") String account) throws BalanceException {
		balanceChecker.nextMoney(account);
		return balanceChecker.getBalances(account);
	}
	
	@RequestMapping(value = "/{account}/next_security/{symbol}", method = RequestMethod.POST)
	public List<Balance> nextSecurity(@PathVariable("account") String account,
			@PathVariable("symbol") String symbol) throws BalanceException {
		balanceChecker.nextSecurity(account, symbol);
		return balanceChecker.getBalances(account);
	}
	
	@RequestMapping(value = "/{account}/total_assets/", method = RequestMethod.GET)
	public long getTotalAssets(@PathVariable("account") String account) throws BalanceException {
		return balanceChecker.getTotalAssets(account);	
		}
	

}
