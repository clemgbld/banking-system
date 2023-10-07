package app.netlify.clementgombauld.banking.core.domain;

public interface CustomerRepository {
    void update(Customer customer);

    void delete(Customer customer);
}
