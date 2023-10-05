package app.netlify.clementgombauld.banking.infra.inMemory;

import app.netlify.clementgombauld.banking.core.domain.Customer;
import app.netlify.clementgombauld.banking.core.domain.CustomerRepository;

import java.util.Map;

public class InMemoryCustomerRepository implements CustomerRepository {
    private final Map<String, Customer> customerStore;

    public InMemoryCustomerRepository(Map<String, Customer> customerStore) {
        this.customerStore = customerStore;
    }

    @Override
    public void update(Customer customer) {
        customerStore.put(customer.getId(), customer);
    }
}
