package app.netlify.clementgombauld.banking.account.integration;

import app.netlify.clementgombauld.banking.account.integration.configuration.AccountTestConfiguration;
import app.netlify.clementgombauld.banking.account.rest.account.AccountController;
import app.netlify.clementgombauld.banking.account.rest.account.in.CloseAccountRequest;
import app.netlify.clementgombauld.banking.account.rest.account.in.ReceiveMoneyFromExternalBankRequest;
import app.netlify.clementgombauld.banking.account.rest.account.in.TransferMoneyRequest;
import app.netlify.clementgombauld.banking.account.usecases.OpenAccount;
import app.netlify.clementgombauld.banking.common.rest.error.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.assertj.core.api.Assertions.assertThat;

@Import(AccountTestConfiguration.class)
@WebMvcTest(AccountController.class)
public class AccountControllerIT {

    private static final JsonMapper jsonMapper = new JsonMapper();

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("bic", () -> "BNPAFRPP123");

    }

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

    @Test
    void shouldCloseAnAccount() throws Exception {
        ResultActions result = mockMvc.perform(post("/api/v1/account/close")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(new CloseAccountRequest(
                        "FR5030004000700000157389538",
                        "AGRIFRPP989",
                        "Arsene Lupin"
                ))));

        result.andExpect(status().isOk());
    }

    @Test
    void shouldBeABadRequestWhenTheExternalIbanIsInvalid() throws Exception {
        ResultActions result = mockMvc.perform(post("/api/v1/account/close")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(new CloseAccountRequest(
                        "FR5030004000700000",
                        "AGRIFRPP989",
                        "Arsene Lupin"
                ))));

        String error = result.andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(error).isEqualTo(jsonMapper.writeValueAsString(new ErrorResponse("accountIdentifier: FR5030004000700000 is invalid.", HttpStatus.BAD_REQUEST.value())));

    }

    @Test
    void shouldTransferMoney() throws Exception {
        ResultActions result = mockMvc.perform(post("/api/v1/account/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(
                        new TransferMoneyRequest(
                                new BigDecimal(5),
                                "FR1420041010050500013M02606",
                                "shopping"
                        )
                )));

        result.andExpect(status().isOk());

    }

    @Test
    void shouldBeABadRequestWhenTheBeneficiaryIsUnknown() throws Exception {
        ResultActions result = mockMvc.perform(post("/api/v1/account/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(
                        new TransferMoneyRequest(
                                new BigDecimal(5),
                                "FR1420041010050500013M02607",
                                "shopping"
                        )
                )));

        String error = result.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();

        assertThat(error).isEqualTo(
                jsonMapper.writeValueAsString(new ErrorResponse("Cannot find any account with the accountIdentifier: FR1420041010050500013M02607 in your beneficiaries list.", HttpStatus.BAD_REQUEST.value()))
        );

    }

    @Test
    void shouldReceiveMoneyFromExternalBank() throws Exception {
        ResultActions result = mockMvc.perform(post("/api/v1/account/receive")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(
                        new ReceiveMoneyFromExternalBankRequest(
                                "FR5030004000700000157389538",
                                "FR1420041010050500013M02607",
                                "AGRIFRPP989",
                                "Jean Louis",
                                new BigDecimal(5),
                                "shopping"
                        )
                )));

        result.andExpect(status().isOk());
    }


}
