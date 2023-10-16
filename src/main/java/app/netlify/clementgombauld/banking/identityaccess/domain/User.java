package app.netlify.clementgombauld.banking.identityaccess.domain;

public class User {

    private final String email;

    public User(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
