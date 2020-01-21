package ru.skvrez.cash_cinimex;

public class NotInCashException extends RuntimeException {

    private static final long serialVersionUID = -9018487529543652493L;

    public NotInCashException() {
        super();
    }

    public NotInCashException(String message) {
        super(message);
    }
}
