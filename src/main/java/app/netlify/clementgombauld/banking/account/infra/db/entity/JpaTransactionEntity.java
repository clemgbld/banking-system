package app.netlify.clementgombauld.banking.account.infra.db.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "transaction")
public class JpaTransactionEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "creation_date", columnDefinition = "TIMESTAMP")
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

    @ManyToOne
    private JpaAccountEntity account;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JpaTransactionEntity that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(creationDate, that.creationDate) && Objects.equals(transactionAmount, that.transactionAmount) && Objects.equals(accountIdentifier, that.accountIdentifier) && Objects.equals(bic, that.bic) && Objects.equals(accountName, that.accountName) && Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creationDate, transactionAmount, accountIdentifier, bic, accountName, reason);
    }

    @Override
    public String toString() {
        return "JpaTransactionEntity{" +
                "id='" + id + '\'' +
                ", creationDate=" + creationDate +
                ", transactionAmount=" + transactionAmount +
                ", accountIdentifier='" + accountIdentifier + '\'' +
                ", bic='" + bic + '\'' +
                ", accountName='" + accountName + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public void setAccountIdentifier(String accountIdentifier) {
        this.accountIdentifier = accountIdentifier;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setAccount(JpaAccountEntity account) {
        this.account = account;
    }
}
