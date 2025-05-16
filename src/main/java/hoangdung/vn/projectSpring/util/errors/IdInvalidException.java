package hoangdung.vn.projectSpring.util.errors;
/**
 * Exception thrown when an invalid ID is encountered.
 * This could be due to various reasons such as format issues or non-existence.
 */

public class IdInvalidException extends Exception {

    private static final long serialVersionUID = 1L;

    public IdInvalidException(String message) {
        super(message);
    }

    public IdInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdInvalidException(Throwable cause) {
        super(cause);
    }

    public IdInvalidException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
