package app.netlify.clementgombauld.banking.identityaccess.domain;

import app.netlify.clementgombauld.banking.identityaccess.domain.exceptions.PasswordLowerCaseRequiredException;
import app.netlify.clementgombauld.banking.identityaccess.domain.exceptions.PasswordTooShortException;
import app.netlify.clementgombauld.banking.identityaccess.domain.exceptions.PasswordUpperCaseRequiredException;

import java.util.List;
import java.util.function.Consumer;


public class Password {

    private final String value;

    private static final int MIN_LENGTH = 8;
    private static final String CONTAINS_LOWER_CASE_LETTER = ".*[a-z].*";

    private static final String CONTAINS_UPPER_CASE_LETTER = ".*[A-Z].*";

    private final ValidatorChain<String> passwordValidatorChain = new ValidatorChain<>(List.of(
            this::validateLength
            ,
            buildRegexValidator(CONTAINS_LOWER_CASE_LETTER, () -> {
                throw new PasswordLowerCaseRequiredException();
            }),
            buildRegexValidator(CONTAINS_UPPER_CASE_LETTER, () -> {
                throw new PasswordUpperCaseRequiredException();
            })
    ));

    public Password(String value) {
        this.value = validate(value);
    }

    private String validate(String value) {
        passwordValidatorChain.validate(value);
        return value;
    }

    private Consumer<String> buildRegexValidator(String regex, Runnable exceptionRunnable) {
        return (value) -> {
            if (!value.matches(regex)) {
                exceptionRunnable.run();
            }
        };
    }

    private void validateLength(String value) {
        if (value.length() < MIN_LENGTH) {
            throw new PasswordTooShortException();
        }
    }


    public String value() {
        return value;
    }
}
