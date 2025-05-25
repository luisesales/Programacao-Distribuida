package exceptions;

public class LookupException extends RemoteErrorException {
    public LookupException(String route) {
        super("Route for class not found: " + route);
    }
}
