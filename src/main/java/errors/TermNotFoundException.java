package errors;

public class TermNotFoundException extends RuntimeException {
    public TermNotFoundException(String message) {
        super(message);
    }
}
