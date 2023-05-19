package D6B.D_discover_alarm.service.exceptions;

public class Client5xxException extends RuntimeException {

    public Client5xxException() {
        super();
    }

    public Client5xxException(String message) {
        super(message);
    }
}
