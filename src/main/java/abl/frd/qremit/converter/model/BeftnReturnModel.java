package abl.frd.qremit.converter.model;
import java.time.LocalDate;

import javax.persistence.*;

@Entity
@Table(name="beftn_return", uniqueConstraints = { @UniqueConstraint(columnNames = { "txn_modified", "amount", "processed_date"})},
    indexes = { @Index(name = "idx_txn_modified", columnList = "txn_modified"), @Index(name = "idx_transaction_no", columnList = "transaction_no"), 
    @Index(name = "idx_exchange_code", columnList = "exchange_code"), @Index(name = "idx_return_date", columnList = "return_date") }
)
public class BeftnReturnModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    @Column(name = "transaction_no", length=30)
    private String transactionNo;
    @Column(name = "amount", length = 15)
    private Double amount;
    @Column(name = "txn_modified", length = 30)
    private String txnModified;
    @Column(name = "beneficiary_account", length = 32)
    private String beneficiaryAccount;
    @Column(name = "beneficiary_name", length=128)
    private String beneficiaryName;
    @Column(name = "exchange_code", length = 20)
    private String exchangeCode;
    @Column(name = "routing_no", length = 15)
    private String routingNo;
    @Column(name = "processed_date", columnDefinition = "DATE")
    LocalDate processedDate;
    @Column(name = "return_date", columnDefinition = "DATE")
    LocalDate returnDate;
    @Column(name = "return_code", length = 10)
    private String returnCode;
    @Column(name = "rem_type", length = 64)
    private String remType;
    @Column(name = "status", columnDefinition = "TINYINT(1) DEFAULT 0")
    private int status = 0;
    @Column(name = "file_info_model_id")
    private int fileInfoModelId;
    @Column(name="user_id")
    private int userId;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTransactionNo() {
        return this.transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public Double getAmount() {
        return this.amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTxnModified() {
        return this.txnModified;
    }

    public void setTxnModified(String txnModified) {
        this.txnModified = txnModified;
    }

    public String getBeneficiaryAccount() {
        return this.beneficiaryAccount;
    }

    public void setBeneficiaryAccount(String beneficiaryAccount) {
        this.beneficiaryAccount = beneficiaryAccount;
    }

    public String getBeneficiaryName() {
        return this.beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getExchangeCode() {
        return this.exchangeCode;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    public String getRoutingNo() {
        return this.routingNo;
    }

    public void setRoutingNo(String routingNo) {
        this.routingNo = routingNo;
    }

    public LocalDate getProcessedDate() {
        return this.processedDate;
    }

    public void setProcessedDate(LocalDate processedDate) {
        this.processedDate = processedDate;
    }

    public LocalDate getReturnDate() {
        return this.returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getReturnCode() {
        return this.returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getRemType() {
        return this.remType;
    }

    public void setRemType(String remType) {
        this.remType = remType;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getFileInfoModelId() {
        return this.fileInfoModelId;
    }

    public void setFileInfoModelId(int fileInfoModelId) {
        this.fileInfoModelId = fileInfoModelId;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BeftnReturnModel() {
    }

    @Override
    public String toString() {
        return "{" +
            " transactionNo='" + getTransactionNo() + "'" +
            ", amount='" + getAmount() + "'" +
            ", txnModified='" + getTxnModified() + "'" +
            ", beneficiaryAccount='" + getBeneficiaryAccount() + "'" +
            ", beneficiaryName='" + getBeneficiaryName() + "'" +
            ", exchangeCode='" + getExchangeCode() + "'" +
            ", routingNo='" + getRoutingNo() + "'" +
            ", processedDate='" + getProcessedDate() + "'" +
            ", returnDate='" + getReturnDate() + "'" +
            ", returnCode='" + getReturnCode() + "'" +
            ", remType='" + getRemType() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }

}
