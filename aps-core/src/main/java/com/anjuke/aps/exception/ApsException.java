package com.anjuke.aps.exception;

public class ApsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ApsException() {
        super();
    }

    public ApsException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public ApsException(String message) {
        super(message);
    }

    public ApsException(Throwable cause) {
        super(cause);
    }

}
