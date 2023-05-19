package D6B.D_discover_picture.picture.service.exceptions;

public class DeletePictureFailException extends RuntimeException{
    public DeletePictureFailException() {
        super();
    }

    public DeletePictureFailException(String message) {
        super(message);
    }
}
