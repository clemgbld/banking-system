package app.netlify.clementgombauld.banking.infra;

import app.netlify.clementgombauld.banking.core.domain.BankInfoType;
import app.netlify.clementgombauld.banking.core.domain.Iban;
import app.netlify.clementgombauld.banking.core.domain.IbanGenerator;
import org.iban4j.CountryCode;
import org.springframework.stereotype.Component;

@Component
public class FrenchIbanGenerator implements IbanGenerator {
    @Override
    public Iban generate() {
        return new Iban(org.iban4j.Iban.random(CountryCode.valueOf(BankInfoType.COUNTRY.getValue())).toString());
    }
}
