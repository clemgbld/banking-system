package app.netlify.clementgombauld.banking.account.infra.db.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "account")
public class JpaAccountEntity {
    @Id
    @Column(name = "id")
    private String id;


    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "iban")
    private String iban;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "created_on", columnDefinition = "TIMESTAMP")
    private Instant createdOn;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<JpaBeneficiaryEntity> beneficiaries;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<JpaTransactionEntity> transactions;


    public JpaAccountEntity(String id, String customerId, String iban, BigDecimal balance, Instant createdOn) {
        this.id = id;
        this.customerId = customerId;
        this.iban = iban;
        this.balance = balance;
        this.createdOn = createdOn;
    }

    public JpaAccountEntity() {

    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getIban() {
        return iban;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Instant getCreatedOn() {
        return createdOn;
    }


}
