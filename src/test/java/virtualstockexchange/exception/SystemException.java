package virtualstockexchange.exception;

@SuppressWarnings("serial")
public class SystemException extends Exception {

	private final String message;
	private final String code;
	
	public SystemException(String code) {
		super();
		this.code = code;
		this.message = null;
	}
	
	public SystemException(String code, String message) {
		super();
		this.code = code;
		this.message = message;
	}
	
	public SystemException(String message, String code, Throwable throwable) {
		super(throwable);
		this.code = code;
		this.message = message;
	}
	
	public SystemException(String code, Throwable throwable) {
		super(throwable);
		this.code = code;
		this.message = null;
	}

	public String getMessage() {
		return message;
	}

	public String getCode() {
		return code;
	}
	
}
