package virtualstockexchange.balance;

public class Balance {
	private long amount;
	private long hold;
	private String secCode;
	private long t0;
	private long t1;
	private long t2;
	private long t3;
	
	
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public long getHold() {
		return hold;
	}
	public void setHold(long hold) {
		this.hold += hold;
	}
	public String getSecCode() {
		return secCode;
	}
	public void setSecCode(String secCode) {
		this.secCode = secCode;
	}
	public long getT0() {
		return t0;
	}
	public void setT0(long t0) {
		this.t0 += t0;
	}
	public long getT1() {
		return t1;
	}
	public void setT1(long t1) {
		this.t1 = t1;
	}
	public long getT2() {
		return t2;
	}
	public void setT2(long t2) {
		this.t2 = t2;
	}
	public long getT3() {
		return t3;
	}
	public void setT3(long t3) {
		this.t3 = t3;
	}
}
