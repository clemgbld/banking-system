package app.netlify.clementgombauld.banking.account.integration;

import app.netlify.clementgombauld.banking.account.integration.configuration.AccountTestConfiguration;
import app.netlify.clementgombauld.banking.account.rest.account.AccountController;
import app.netlify.clementgombauld.banking.account.usecases.OpenAccount;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(AccountTestConfiguration.class)
@WebMvcTest(AccountController.class)
public class AccountControllerIT {

    private static final JsonMapper jsonMapper = new JsonMapper();

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private OpenAccount openAccount;

    @Test
    void shouldOpenAnAccount() throws Exception {
        ResultActions result = mockMvc.perform(post("/api/v1/account/open")
                .contentType(MediaType.APPLICATION_JSON));

        Mockito.verify(openAccount).handle();

        result.andExpect(status().isCreated());

    }

}
