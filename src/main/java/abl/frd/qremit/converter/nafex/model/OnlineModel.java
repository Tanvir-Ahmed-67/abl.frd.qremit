package abl.frd.qremit.converter.nafex.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="converted_data_online")
public class OnlineModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
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
    @Column(name = "is_processed")
    private String isProcessed;
    @Column(name = "is_downloaded")
    private String isDownloaded;
    @Column(name = "download_date_time")
    private LocalDateTime downloadDateTime;
    @Column(name = "download_user_id")
    private int downloadUserId;
    @Column(name = "extra_e")
    private String extraE;

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="upload_user_id")
    private User userModel;

    public User getUserModel() {
        return userModel;
    }

    public void setUserModel(User userModel) {
        this.userModel = userModel;
    }

    @ManyToOne(cascade=CascadeType.ALL)
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getExtraE() {
        return extraE;
    }

    public void setExtraE(String extraE) {
        this.extraE = extraE;
    }

    public OnlineModel(long id, String transactionNo, String exchangeCode, String beneficiaryName, String beneficiaryAccount, Double amount, String remitterName, String extraA, String extraB, LocalDateTime downloadDateTime, int downloadUserId, String extraE) {
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
        this.extraE = extraE;
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
                ", extraE='" + extraE + '\'' +
                '}';
    }

}