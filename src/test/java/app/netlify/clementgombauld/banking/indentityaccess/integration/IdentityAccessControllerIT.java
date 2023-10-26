package app.netlify.clementgombauld.banking.indentityaccess.integration;

import app.netlify.clementgombauld.banking.identityaccess.rest.IdentityAccessController;
import app.netlify.clementgombauld.banking.identityaccess.rest.in.AuthenticateRequest;
import app.netlify.clementgombauld.banking.identityaccess.rest.in.RegisterRequest;
import app.netlify.clementgombauld.banking.identityaccess.rest.out.AuthenticationResponse;
import app.netlify.clementgombauld.banking.indentityaccess.integration.configuration.IdentityAccessTestConfiguration;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@Import(IdentityAccessTestConfiguration.class)
@WebMvcTest(IdentityAccessController.class)
public class IdentityAccessControllerIT {

    private static final JsonMapper jsonMapper = new JsonMapper();

    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldLoginAndGetToken() throws Exception {
        String email = "John@hotmail.fr";
        String password = "145312455fqsdlkfjqm9@";
        AuthenticateRequest authenticateRequest = new AuthenticateRequest(email, password);

        ResultActions result = mockMvc.perform(post("/api/v1/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(authenticateRequest)));

        MvcResult mvcResult = result.andExpect(status().isOk()).andReturn();

        String content = mvcResult.getResponse().getContentAsString();

        assertThat(content).isEqualTo(jsonMapper.writeValueAsString(new AuthenticationResponse("token")));
    }

    @Test
    void shouldRegisterAndGetToken() throws Exception {
        String firstName = "John";
        String lastName = "Smith";
        String email = "John@hotmail.fr";
        String password = "145312455fqsdDkfjqm9@";
        RegisterRequest registerRequest = new RegisterRequest(firstName, lastName, email, password);

        ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(registerRequest)));

        MvcResult mvcResult = result.andExpect(status().isOk()).andReturn();

        String content = mvcResult.getResponse().getContentAsString();

        assertThat(content).isEqualTo(jsonMapper.writeValueAsString(new AuthenticationResponse("token")));

    }

    @Test
    void shouldGetA404WhenPasswordIsNotCorrect() throws Exception {
        String firstName = "John";
        String lastName = "Smith";
        String email = "John@hotmail.fr";
        String password = "145312455fqsdkfjqm9@";

        RegisterRequest registerRequest = new RegisterRequest(firstName, lastName, email, password);

        ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(registerRequest)));

        String error = Objects.requireNonNull(result.andExpect(status().isBadRequest()).andReturn().getResolvedException()).getMessage();

        assertThat(error).isEqualTo("Password must at least have one upper case letter.");
    }

}
