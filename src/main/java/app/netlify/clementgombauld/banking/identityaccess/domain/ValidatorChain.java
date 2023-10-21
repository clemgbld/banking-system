package app.netlify.clementgombauld.banking.identityaccess.domain;

import java.util.List;
import java.util.function.Consumer;

public class ValidatorChain<T> {
    private final List<Consumer<T>> validators;

    ValidatorChain(List<Consumer<T>> validators) {
        this.validators = validators;
    }

    public void validate(T value) {
        validators.forEach((validator -> validator.accept(value)));
    }
}
