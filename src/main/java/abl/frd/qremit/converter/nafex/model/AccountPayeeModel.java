package abl.frd.qremit.converter.nafex.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="converted_data_account_payee",
    indexes = { @Index(name = "idx_report_date", columnList = "report_date"), @Index(name = "idx_is_processed", columnList = "is_processed"),
        @Index(name = "idx_is_voucher_generated", columnList = "is_voucher_generated"), @Index(name = "idx_upload_date_time", columnList = "upload_date_time")
    }
)
public class AccountPayeeModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    @Column(name = "transaction_no")
    private String transactionNo;
    @Column(name = "credit_mark")
    private String creditMark;
    @Column(name = "entered_date")
    private String enteredDate;
    @Column(name = "currency")
    private String currency;
    @Column(name = "amount")
    private Double amount;
    @Column(name = "beneficiary_name")
    private String beneficiaryName;
    @Column(name = "exchange_code")
    private String exchangeCode;
    @Column(name = "bank_name")
    private String bankName;
    @Column(name = "bank_code")
    private String bankCode;
    @Column(name = "branch_name")
    private String branchName;
    @Column(name = "branch_code")
    private String branchCode;
    @Column(name = "beneficiary_account_no")
    private String beneficiaryAccount;
    @Column(name = "remitter_name")
    private String remitterName;
    @Column(name = "incentive")
    private Double incentive;
    @Column(name = "account_payee_code")
    private String accountPayeeCode;
    @Column(name = "is_processed", columnDefinition = "TINYINT(1) DEFAULT 0")
    private int isProcessed = 0;
    @Column(name = "is_downloaded", columnDefinition = "TINYINT(1) DEFAULT 0")
    private int isDownloaded = 0;
    @Column(name = "download_date_time")
    private LocalDateTime downloadDateTime;
    @Column(name = "upload_date_time", columnDefinition = "DATETIME")
    private LocalDateTime uploadDateTime;
    @Column(name = "is_voucher_generated", columnDefinition = "TINYINT(1) DEFAULT 0")
    private int isVoucherGenerated = 0;
    @Column(name = "report_date", columnDefinition = "DATETIME")
    private LocalDateTime reportDate;

    @Column(name = "download_user_id")
    private int downloadUserId;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE})
    //@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="upload_user_id")
    private User userModel;

    public User getUserModel() {
        return userModel;
    }

    public void setUserModel(User userModel) {
        this.userModel = userModel;
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE})
    //@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="file_info_model_id")
    private FileInfoModel fileInfoModel;

    public FileInfoModel getFileInfoModel() {
        return fileInfoModel;
    }

    public void setFileInfoModel(FileInfoModel fileInfoModel) {
        this.fileInfoModel = fileInfoModel;
    }

    public AccountPayeeModel() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getCreditMark() {
        return creditMark;
    }

    public void setCreditMark(String creditMark) {
        this.creditMark = creditMark;
    }

    public String getEnteredDate() {
        return enteredDate;
    }

    public void setEnteredDate(String enteredDate) {
        this.enteredDate = enteredDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getExchangeCode() {
        return exchangeCode;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBeneficiaryAccount() {
        return beneficiaryAccount;
    }

    public void setBeneficiaryAccount(String beneficiaryAccount) {
        this.beneficiaryAccount = beneficiaryAccount;
    }

    public String getRemitterName() {
        return remitterName;
    }

    public void setRemitterName(String remitterName) {
        this.remitterName = remitterName;
    }

    public Double getIncentive() {
        return incentive;
    }

    public void setIncentive(Double incentive) {
        this.incentive = incentive;
    }

    public String getAccountPayeeCode() {
        return accountPayeeCode;
    }

    public void setAccountPayeeCode(String accountPayeeCode) {
        this.accountPayeeCode = accountPayeeCode;
    }


    public int getIsProcessed() {
        return this.isProcessed;
    }

    public void setIsProcessed(int isProcessed) {
        this.isProcessed = isProcessed;
    }

    public int getIsDownloaded() {
        return this.isDownloaded;
    }

    public void setIsDownloaded(int isDownloaded) {
        this.isDownloaded = isDownloaded;
    }
    

    public LocalDateTime getDownloadDateTime() {
        return downloadDateTime;
    }

    public void setDownloadDateTime(LocalDateTime downloadDateTime) {
        this.downloadDateTime = downloadDateTime;
    }

    public int getDownloadUserId() {
        return downloadUserId;
    }

    public void setDownloadUserId(int downloadUserId) {
        this.downloadUserId = downloadUserId;
    }

    public LocalDateTime getUploadDateTime() {
        return this.uploadDateTime;
    }

    public void setUploadDateTime(LocalDateTime uploadDateTime) {
        this.uploadDateTime = uploadDateTime;
    }

    public int getIsVoucherGenerated() {
        return this.isVoucherGenerated;
    }

    public void setIsVoucherGenerated(int isVoucherGenerated) {
        this.isVoucherGenerated = isVoucherGenerated;
    }

    public LocalDateTime getReportDate() {
        return this.reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }    

    public AccountPayeeModel(int id, String transactionNo, String creditMark, String enteredDate, String currency, Double amount, String beneficiaryName, String exchangeCode, String bankName, String bankCode, String branchName, String branchCode, String beneficiaryAccount, String remitterName, Double incentive, String accountPayeeCode, int extraA, int extraB, LocalDateTime downloadDateTime, int downloadUserId, LocalDateTime uploadDateTime) {
        this.id = id;
        this.transactionNo = transactionNo;
        this.creditMark = creditMark;
        this.enteredDate = enteredDate;
        this.currency = currency;
        this.amount = amount;
        this.beneficiaryName = beneficiaryName;
        this.exchangeCode = exchangeCode;
        this.bankName = bankName;
        this.bankCode = bankCode;
        this.branchName = branchName;
        this.branchCode = branchCode;
        this.beneficiaryAccount = beneficiaryAccount;
        this.remitterName = remitterName;
        this.incentive = incentive;
        this.accountPayeeCode = accountPayeeCode;
        this.isProcessed = extraA;
        this.isDownloaded = extraB;
        this.downloadDateTime = downloadDateTime;
        this.downloadUserId = downloadUserId;
        this.uploadDateTime = uploadDateTime;
    }

    @Override
    public String toString() {
        return "AccountPayeeModel{" +
                "id=" + id +
                ", transactionNo='" + transactionNo + '\'' +
                ", creditMark='" + creditMark + '\'' +
                ", enteredDate='" + enteredDate + '\'' +
                ", currency='" + currency + '\'' +
                ", amount=" + amount +
                ", beneficiaryName='" + beneficiaryName + '\'' +
                ", exchangeCode='" + exchangeCode + '\'' +
                ", bankName='" + bankName + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", branchName='" + branchName + '\'' +
                ", branchCode='" + branchCode + '\'' +
                ", beneficiaryAccount='" + beneficiaryAccount + '\'' +
                ", remitterName='" + remitterName + '\'' +
                ", incentive=" + incentive +
                ", accountPayeeCode='" + accountPayeeCode + '\'' +
                ", extraA='" + isProcessed + '\'' +
                ", extraB='" + isDownloaded + '\'' +
                ", extraC='" + downloadDateTime + '\'' +
                ", extraD='" + downloadUserId + '\'' +
                ", uploadDateTime='" + uploadDateTime + '\'' +
                '}';
    }

}
