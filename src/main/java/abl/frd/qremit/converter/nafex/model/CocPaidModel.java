package abl.frd.qremit.converter.nafex.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="base_data_table_coc_paid", uniqueConstraints = @UniqueConstraint(columnNames = {"file_info_model_id", "transaction_no"}))
public class CocPaidModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int  id;
    @Column(name = "exchange_code")
    private String exchangeCode;
    @Column(name = "transaction_no", unique = true, nullable = false)
    private String transactionNo;
    @Column(name = "amount", nullable = false)
    private Double amount;
    @Column(name = "entered_date")
    private String enteredDate;
    @Column(name = "paid_date")
    private String paidDate;
    @Column(name = "remitter_name")
    private String remitterName;
    @Column(name = "beneficiary_name")
    private String beneficiaryName;
    @Column(name = "beneficiary_account_no", nullable = false)
    private String beneficiaryAccount;
    @Column(name = "beneficiary_mobile_no")
    private String beneficiaryMobile;
    @Column(name = "branch_code")
    private String branchCode;
    @Column(name = "tr_mode")
    private String trMode;
    @Column(name = "upload_date_time", columnDefinition = "DATETIME")
    private LocalDateTime uploadDateTime;
    @Column(name = "report_date", columnDefinition = "DATETIME")
    private LocalDateTime reportDate;
    @Column(name = "is_updated", columnDefinition = "TINYINT(1) DEFAULT 0")
    private int isUpdated = 0;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name="upload_user_id")
    private User userModel;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name="file_info_model_id")
    private FileInfoModel fileInfoModel;

    public CocPaidModel(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExchangeCode() {
        return exchangeCode;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getEnteredDate() {
        return enteredDate;
    }

    public void setEnteredDate(String enteredDate) {
        this.enteredDate = enteredDate;
    }

    public String getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(String paidDate) {
        this.paidDate = paidDate;
    }

    public String getRemitterName() {
        return remitterName;
    }

    public void setRemitterName(String remitterName) {
        this.remitterName = remitterName;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getBeneficiaryAccount() {
        return beneficiaryAccount;
    }

    public void setBeneficiaryAccount(String beneficiaryAccount) {
        this.beneficiaryAccount = beneficiaryAccount;
    }

    public String getBeneficiaryMobile() {
        return beneficiaryMobile;
    }

    public void setBeneficiaryMobile(String beneficiaryMobile) {
        this.beneficiaryMobile = beneficiaryMobile;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getTrMode() {
        return trMode;
    }

    public void setTrMode(String trMode) {
        this.trMode = trMode;
    }

    public LocalDateTime getUploadDateTime() {
        return uploadDateTime;
    }

    public void setUploadDateTime(LocalDateTime uploadDateTime) {
        this.uploadDateTime = uploadDateTime;
    }

    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public int getIsUpdated() {
        return isUpdated;
    }

    public void setIsUpdated(int isUpdated) {
        this.isUpdated = isUpdated;
    }

    public User getUserModel() {
        return userModel;
    }

    public void setUserModel(User userModel) {
        this.userModel = userModel;
    }

    public FileInfoModel getFileInfoModel() {
        return fileInfoModel;
    }

    public void setFileInfoModel(FileInfoModel fileInfoModel) {
        this.fileInfoModel = fileInfoModel;
    }

    public CocPaidModel(String exchangeCode, String transactionNo, Double amount, String enteredDate, String paidDate, String remitterName, String beneficiaryName, String beneficiaryAccount, String beneficiaryMobile, String branchCode, String trMode, LocalDateTime uploadDateTime) {
        this.exchangeCode = exchangeCode;
        this.transactionNo = transactionNo;
        this.amount = amount;
        this.enteredDate = enteredDate;
        this.paidDate = paidDate;
        this.remitterName = remitterName;
        this.beneficiaryName = beneficiaryName;
        this.beneficiaryAccount = beneficiaryAccount;
        this.beneficiaryMobile = beneficiaryMobile;
        this.branchCode = branchCode;
        this.trMode = trMode;
        this.uploadDateTime = uploadDateTime;
    }

    @Override
    public String toString() {
        return "CocPaidModel{" +
                "id=" + id +
                ", exchangeCode='" + exchangeCode + '\'' +
                ", transactionNo='" + transactionNo + '\'' +
                ", amount=" + amount +
                ", enteredDate='" + enteredDate + '\'' +
                ", paidDate='" + paidDate + '\'' +
                ", remitterName='" + remitterName + '\'' +
                ", beneficiaryName='" + beneficiaryName + '\'' +
                ", beneficiaryAccount='" + beneficiaryAccount + '\'' +
                ", beneficiaryMobile='" + beneficiaryMobile + '\'' +
                ", branchCode='" + branchCode + '\'' +
                ", trMode='" + trMode + '\'' +
                ", uploadDateTime=" + uploadDateTime +
                ", reportDate=" + reportDate +
                ", isUpdated=" + isUpdated +
                '}';
    }
}
