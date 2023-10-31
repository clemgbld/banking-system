package app.netlify.clementgombauld.banking.account.integration;

import app.netlify.clementgombauld.banking.account.integration.configuration.AccountTestConfiguration;
import app.netlify.clementgombauld.banking.account.rest.beneficiary.BeneficiaryController;
import app.netlify.clementgombauld.banking.account.rest.beneficiary.in.AddBeneficiaryRequest;
import app.netlify.clementgombauld.banking.account.rest.beneficiary.in.DeleteBeneficiaryRequest;
import app.netlify.clementgombauld.banking.account.rest.beneficiary.out.AddBeneficiaryResponse;
import app.netlify.clementgombauld.banking.common.rest.error.ErrorResponse;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(AccountTestConfiguration.class)
@WebMvcTest(BeneficiaryController.class)
public class BeneficiaryControllerIT {

    private static final JsonMapper jsonMapper = new JsonMapper();

    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldAddABeneficiary() throws Exception {
        String beneficiaryId = "3";
        String beneficiaryIban = "DE89370400440532013000";
        String beneficiaryBic = "DEUTDEFF";
        String beneficiaryName = "John Smith";

        ResultActions result = mockMvc.perform(post("/api/v1/beneficiary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(new AddBeneficiaryRequest(
                        beneficiaryIban,
                        beneficiaryBic,
                        beneficiaryName
                ))));

        MvcResult mvcResult = result.andExpect(status().isCreated()).andReturn();

        String content = mvcResult.getResponse().getContentAsString();

        assertThat(content).isEqualTo(jsonMapper.writeValueAsString(new AddBeneficiaryResponse(beneficiaryId)));

    }

    @Test
    void shouldBeABadRequestWhenTryingToAddAnAlreadyExistingBeneficiary() throws Exception {
        String beneficiaryIban = "FR1420041010050500013M02606";
        String beneficiaryBic = "BNPAFRPP123";
        String beneficiaryName = "Arsene Lupin";

        ResultActions result = mockMvc.perform(post("/api/v1/beneficiary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(new AddBeneficiaryRequest(beneficiaryIban,
                        beneficiaryBic,
                        beneficiaryName))));

        MvcResult mvcResult = result.andExpect(status().isBadRequest()).andReturn();

        String content = mvcResult.getResponse().getContentAsString();

        assertThat(content).isEqualTo(jsonMapper.writeValueAsString(new ErrorResponse(
                "The beneficiary with the accountIdentifier : FR1420041010050500013M02606 is already a beneficiary of the account 4",
                HttpStatus.BAD_REQUEST.value()
        )));
    }

    @Test
    void shouldDeleteTheBeneficiary() throws Exception {
        String beneficiaryIban = "FR1420041010050500013M02606";
        ResultActions result = mockMvc.perform(delete("/api/v1/beneficiary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(new DeleteBeneficiaryRequest(beneficiaryIban))));

        result.andExpect(status().isNoContent());

    }

    @Test
    void shouldThrowABadRequestWhenTryingToDeleteANonExistingBeneficiary() throws Exception {
        String beneficiaryIban = "FR5030004000700000157389538";
        ResultActions result = mockMvc.perform(delete("/api/v1/beneficiary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(new DeleteBeneficiaryRequest(beneficiaryIban))));

        MvcResult mvcResult = result.andExpect(status().isBadRequest()).andReturn();

        String content = mvcResult.getResponse().getContentAsString();

        assertThat(content).isEqualTo(jsonMapper.writeValueAsString(new ErrorResponse(
                "Cannot find any account with the accountIdentifier: FR5030004000700000157389538 in your beneficiaries list.",
                HttpStatus.BAD_REQUEST.value()
        )));
    }


}
