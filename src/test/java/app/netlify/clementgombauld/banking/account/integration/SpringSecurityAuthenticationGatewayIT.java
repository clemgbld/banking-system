package app.netlify.clementgombauld.banking.account.integration;

import app.netlify.clementgombauld.banking.account.domain.AuthenticationGateway;
import app.netlify.clementgombauld.banking.account.domain.Customer;
import app.netlify.clementgombauld.banking.account.infra.SpringSecurityAuthenticationGateway;
import app.netlify.clementgombauld.banking.identityaccess.infra.db.entity.JpaUserEntity;
import app.netlify.clementgombauld.banking.identityaccess.domain.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class SpringSecurityAuthenticationGatewayIT {
    @AfterEach
    void cleanUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldRetrieveCurrentCustomer() {
        String id = "1";
        String firstName = "Joel";
        String lastName = "Smith";
        String email = "joelsmith@gmail.com";
        String password = "password";

        UserDetails jpaUserEntity = new JpaUserEntity(
                id,
                firstName,
                lastName,
                email,
                password,
                Role.USER
        );

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(jpaUserEntity, null, jpaUserEntity.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        AuthenticationGateway authenticationGateway = new SpringSecurityAuthenticationGateway();

        Optional<Customer> currentCustomer = authenticationGateway.currentCustomer();

        assertThat(currentCustomer).isPresent();
        assertThat(currentCustomer.get()).isEqualTo(new Customer(id, firstName, lastName));
    }

    @Test
    void shouldNotRetrieveAnyCustomerWhenThereIsNoAuthenticatedCustomer() {
        AuthenticationGateway authenticationGateway = new SpringSecurityAuthenticationGateway();

        Optional<Customer> currentCustomer = authenticationGateway.currentCustomer();

        assertThat(currentCustomer).isEmpty();
    }

}
