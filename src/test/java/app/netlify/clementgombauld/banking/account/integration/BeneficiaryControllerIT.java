package app.netlify.clementgombauld.banking.account.integration;

import app.netlify.clementgombauld.banking.account.integration.configuration.AccountTestConfiguration;
import app.netlify.clementgombauld.banking.account.rest.beneficiary.BeneficiaryController;
import app.netlify.clementgombauld.banking.account.rest.beneficiary.in.AddBeneficiaryRequest;
import app.netlify.clementgombauld.banking.account.rest.beneficiary.out.AddBeneficiaryResponse;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
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
        AddBeneficiaryRequest addBeneficiaryRequest = new AddBeneficiaryRequest(
                beneficiaryIban,
                beneficiaryBic,
                beneficiaryName
        );

        ResultActions result = mockMvc.perform(post("/api/v1/beneficiary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(addBeneficiaryRequest)));

        MvcResult mvcResult = result.andExpect(status().isCreated()).andReturn();

        String content = mvcResult.getResponse().getContentAsString();

        assertThat(content).isEqualTo(jsonMapper.writeValueAsString(new AddBeneficiaryResponse(beneficiaryId)));

    }

}
