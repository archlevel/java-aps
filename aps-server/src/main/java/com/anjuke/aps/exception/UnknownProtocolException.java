package com.anjuke.aps.exception;

public class UnknownProtocolException extends ApsException{

    private static final long serialVersionUID = 977023469735311086L;

    public UnknownProtocolException() {
        super();
    }

    public UnknownProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownProtocolException(String message) {
        super(message);
    }

    public UnknownProtocolException(Throwable cause) {
        super(cause);
    }

}
