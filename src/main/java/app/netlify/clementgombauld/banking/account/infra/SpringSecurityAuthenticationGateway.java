package app.netlify.clementgombauld.banking.account.infra;

import app.netlify.clementgombauld.banking.account.domain.AuthenticationGateway;
import app.netlify.clementgombauld.banking.account.domain.Customer;
import app.netlify.clementgombauld.banking.identityaccess.infra.entity.JpaUserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.Optional;

public class SpringSecurityAuthenticationGateway implements AuthenticationGateway {
    @Override
    public Optional<Customer> currentCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof JpaUserEntity jpaUserEntity) {
            return translateToCustomer(jpaUserEntity);
        }
        return Optional.empty();
    }

    private Optional<Customer> translateToCustomer(JpaUserEntity jpaUserEntity) {
        return Optional.of(new Customer(
                jpaUserEntity.getId(),
                jpaUserEntity.getFirstName(),
                jpaUserEntity.getLastName()
        ));
    }
}
