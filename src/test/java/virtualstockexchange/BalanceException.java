package virtualstockexchange;

public class BalanceException extends Exception {
	 
	private static final long serialVersionUID = 838015769288988758L;
	private String message = null;
 
    public BalanceException() {
        super();
    }
 
    public BalanceException(String message) {
        super(message);
        this.message = message;
    }
 
    public BalanceException(Throwable cause) {
        super(cause);
    }
 
    @Override
    public String toString() {
        return message;
    }
 
    @Override
    public String getMessage() {
        return message;
    }
}
