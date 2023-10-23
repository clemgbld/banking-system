package app.netlify.clementgombauld.banking.indentityaccess.integration;

import app.netlify.clementgombauld.banking.common.domain.DateProvider;
import app.netlify.clementgombauld.banking.common.inmemory.DeterministicDateProvider;
import app.netlify.clementgombauld.banking.identityaccess.infra.JwtTokenGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class JwtTokenGeneratorIT {

    private long currentDateInMs = 1516239022111L;

    @Test
    void shouldGenerateAJwtToken() {
        final String email = "John@hotmail.fr";
        JwtTokenGenerator jwtTokenGenerator = buildJwtTokenGenerator();
        assertThat(jwtTokenGenerator.generate(email)).isEqualTo("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJKb2huQGhvdG1haWwuZnIiLCJpYXQiOjE1MTYyMzkwMjIsImV4cCI6MTUxNjI0MDQ2Mn0.gR1NNo87xLIYs5xtmj_iQAdzwd3p_v_ooQoCVFt3OO0");
    }


    private JwtTokenGenerator buildJwtTokenGenerator() {
        DateProvider dateProvider = new DeterministicDateProvider(currentDateInMs);
        final String jwtSecret = "MIIEpQIBAAKCAQEAlqZak6RpiMbIc2nUU1QEvfOLcn1+FTaYtGXqR9X24nX3O7Z0" +
                "dvF3qbyCffZTIl2waCYo6O1+wp7H5vmqHtB+r+qWCzw/+1GMeKR3dKvMrUPwXIXG" +
                "efBFzMWE8xJr4zOVERJihqoFfYgQ3Yd9FQXt1Cx4Q/M0ZhloRVULhr8e1HZWujxt" +
                "3tleWEUf0IgvjDIk/87ex5Ds/4xfTSxja5RAj85NXFNVufQ5i4G4KPdQRT0rbS4w" +
                "LRmXQM+nA8QuJMQvZPsqgv1+SvehZJJ5pituw9ubXNd9D8Z1V31sVXOskFbXrY8y" +
                "PFjmqK6zQS319UyOiSkGsNgOXMPuOwSgQqrMtQIDAQABAoIBAQCJfo7QjZAcW4D7" +
                "hS1aWCZzz2IIPmzio4+/pfyrT6cijRP2ldG59SyH6BsZJ8Y8aVHl3F4eKRQ2QICm" +
                "WFtiAFx/5hTHcFfc4lLYkRLNkzcdNBKd/yJkjFzIRcXPq5J4LM3yZNszmN2l8peh" +
                "+FS66UxlB6lEKyY5wRqfnL0GYEydD3YiQdKhGXNPlkTveHX3kZ6TpTv4mhuleCRD" +
                "YjodnmOwUI7LrEu9LH624tUOOHuJPpWVzw3UASm0/r5v4Y+MjoEhurNp20+8fRg2" +
                "AQxirYwM72tiVC+1HW+I1EYJkj+8tjkH8YiddLs0RbGUfgWYa+DWoSwAR1VP4+Zw" +
                "Mk1IvohJAoGBAMUxP5JIssi5UKf8bOb+4h7OhN7dUtfhoIdxs2jjgKp8Lov9s+/H" +
                "pzzuknDCw6Aad+VJtiMS5KK7dGOvWmBFe7cXPwoc3gkCscQ+9tuLSAUoaduPu/O3" +
                "FRJdmlJkd4Ij1r/7fQpyxNehlpAypvsJGzIXkZ8LiUXgM51yRRQLjPSnAoGBAMOT" +
                "ylv7CXh4Wgu6IUTnxmpE5FgkbHERnPKxzRB6znoQ1Rzh1zyc92Wpkixny8fsACUs" +
                "PXXe4foeT5TKlRF13jnmtYFAXuoH/kiCNjLXrTDyhrGgi/YwLJYGqvZF5c9v1sD3" +
                "XoP/p2JRytvsZpkJ46Yzsel+JACveP9AQgldWrNDAoGBALpgH8utNn2M2XTMmkmB" +
                "okqnuuHBoek08brSdlWuoQ+Bq6TYt1bsB18JxhMZojgSjDuEfceMtdgxAIKvW+Ye" +
                "Bh44tHZkbl9p0oa/VCaQ8BMf7V197pEc+6eOHKu/FU1FflWZ5scXToDX0yybE0O1" +
                "iaTArfv6tVX74Azx0eRuPl2FAoGAXckKOe94zCdSbtszxvoqIJ2mrknRIqtiitMO" +
                "Vf+98bUOrQnzj+JplHurV9XyV41pSGodkt9tl+ZfHSfEMhpGlZd3pGJ84Vcx7g1a" +
                "9iiMqbMzOgyV8VmKl+rcorpECKRF5ET7MusulixIzgtf67VdUD7r6t8NXrLGoAht" +
                "YlY8/0cCgYEAh5OcYmdaanA/M9R3ogwaXLS7eYhedAdXkMveAfhJENZCfF5L6cQC" +
                "qCxZ12wR1V2sMWpVzazLMpM5nKTJ0G4ZO7Sy7BEPiDB19/P7Ur/eyzx+V8g/sEcD" +
                "xLd3eI5eEVvUVpmcM/+byoxM8BJ6r/z6mPvMA4XxJRoCUBH6ri6ew3k=é";
        return new JwtTokenGenerator(dateProvider, jwtSecret);
    }


}
