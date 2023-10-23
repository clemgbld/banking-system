package app.netlify.clementgombauld.banking.indentityaccess.integration;

import app.netlify.clementgombauld.banking.common.domain.DateProvider;
import app.netlify.clementgombauld.banking.common.inmemory.DeterministicDateProvider;
import app.netlify.clementgombauld.banking.identityaccess.domain.Role;
import app.netlify.clementgombauld.banking.identityaccess.infra.JwtService;
import app.netlify.clementgombauld.banking.identityaccess.infra.entity.JpaUserEntity;
import app.netlify.clementgombauld.banking.indentityaccess.integration.inmemory.InMemoryUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.io.BufferedReader;
import java.security.Principal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class JwtServiceIT {


    private DateProvider dateProvider;

    private HashMap<String, UserDetails> userDetailsStore;

    private UserDetailsService userDetailsService;


    @BeforeEach
    void setup() {
        long currentDateInMs = 1516239022111L;
        dateProvider = new DeterministicDateProvider(currentDateInMs);
        userDetailsStore = new HashMap<>();
        userDetailsService = new InMemoryUserDetailsService(userDetailsStore);
    }


    @AfterEach
    void cleanUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldHaveAToken() {
        JwtService jwtService = buildJwtService();
        HttpServletRequest request = buildInMemoryRequest();
        request.setAttribute("Authorization", "Bearer some-token");
        assertThat(jwtService.hasNoToken(
                request
        )).isFalse();
    }

    @Test
    void shouldNotHaveAToken() {

        JwtService jwtService = buildJwtService();
        HttpServletRequest request = buildInMemoryRequest();
        assertThat(jwtService.hasNoToken(request)).isTrue();
    }

    @Test
    void shouldNotHaveATokenWhenTheAuthorizationHeaderDoesNotStartWithBearer() {
        JwtService jwtService = buildJwtService();
        HttpServletRequest request = buildInMemoryRequest();
        request.setAttribute("Authorization", "Something");
        assertThat(jwtService.hasNoToken(request)).isTrue();
    }

    @Test
    void shouldAuthenticateTheUser() {
        UserDetails expectedUserDetails = new JpaUserEntity("1", "John", "Doe", "John@hotmail.fr", "fqosfjqsfjio01", Role.USER);
        userDetailsStore.put("John@hotmail.fr", expectedUserDetails);
        JwtService jwtService = buildJwtService();
        HttpServletRequest request = buildInMemoryRequest();
        request.setAttribute("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJKb2huQGhvdG1haWwuZnIiLCJpYXQiOjE1MTYyMzkwMjJ9.zwcwSAvfDWnxLUVueRLoefPqQr1OF-LjcyY3fIfApD0");
        jwtService.authenticate(request);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(expectedUserDetails);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities()).isEqualTo(expectedUserDetails.getAuthorities());
        assertThat(SecurityContextHolder.getContext().getAuthentication().getDetails()).isEqualTo(new WebAuthenticationDetails("remoteAddress", null));
    }

    @Test
    void shouldNotAuthenticateTheUserWhenTheUserIsAlreadyAuthenticated() {
        UserDetails authenticatedUser = new JpaUserEntity("1", "John", "Doe", "John@hotmail.fr", "fqosfjqsfjio01", Role.USER);
        UserDetails userFromDatasource = new JpaUserEntity("1", "John", "Doe", "John@hotmail.fr", "fqosfjqsfjio01", Role.USER);
        userDetailsStore.put("John@hotmail.fr", userFromDatasource);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                authenticatedUser,
                null,
                authenticatedUser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        JwtService jwtService = buildJwtService();
        HttpServletRequest request = buildInMemoryRequest();
        request.setAttribute("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJKb2huQGhvdG1haWwuZnIiLCJpYXQiOjE1MTYyMzkwMjJ9.zwcwSAvfDWnxLUVueRLoefPqQr1OF-LjcyY3fIfApD0");
        jwtService.authenticate(request);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isNotEqualTo(userFromDatasource);
    }

    @Test
    void shouldNotAuthenticateTheUserWhenItsNotTheSameUser() {
        UserDetails expectedUserDetails = new JpaUserEntity("2", "John", "Snow", "Snow@hotmail.fr", "fqosfjqsfjio01", Role.USER);
        userDetailsStore.put("John@hotmail.fr", expectedUserDetails);
        JwtService jwtService = buildJwtService();
        HttpServletRequest request = buildInMemoryRequest();
        request.setAttribute("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJKb2huQGhvdG1haWwuZnIiLCJpYXQiOjE1MTYyMzkwMjJ9.zwcwSAvfDWnxLUVueRLoefPqQr1OF-LjcyY3fIfApD0");
        jwtService.authenticate(request);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldNotAuthenticateTheUserWhenTheTokenIsExpired() {
        UserDetails expectedUserDetails = new JpaUserEntity("1", "John", "Doe", "John@hotmail.fr", "fqosfjqsfjio01", Role.USER);
        userDetailsStore.put("John@hotmail.fr", expectedUserDetails);
        JwtService jwtService = buildJwtService();
        HttpServletRequest request = buildInMemoryRequest();
        request.setAttribute("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJKb2huQGhvdG1haWwuZnIiLCJpYXQiOjE0MTYyMzkwMjIsImV4cCI6MTQxNjI0MDQ2Mn0.B_oXtqE_L0Ml_q6yKjdOI0CBqI_6Iw79g8azaC1qstM");
        assertThatThrownBy(() -> jwtService.authenticate(request)).isInstanceOf(ExpiredJwtException.class);
    }


    private JwtService buildJwtService() {
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
        return new JwtService(jwtSecret, dateProvider, userDetailsService);
    }


    private HttpServletRequest buildInMemoryRequest() {


        return new HttpServletRequest() {

            private final Map<String, Object> header = new HashMap<>();

            @Override
            public String getAuthType() {
                return null;
            }

            @Override
            public Cookie[] getCookies() {
                return new Cookie[0];
            }

            @Override
            public long getDateHeader(String s) {
                return 0;
            }

            @Override
            public String getHeader(String s) {
                return (String) header.get(s);
            }

            @Override
            public Enumeration<String> getHeaders(String s) {
                return null;
            }

            @Override
            public Enumeration<String> getHeaderNames() {
                return null;
            }

            @Override
            public int getIntHeader(String s) {
                return 0;
            }

            @Override
            public String getMethod() {
                return null;
            }

            @Override
            public String getPathInfo() {
                return null;
            }

            @Override
            public String getPathTranslated() {
                return null;
            }

            @Override
            public String getContextPath() {
                return null;
            }

            @Override
            public String getQueryString() {
                return null;
            }

            @Override
            public String getRemoteUser() {
                return null;
            }

            @Override
            public boolean isUserInRole(String s) {
                return false;
            }

            @Override
            public Principal getUserPrincipal() {
                return null;
            }

            @Override
            public String getRequestedSessionId() {
                return null;
            }

            @Override
            public String getRequestURI() {
                return null;
            }

            @Override
            public StringBuffer getRequestURL() {
                return null;
            }

            @Override
            public String getServletPath() {
                return null;
            }

            @Override
            public HttpSession getSession(boolean b) {
                return null;
            }

            @Override
            public HttpSession getSession() {
                return null;
            }

            @Override
            public String changeSessionId() {
                return null;
            }

            @Override
            public boolean isRequestedSessionIdValid() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromCookie() {
                return false;
            }

            @Override
            public boolean isRequestedSessionIdFromURL() {
                return false;
            }

            @Override
            public boolean authenticate(HttpServletResponse httpServletResponse) {
                return false;
            }

            @Override
            public void login(String s, String s1) {

            }

            @Override
            public void logout() {

            }

            @Override
            public Collection<Part> getParts() {
                return null;
            }

            @Override
            public Part getPart(String s) {
                return null;
            }

            @Override
            public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) {
                return null;
            }

            @Override
            public Object getAttribute(String s) {
                return null;
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return null;
            }

            @Override
            public String getCharacterEncoding() {
                return null;
            }

            @Override
            public void setCharacterEncoding(String s) {

            }

            @Override
            public int getContentLength() {
                return 0;
            }

            @Override
            public long getContentLengthLong() {
                return 0;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public ServletInputStream getInputStream() {
                return null;
            }

            @Override
            public String getParameter(String s) {
                return null;
            }

            @Override
            public Enumeration<String> getParameterNames() {
                return null;
            }

            @Override
            public String[] getParameterValues(String s) {
                return new String[0];
            }

            @Override
            public Map<String, String[]> getParameterMap() {
                return null;
            }

            @Override
            public String getProtocol() {
                return null;
            }

            @Override
            public String getScheme() {
                return null;
            }

            @Override
            public String getServerName() {
                return null;
            }

            @Override
            public int getServerPort() {
                return 0;
            }

            @Override
            public BufferedReader getReader() {
                return null;
            }

            @Override
            public String getRemoteAddr() {
                return "remoteAddress";
            }

            @Override
            public String getRemoteHost() {
                return null;
            }

            @Override
            public void setAttribute(String s, Object o) {
                header.put(s, o);
            }

            @Override
            public void removeAttribute(String s) {

            }

            @Override
            public Locale getLocale() {
                return null;
            }

            @Override
            public Enumeration<Locale> getLocales() {
                return null;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public RequestDispatcher getRequestDispatcher(String s) {
                return null;
            }

            @Override
            public int getRemotePort() {
                return 0;
            }

            @Override
            public String getLocalName() {
                return null;
            }

            @Override
            public String getLocalAddr() {
                return null;
            }

            @Override
            public int getLocalPort() {
                return 0;
            }

            @Override
            public ServletContext getServletContext() {
                return null;
            }

            @Override
            public AsyncContext startAsync() throws IllegalStateException {
                return null;
            }

            @Override
            public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
                return null;
            }

            @Override
            public boolean isAsyncStarted() {
                return false;
            }

            @Override
            public boolean isAsyncSupported() {
                return false;
            }

            @Override
            public AsyncContext getAsyncContext() {
                return null;
            }

            @Override
            public DispatcherType getDispatcherType() {
                return null;
            }

            @Override
            public String getRequestId() {
                return null;
            }

            @Override
            public String getProtocolRequestId() {
                return null;
            }

            @Override
            public ServletConnection getServletConnection() {
                return null;
            }
        };
    }


}
