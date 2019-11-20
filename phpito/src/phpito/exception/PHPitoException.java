package phpito.exception;

/**
 * Class for PHPito Exception
 * @author Andrea Serra
 *
 */
public class PHPitoException extends Exception {
	private static final long serialVersionUID = -8725336675058043135L;

	public PHPitoException() {
		super();
	}

	public PHPitoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PHPitoException(String message, Throwable cause) {
		super(message, cause);
	}

	public PHPitoException(String message) {
		super(message);
	}

	public PHPitoException(Throwable cause) {
		super(cause);
	}

}
