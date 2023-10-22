package app.netlify.clementgombauld.banking.indentityaccess.integration.inmemory;

import app.netlify.clementgombauld.banking.identityaccess.infra.entity.JpaUserEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Map;

public class InMemoryUserDetailsService implements UserDetailsService {

    private final Map<String, UserDetails> userDetailsStore;

    public InMemoryUserDetailsService(Map<String, UserDetails> userDetailsStore) {
        this.userDetailsStore = userDetailsStore;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDetailsStore.get(username);
    }
}
