package abl.frd.qremit.converter.nafex.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="converted_data_beftn")
public class BeftnModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    @Column(name = "transaction_no")
    private String transactionNo;
    @Column(name = "org_customer_no")
    private String orgCustomerNo;
    @Column(name = "org_name")
    private String orgName;
    @Column(name = "org_account_no")
    private String orgAccountNo;
    @Column(name = "org_account_type")
    private String orgAccountType;
    @Column(name = "amount")
    private Double amount;
    @Column(name = "beneficiary_name")
    private String beneficiaryName;
    @Column(name = "beneficiary_account")
    private String beneficiaryAccount;
    @Column(name = "beneficiary_account_type")
    private String beneficiaryAccountType;
    @Column(name = "exchange_code")
    private String exchangeCode;
    @Column(name = "routing_no")
    private String routingNo;
    @Column(name = "incentive")
    private Double incentive;
    @Column(name = "is_processed_main")
    private String isProcessedMain;
    @Column(name = "is_processed_incentive")
    private String isProcessedIncentive;
    @Column(name = "is_inc_downloaded")
    private String isIncDownloaded;
    @Column(name = "download_user_id")
    private int downloadUserId;
    @Column(name = "remitter_name", length = 128)
    private String remitterName;
    @Column(name = "bank_name")
    private String bankName;
    @Column(name = "bank_code")
    private String bankCode;
    @Column(name = "branch_name")
    private String branchName;
    @Column(name = "download_date_time")
    private LocalDateTime downloadDateTime;
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

    public BeftnModel() {

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

    public String getOrgCustomerNo() {
        return orgCustomerNo;
    }

    public void setOrgCustomerNo(String orgCustomerNo) {
        this.orgCustomerNo = orgCustomerNo;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgAccountNo() {
        return orgAccountNo;
    }

    public void setOrgAccountNo(String orgAccountNo) {
        this.orgAccountNo = orgAccountNo;
    }

    public String getOrgAccountType() {
        return orgAccountType;
    }

    public void setOrgAccountType(String orgAccountType) {
        this.orgAccountType = orgAccountType;
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

    public String getBeneficiaryAccount() {
        return beneficiaryAccount;
    }

    public void setBeneficiaryAccount(String beneficiaryAccount) {
        this.beneficiaryAccount = beneficiaryAccount;
    }

    public String getBeneficiaryAccountType() {
        return beneficiaryAccountType;
    }

    public void setBeneficiaryAccountType(String beneficiaryAccountType) {
        this.beneficiaryAccountType = beneficiaryAccountType;
    }

    public String getExchangeCode() {
        return exchangeCode;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    public String getRoutingNo() {
        return routingNo;
    }

    public void setRoutingNo(String routingNo) {
        this.routingNo = routingNo;
    }

    public Double getIncentive() {
        return incentive;
    }

    public void setIncentive(Double incentive) {
        this.incentive = incentive;
    }

    public String getIsProcessedMain() {
        return isProcessedMain;
    }

    public void setIsProcessedMain(String extraA) {
        this.isProcessedMain = extraA;
    }

    public String getIsProcessedIncentive() {
        return isProcessedIncentive;
    }

    public void setIsProcessedIncentive(String extraB) {
        this.isProcessedIncentive = extraB;
    }

    public String getIsIncDownloaded() {
        return isIncDownloaded;
    }

    public void setIsIncDownloaded(String extraC) {
        this.isIncDownloaded = extraC;
    }

    public int getDownloadUserId() {
        return downloadUserId;
    }

    public void setDownloadUserId(int downloadUserId) {
        this.downloadUserId = downloadUserId;
    }

    public String getRemitterName() {
        return this.remitterName;
    }

    public void setRemitterName(String remitterName) {
        this.remitterName = remitterName;
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

    public LocalDateTime getDownloadDateTime() {
        return downloadDateTime;
    }

    public void setDownloadDateTime(LocalDateTime extraE) {
        this.downloadDateTime = extraE;
    }

    public LocalDateTime getUploadDateTime() {
        return this.uploadDateTime;
    }

    public void setUploadDateTime(LocalDateTime uploadDateTime) {
        this.uploadDateTime = uploadDateTime;
    }

    public BeftnModel(int id, String transactionNo, String orgCustomerNo, String orgName, String orgAccountNo, String orgAccountType, Double amount, String beneficiaryName, String beneficiaryAccount, String beneficiaryAccountType, String exchangeCode, String routingNo, Double incentive, String remitterName, String bankName, String bankCode, String branchName, String extraA, String extraB, String extraC, int downloadUserId, LocalDateTime downloadDateTime, LocalDateTime uploadDateTime) {
        this.id = id;
        this.transactionNo = transactionNo;
        this.orgCustomerNo = orgCustomerNo;
        this.orgName = orgName;
        this.orgAccountNo = orgAccountNo;
        this.orgAccountType = orgAccountType;
        this.amount = amount;
        this.beneficiaryName = beneficiaryName;
        this.beneficiaryAccount = beneficiaryAccount;
        this.beneficiaryAccountType = beneficiaryAccountType;
        this.exchangeCode = exchangeCode;
        this.routingNo = routingNo;
        this.incentive = incentive;
        this.isProcessedMain = extraA;
        this.isProcessedIncentive = extraB;
        this.isIncDownloaded = extraC;
        this.downloadUserId = downloadUserId;
        this.downloadDateTime = downloadDateTime;
        this.uploadDateTime = uploadDateTime;
        this.remitterName = remitterName;
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.branchName = branchName;
    }

    @Override
    public String toString() {
        return "BeftnModel{" +
                "id=" + id +
                ", transactionNo='" + transactionNo + '\'' +
                ", orgCustomerNo='" + orgCustomerNo + '\'' +
                ", orgName='" + orgName + '\'' +
                ", orgAccountNo='" + orgAccountNo + '\'' +
                ", orgAccountType='" + orgAccountType + '\'' +
                ", amount=" + amount +
                ", beneficiaryName='" + beneficiaryName + '\'' +
                ", beneficiaryAccount='" + beneficiaryAccount + '\'' +
                ", beneficiaryAccountType='" + beneficiaryAccountType + '\'' +
                ", exchangeCode='" + exchangeCode + '\'' +
                ", routingNo='" + routingNo + '\'' +
                ", incentive=" + incentive +
                ", extraA='" + isProcessedMain + '\'' +
                ", extraB='" + isProcessedIncentive + '\'' +
                ", extraC='" + isIncDownloaded + '\'' +
                ", extraD='" + downloadUserId + '\'' +
                ", extraE='" + downloadDateTime + '\'' +
                '}';
    }
}
