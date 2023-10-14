package app.netlify.clementgombauld.banking.account.infra;

import app.netlify.clementgombauld.banking.account.domain.BankInfoType;
import app.netlify.clementgombauld.banking.account.domain.Iban;
import app.netlify.clementgombauld.banking.account.domain.IbanGenerator;
import org.iban4j.CountryCode;
import org.springframework.stereotype.Component;

@Component
public class FrenchIbanGenerator implements IbanGenerator {
    @Override
    public Iban generate() {
        return new Iban(org.iban4j.Iban.random(CountryCode.valueOf(BankInfoType.COUNTRY.getValue())).toString());
    }
}
