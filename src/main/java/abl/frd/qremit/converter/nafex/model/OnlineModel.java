package abl.frd.qremit.converter.nafex.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="converted_data_online")
public class OnlineModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    @Column(name = "transaction_no")
    private String transactionNo;
    @Column(name = "exchange_code")
    private String exchangeCode;
    @Column(name = "beneficiary_name")
    private String beneficiaryName;
    @Column(name = "beneficiary_account")
    private String beneficiaryAccount;
    @Column(name = "amount")
    private Double amount;
    @Column(name = "remitter_name")
    private String remitterName;
    @Column(name = "bank_name")
    private String bankName;
    @Column(name = "bank_code")
    private String bankCode;
    @Column(name = "branch_name")
    private String branchName;
    @Column(name = "branch_code")
    private String branchCode;
    @Column(name = "is_processed")
    private String isProcessed;
    @Column(name = "is_downloaded")
    private String isDownloaded;
    @Column(name = "download_date_time")
    private LocalDateTime downloadDateTime;
    @Column(name = "download_user_id")
    private int downloadUserId;
    @Column(name = "upload_date_time", columnDefinition = "DATETIME")
    private LocalDateTime uploadDateTime;

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

    public OnlineModel() {

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

    public String getExchangeCode() {
        return exchangeCode;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getRemitterName() {
        return remitterName;
    }

    public void setRemitterName(String remitterName) {
        this.remitterName = remitterName;
    }

    public String getIsProcessed() {
        return isProcessed;
    }

    public void setIsProcessed(String extraA) {
        this.isProcessed = extraA;
    }

    public String getIsDownloaded() {
        return isDownloaded;
    }

    public void setIsDownloaded(String extraB) {
        this.isDownloaded = extraB;
    }

    public LocalDateTime getDownloadDateTime() {
        return downloadDateTime;
    }

    public void setDownloadDateTime(LocalDateTime extraC) {
        this.downloadDateTime = extraC;
    }

    public int getDownloadUserId() {
        return downloadUserId;
    }

    public void setDownloadUserId(int downloadUserId) {
        this.downloadUserId = downloadUserId;
    }

    public String getBankName() {
        return this.bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCode() {
        return this.bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBranchName() {
        return this.branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchCode() {
        return this.branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public LocalDateTime getUploadDateTime() {
        return this.uploadDateTime;
    }

    public void setUploadDateTime(LocalDateTime uploadDateTime) {
        this.uploadDateTime = uploadDateTime;
    }
    
    public OnlineModel(int id, String transactionNo, String exchangeCode, String beneficiaryName, String beneficiaryAccount, Double amount, String remitterName, String bankName, String bankCode, String branchName, String branchCode, String extraA, String extraB, LocalDateTime downloadDateTime, int downloadUserId, LocalDateTime uploadDateTime) {
        this.id = id;
        this.transactionNo = transactionNo;
        this.exchangeCode = exchangeCode;
        this.beneficiaryName = beneficiaryName;
        this.beneficiaryAccount = beneficiaryAccount;
        this.amount = amount;
        this.remitterName = remitterName;
        this.isProcessed = extraA;
        this.isDownloaded = extraB;
        this.downloadDateTime = downloadDateTime;
        this.downloadUserId = downloadUserId;
        this.uploadDateTime = uploadDateTime;
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.branchCode = branchCode;
        this.branchName = branchName;
    }

    @Override
    public String toString() {
        return "OnlineModel{" +
                "id=" + id +
                ", transactionNo='" + transactionNo + '\'' +
                ", exchangeCode='" + exchangeCode + '\'' +
                ", beneficiaryName='" + beneficiaryName + '\'' +
                ", beneficiaryAccount='" + beneficiaryAccount + '\'' +
                ", amount=" + amount +
                ", remitterName='" + remitterName + '\'' +
                ", extraA='" + isProcessed + '\'' +
                ", extraB='" + isDownloaded + '\'' +
                ", extraC='" + downloadDateTime + '\'' +
                ", extraD='" + downloadUserId + '\'' +
                ", extraE='" + uploadDateTime + '\'' +
                '}';
    }

}