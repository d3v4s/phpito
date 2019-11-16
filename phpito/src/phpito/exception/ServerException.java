package phpito.exception;

/**
 * Class for Server exceptions
 * @author Andrea Serra
 *
 */
public class ServerException extends Exception {
	private static final long serialVersionUID = 9165051035928508044L;

	public ServerException() {
	}

	public ServerException(String message) {
		super(message);
	}

	public ServerException(Throwable cause) {
		super(cause);
	}

	public ServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
