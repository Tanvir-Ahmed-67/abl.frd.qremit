package abl.frd.qremit.converter.model;
import java.time.LocalDateTime;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name="base_data_table_npsb_mfs", uniqueConstraints = { @UniqueConstraint(columnNames = { "transaction_no", "amount", "exchange_code"})},
    indexes = { @Index(name = "idx_file_info_model_id", columnList = "file_info_model_id"), @Index(name="typeFlag", columnList = "typeFlag"), @Index(name = "idx_report_date", columnList = "report_date"), 
        @Index(name = "idx_is_processed", columnList = "is_processed"),@Index(name = "idx_is_voucher_generated", columnList = "is_voucher_generated"), 
        @Index(name = "idx_upload_date_time", columnList = "upload_date_time"),@Index(name = "idx_download_date_time", columnList = "download_date_time"),
        @Index(name = "idx_temp_status", columnList = "temp_status"), @Index(name = "idx_beneficiary_account", columnList = "beneficiary_account_no") }
)
public class NpsbMfsModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int  id;
    @Column(name = "exchange_code", length = 20)
    private String exchangeCode;
    @Column(name = "transaction_no", length=30, nullable = false)
    private String transactionNo;
    @Column(name = "currency", length=32)
    private String currency;
    @Column(name = "amount", length = 15, nullable = false)
    private Double amount;
    @Column(name = "entered_date", length=30)
    private String enteredDate;
    @Column(name = "remitter_name", length=128)
    private String remitterName;
    @Column(name = "remitter_mobile_no", length=30)
    private String remitterMobile;
    @Column(name = "beneficiary_name", length=128)
    private String beneficiaryName;
    @Column(name = "beneficiary_account_no", nullable = false, length=32)
    private String beneficiaryAccount;
    @Column(name = "beneficiary_mobile_no", length=20)
    private String beneficiaryMobile;
    @Column(name = "bank_name", length=64)
    private String bankName;
    @Column(name = "bank_code", length=10)
    private String bankCode;
    @Column(name = "branch_name", length=128)
    private String branchName;
    @Column(name = "branch_code", length=15)
    private String branchCode;
    @Column(name = "drawee_branch_name", length=32)
    private String draweeBranchName;
    @Column(name = "drawee_branch_code", length=10)
    private String draweeBranchCode;
    @Column(name = "purpose_of_remittance", length=32)
    private String purposeOfRemittance;
    @Column(name = "source_of_income", length=32)
    private String sourceOfIncome;
    @Column(name = "typeFlag", length = 10)
    private String typeFlag;
    @Column(name = "govt_incentive", length = 12)
    private Double govtIncentive = 0.0;
    @Column(name = "agrani_incentive", length = 12)
    private Double agraniIncentive = 0.0;
    @Column(name = "incentive")
    private Double incentive = 0.0;
    @Column(name = "is_processed", columnDefinition = "TINYINT(1) DEFAULT 0")
    private int isProcessed = 0;
    @Column(name = "is_downloaded", columnDefinition = "TINYINT(1) DEFAULT 0")
    private int isDownloaded = 0;
    @Column(name = "download_date_time")
    private LocalDateTime downloadDateTime;
    @Column(name = "download_user_id")
    private int downloadUserId;
    @Column(name = "upload_date_time", columnDefinition = "DATETIME")
    private LocalDateTime uploadDateTime;
    @Column(name = "is_voucher_generated", columnDefinition = "TINYINT(1) DEFAULT 0")
    private int isVoucherGenerated = 0;
    @Column(name = "temp_status", columnDefinition = "TINYINT(1) DEFAULT 0")
    private int tempStatus = 0;
    @Column(name = "report_date", columnDefinition = "DATE")
    private LocalDateTime reportDate;
    @Column(name="commission", length = 10)
    private String commission;
    @Column(name = "source_country", length = 64)
    private String sourceCountry;
    @Column(name = "source_foreign_currency", length = 10)
    private String sourceForeignCurrency;
    @Column(name = "conversion_rate", length = 10)
    private String conversionRate;
    @Column(name = "remitter_gender", length=10)
    private String remitterGender;
    @Column(name = "beneficiary_gender", length=10)
    private String beneficiaryGender;
    @Column(name = "beneficiary_district", length = 64)
    private String beneficiaryDistrict;
    @Column(name = "paid_date", columnDefinition = "DATETIME")
    private LocalDateTime paidDate;
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name="upload_user_id")
    @JsonIgnore
    private User userModel;
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name="file_info_model_id")
    @JsonIgnore
    private FileInfoModel fileInfoModel;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExchangeCode() {
        return this.exchangeCode;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    public String getTransactionNo() {
        return this.transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getAmount() {
        return this.amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getEnteredDate() {
        return this.enteredDate;
    }

    public void setEnteredDate(String enteredDate) {
        this.enteredDate = enteredDate;
    }

    public String getRemitterName() {
        return this.remitterName;
    }

    public void setRemitterName(String remitterName) {
        this.remitterName = remitterName;
    }

    public String getRemitterMobile() {
        return this.remitterMobile;
    }

    public void setRemitterMobile(String remitterMobile) {
        this.remitterMobile = remitterMobile;
    }

    public String getBeneficiaryName() {
        return this.beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getBeneficiaryAccount() {
        return this.beneficiaryAccount;
    }

    public void setBeneficiaryAccount(String beneficiaryAccount) {
        this.beneficiaryAccount = beneficiaryAccount;
    }

    public String getBeneficiaryMobile() {
        return this.beneficiaryMobile;
    }

    public void setBeneficiaryMobile(String beneficiaryMobile) {
        this.beneficiaryMobile = beneficiaryMobile;
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

    public String getDraweeBranchName() {
        return this.draweeBranchName;
    }

    public void setDraweeBranchName(String draweeBranchName) {
        this.draweeBranchName = draweeBranchName;
    }

    public String getDraweeBranchCode() {
        return this.draweeBranchCode;
    }

    public void setDraweeBranchCode(String draweeBranchCode) {
        this.draweeBranchCode = draweeBranchCode;
    }

    public String getPurposeOfRemittance() {
        return this.purposeOfRemittance;
    }

    public void setPurposeOfRemittance(String purposeOfRemittance) {
        this.purposeOfRemittance = purposeOfRemittance;
    }

    public String getSourceOfIncome() {
        return this.sourceOfIncome;
    }

    public void setSourceOfIncome(String sourceOfIncome) {
        this.sourceOfIncome = sourceOfIncome;
    }


    public String getTypeFlag() {
        return this.typeFlag;
    }

    public void setTypeFlag(String typeFlag) {
        this.typeFlag = typeFlag;
    }

    public Double getGovtIncentive() {
        return this.govtIncentive;
    }

    public void setGovtIncentive(Double govtIncentive) {
        this.govtIncentive = govtIncentive;
    }

    public Double getAgraniIncentive() {
        return this.agraniIncentive;
    }

    public void setAgraniIncentive(Double agraniIncentive) {
        this.agraniIncentive = agraniIncentive;
    }

    public Double getIncentive() {
        return this.incentive;
    }

    public void setIncentive(Double incentive) {
        this.incentive = incentive;
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
        return this.downloadDateTime;
    }

    public void setDownloadDateTime(LocalDateTime downloadDateTime) {
        this.downloadDateTime = downloadDateTime;
    }

    public int getDownloadUserId() {
        return this.downloadUserId;
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

    public int getTempStatus() {
        return this.tempStatus;
    }

    public void setTempStatus(int tempStatus) {
        this.tempStatus = tempStatus;
    }

    public LocalDateTime getReportDate() {
        return this.reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public String getCommission() {
        return this.commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getSourceCountry() {
        return this.sourceCountry;
    }

    public void setSourceCountry(String sourceCountry) {
        this.sourceCountry = sourceCountry;
    }

    public String getSourceForeignCurrency() {
        return this.sourceForeignCurrency;
    }

    public void setSourceForeignCurrency(String sourceForeignCurrency) {
        this.sourceForeignCurrency = sourceForeignCurrency;
    }

    public String getConversionRate() {
        return this.conversionRate;
    }

    public void setConversionRate(String conversionRate) {
        this.conversionRate = conversionRate;
    }

    public String getRemitterGender() {
        return this.remitterGender;
    }

    public void setRemitterGender(String remitterGender) {
        this.remitterGender = remitterGender;
    }

    public String getBeneficiaryGender() {
        return this.beneficiaryGender;
    }

    public void setBeneficiaryGender(String beneficiaryGender) {
        this.beneficiaryGender = beneficiaryGender;
    }

    public String getBeneficiaryDistrict() {
        return this.beneficiaryDistrict;
    }

    public void setBeneficiaryDistrict(String beneficiaryDistrict) {
        this.beneficiaryDistrict = beneficiaryDistrict;
    }

    public User getUserModel() {
        return this.userModel;
    }

    public void setUserModel(User userModel) {
        this.userModel = userModel;
    }

    public FileInfoModel getFileInfoModel() {
        return this.fileInfoModel;
    }

    public void setFileInfoModel(FileInfoModel fileInfoModel) {
        this.fileInfoModel = fileInfoModel;
    }

    public LocalDateTime getPaidDate() {
        return this.paidDate;
    }

    public void setPaidDate(LocalDateTime paidDate) {
        this.paidDate = paidDate;
    }

    public NpsbMfsModel() {
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", exchangeCode='" + getExchangeCode() + "'" +
            ", transactionNo='" + getTransactionNo() + "'" +
            ", currency='" + getCurrency() + "'" +
            ", amount='" + getAmount() + "'" +
            ", enteredDate='" + getEnteredDate() + "'" +
            ", remitterName='" + getRemitterName() + "'" +
            ", remitterMobile='" + getRemitterMobile() + "'" +
            ", beneficiaryName='" + getBeneficiaryName() + "'" +
            ", beneficiaryAccount='" + getBeneficiaryAccount() + "'" +
            ", beneficiaryMobile='" + getBeneficiaryMobile() + "'" +
            ", bankName='" + getBankName() + "'" +
            ", bankCode='" + getBankCode() + "'" +
            ", branchName='" + getBranchName() + "'" +
            ", branchCode='" + getBranchCode() + "'" +
            ", draweeBranchName='" + getDraweeBranchName() + "'" +
            ", draweeBranchCode='" + getDraweeBranchCode() + "'" +
            ", purposeOfRemittance='" + getPurposeOfRemittance() + "'" +
            ", sourceOfIncome='" + getSourceOfIncome() + "'" +
            ", typeFlag='" + getTypeFlag() + "'" +
            ", govtIncentive='" + getGovtIncentive() + "'" +
            ", agraniIncentive='" + getAgraniIncentive() + "'" +
            ", incentive='" + getIncentive() + "'" +
            ", isProcessed='" + getIsProcessed() + "'" +
            ", isDownloaded='" + getIsDownloaded() + "'" +
            ", downloadDateTime='" + getDownloadDateTime() + "'" +
            ", downloadUserId='" + getDownloadUserId() + "'" +
            ", uploadDateTime='" + getUploadDateTime() + "'" +
            ", isVoucherGenerated='" + getIsVoucherGenerated() + "'" +
            ", tempStatus='" + getTempStatus() + "'" +
            ", reportDate='" + getReportDate() + "'" +
            ", commission='" + getCommission() + "'" +
            ", sourceCountry='" + getSourceCountry() + "'" +
            ", sourceForeignCurrency='" + getSourceForeignCurrency() + "'" +
            ", conversionRate='" + getConversionRate() + "'" +
            ", remitterGender='" + getRemitterGender() + "'" +
            ", beneficiaryGender='" + getBeneficiaryGender() + "'" +
            ", beneficiaryDistrict='" + getBeneficiaryDistrict() + "'" +
            ", paidDate='" + getPaidDate() + "'" +
            "}";
    }

}
