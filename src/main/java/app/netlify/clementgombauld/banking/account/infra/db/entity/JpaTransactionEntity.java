package app.netlify.clementgombauld.banking.account.infra.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transaction")
public class JpaTransactionEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "creation_date")
    private Instant creationDate;

    @Column(name = "transaction_amount")
    private BigDecimal transactionAmount;

    @Column(name = "account_identifier")
    private String accountIdentifier;

    @Column(name = "bic")
    private String bic;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "reason")
    private String reason;

    public JpaTransactionEntity() {
    }

    public JpaTransactionEntity(String id, Instant creationDate, BigDecimal transactionAmount, String accountIdentifier, String bic, String accountName, String reason) {
        this.id = id;
        this.creationDate = creationDate;
        this.transactionAmount = transactionAmount;
        this.accountIdentifier = accountIdentifier;
        this.bic = bic;
        this.accountName = accountName;
        this.reason = reason;
    }

    public String getId() {
        return id;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public String getAccountIdentifier() {
        return accountIdentifier;
    }

    public String getBic() {
        return bic;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getReason() {
        return reason;
    }
}
