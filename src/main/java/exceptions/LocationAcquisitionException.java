package exceptions;

public class LocationAcquisitionException extends Exception{
    public LocationAcquisitionException (String message) {
        super(message);
    }

    public LocationAcquisitionException(String message, Throwable cause) {
        super(message, cause);
    }
}
