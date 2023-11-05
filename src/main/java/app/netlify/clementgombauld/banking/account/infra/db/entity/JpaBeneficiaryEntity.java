package app.netlify.clementgombauld.banking.account.infra.db.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "beneficiary")
public class JpaBeneficiaryEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "iban")
    private String iban;

    @Column(name = "bic")
    private String bic;

    @Column(name = "name")
    private String name;

    @ManyToOne
    private JpaAccountEntity account;


    public JpaBeneficiaryEntity() {
    }

    public JpaBeneficiaryEntity(String id, String iban, String bic, String name) {
        this.id = id;
        this.iban = iban;
        this.bic = bic;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getIban() {
        return iban;
    }

    public String getBic() {
        return bic;
    }

    public String getName() {
        return name;
    }

}
