package virtualstockexchange.exception;

public enum ExceptionCode {
		
	SEC_CODE_NOT_EXIST("SEC_CODE_NOT_EXIST"),
	SECURITY_NOT_ENOUGH_QUANTITY("SECURITY_NOT_ENOUGH_QUANTITY"),
	OK("0");

	private String code;
	
	private ExceptionCode(String code) {
		this.code = code;
	}
	
	public String code() {
		return code;
	}
}
