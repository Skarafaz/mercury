package it.skarafaz.mercury.exception;

public class ValidationException extends Exception {
    private static final long serialVersionUID = 5137298023298850552L;

    public ValidationException(String detailMessage) {
        super(detailMessage);
    }
}
