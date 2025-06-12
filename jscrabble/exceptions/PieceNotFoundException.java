package jscrabble.exceptions;

public class PieceNotFoundException extends RuntimeException {

    public PieceNotFoundException() {
        super();
    }
    
    public PieceNotFoundException(String message) {
        super(message);
    }
}
