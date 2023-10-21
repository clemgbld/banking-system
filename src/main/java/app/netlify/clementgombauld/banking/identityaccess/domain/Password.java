package app.netlify.clementgombauld.banking.identityaccess.domain;

import app.netlify.clementgombauld.banking.identityaccess.domain.exceptions.PasswordTooShortException;

public record Password(String value) {

    public static final int MIN_LENGTH = 8;

    public Password(String value) {
        this.value = validate(value);
    }

    private String validate(String value) {
        if (value.length() < MIN_LENGTH) {
            throw new PasswordTooShortException();
        }
        return value;
    }
}
