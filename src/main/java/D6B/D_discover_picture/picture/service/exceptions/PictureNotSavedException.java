package D6B.D_discover_picture.picture.service.exceptions;

public class PictureNotSavedException extends RuntimeException{
    public PictureNotSavedException() {
        super();
    }

    public PictureNotSavedException(String message) {
        super(message);
    }
}
