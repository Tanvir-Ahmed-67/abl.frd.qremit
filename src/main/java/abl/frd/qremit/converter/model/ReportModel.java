package abl.frd.qremit.converter.model;
import java.time.*;
import javax.persistence.*;

@Entity
@Table(name = "report",
    uniqueConstraints = @UniqueConstraint(name = "idx_transaction_no_exchange_code", columnNames = {"exchange_code", "transaction_no", "amount"}),
    indexes = { @Index(name = "idx_report_date", columnList = "report_date"), 
        @Index(name = "idx_exchange_code", columnList = "exchange_code"), @Index(name = "idx_zone_code", columnList = "zone_code"),
        @Index(name = "idx_circle_code", columnList = "circle_code"), @Index(name = "idx_type", columnList = "type"), 
        @Index(name = "idx_file_info_model_id", columnList = "file_info_model_id"), @Index(name = "idx_upload_user_id", columnList = "upload_user_id"),
        @Index(name = "idx_branch_code", columnList = "branch_code"), @Index(name = "idx_beneficiary_account", columnList = "beneficiary_account"), 
        @Index(name = "idx_govt_incentive", columnList = "govt_incentive"), @Index(name = "idx_agrani_incentive", columnList = "agrani_incentive"),
        @Index(name = "idx_incentive", columnList = "incentive"), @Index(name = "idx_download_date_time", columnList = "download_date_time"), @Index(name = "idx_upload_date_time", columnList = "upload_date_time")
    }
)

public class ReportModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    @Column(name = "exchange_code", length = 20)
    private String exchangeCode;
    @Column(name = "transaction_no", length = 30)
    private String transactionNo;
    @Column(name = "bank_code", length = 10)
    private String bankCode;
    @Column(name = "bank_name", length = 64)
    private String bankName;
    @Column(name = "branch_name", length = 128)
    private String branchName;
    @Column(name = "branch_code", length = 15)
    private String branchCode;
    @Column(name = "amount", length = 15)
    private Double amount;
    @Column(name = "beneficiary_name", length = 128)
    private String beneficiaryName;
    @Column(name = "beneficiary_account", length = 32)
    private String beneficiaryAccount;
    @Column(name = "govt_incentive", length = 15)
    private Double govtIncentive;
    @Column(name = "agrani_incentive", length = 15)
    private Double agraniIncentive;
    @Column(name = "mo_number", length = 30)
    private String moNumber;
    @Column(name = "incentive", length = 15)
    private Double incentive;
    @Column(name = "remitter_name", length = 128)
    private String remitterName;
    @Column(name = "download_date_time", columnDefinition = "DATETIME")
    private LocalDateTime downloadDateTime;
    @Column(name = "upload_date_time", columnDefinition = "DATETIME")
    private LocalDateTime uploadDateTime;
    @Column(name = "upload_user_id")
    private int uploadUserId;
    @Column(name = "file_info_model_id")
    private int fileInfoModelId;
    @Column(name = "type", length = 10)
    private String type;
    @Column(name = "zone_code", length = 10)
    private String zoneCode;
    @Column(name = "circle_code", length = 10)
    private String circleCode;
    @Column(name = "district_code", length = 10)
    private String districtCode;
    @Column(name = "report_date", columnDefinition = "DATE")
    private LocalDate reportDate; 
    @Column(name = "is_api", columnDefinition = "TINYINT(1) DEFAULT 0")
    private int isApi = 0;
    @Column(name = "data_model_id")
    private int dataModelId = 0;
    @Column(name = "entered_date", length=30)
    private String enteredDate;
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

    public String getBankCode() {
        return this.bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return this.bankName;
    }

    public int getDataModelId() {
        return this.dataModelId;
    }

    public void setDataModelId(int dataModelId) {
        this.dataModelId = dataModelId;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
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

    public Double getAmount() {
        return this.amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
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


    public String getEnteredDate() {
        return this.enteredDate;
    }

    public void setEnteredDate(String enteredDate) {
        this.enteredDate = enteredDate;
    }

    public Double getIncentive() {
        return this.incentive;
    }

    public void setIncentive(Double incentive) {
        this.incentive = incentive;
    }

    public String getRemitterName() {
        return this.remitterName;
    }

    public void setRemitterName(String remitterName) {
        this.remitterName = remitterName;
    }

    public int getIsApi() {
        return this.isApi;
    }

    public void setIsApi(int isApi) {
        this.isApi = isApi;
    }


    public LocalDateTime getUploadDateTime() {
        return this.uploadDateTime;
    }

    public void setUploadDateTime(LocalDateTime uploadDateTime) {
        this.uploadDateTime = uploadDateTime;
    }

    public String getDistrictCode() {
        return this.districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public LocalDateTime getDownloadDateTime() {
        return this.downloadDateTime;
    }

    public void setDownloadDateTime(LocalDateTime downloadDateTime) {
        this.downloadDateTime = downloadDateTime;
    }

    public int getUploadUserId() {
        return this.uploadUserId;
    }

    public void setUploadUserId(int uploadUserId) {
        this.uploadUserId = uploadUserId;
    }

    public int getFileInfoModelId() {
        return this.fileInfoModelId;
    }

    public void setFileInfoModelId(int fileInfoModelId) {
        this.fileInfoModelId = fileInfoModelId;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getZoneCode() {
        return this.zoneCode;
    }

    public void setZoneCode(String zoneCode) {
        this.zoneCode = zoneCode;
    }

    public String getCircleCode() {
        return this.circleCode;
    }

    public void setCircleCode(String circleCode) {
        this.circleCode = circleCode;
    }

    public LocalDate getReportDate() {
        return this.reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public ReportModel() {
    }

    public Double getGovtIncentive() {
        return govtIncentive;
    }

    public void setGovtIncentive(Double govtIncentive) {
        this.govtIncentive = govtIncentive;
    }

    public Double getAgraniIncentive() {
        return agraniIncentive;
    }

    public void setAgraniIncentive(Double agraniIncentive) {
        this.agraniIncentive = agraniIncentive;
    }

    public String getMoNumber() {
        return moNumber;
    }

    public void setMoNumber(String moNumber) {
        this.moNumber = moNumber;
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

    public ReportModel(String exchangeCode, String transactionNo, String bankCode, String bankName, String branchName, String branchCode, Double amount, String beneficiaryName, String beneficiaryAccount, Double govtIncentive, Double agraniIncentive, Double incentive, String remitterName, LocalDateTime downloadDateTime, LocalDateTime uploadDateTime, int uploadUserId, int fileInfoModelId, String type, String zoneCode, String circleCode, String districtCode, LocalDate reportDate) {
        this.exchangeCode = exchangeCode;
        this.transactionNo = transactionNo;
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.branchName = branchName;
        this.branchCode = branchCode;
        this.amount = amount;
        this.beneficiaryName = beneficiaryName;
        this.beneficiaryAccount = beneficiaryAccount;
        this.govtIncentive = govtIncentive;
        this.agraniIncentive = agraniIncentive;
        this.incentive = incentive;
        this.remitterName = remitterName;
        this.downloadDateTime = downloadDateTime;
        this.uploadDateTime = uploadDateTime;
        this.uploadUserId = uploadUserId;
        this.fileInfoModelId = fileInfoModelId;
        this.type = type;
        this.zoneCode = zoneCode;
        this.circleCode = circleCode;
        this.districtCode = districtCode;
        this.reportDate = reportDate;
    }
    
    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", exchangeCode='" + getExchangeCode() + "'" +
            ", transactionNo='" + getTransactionNo() + "'" +
            ", bankCode='" + getBankCode() + "'" +
            ", bankName='" + getBankName() + "'" +
            ", branchName='" + getBranchName() + "'" +
            ", branchCode='" + getBranchCode() + "'" +
            ", amount='" + getAmount() + "'" +
            ", beneficiaryName='" + getBeneficiaryName() + "'" +
            ", beneficiaryAccount='" + getBeneficiaryAccount() + "'" +
            ", govtIncentive='" + getGovtIncentive() + "'" +
            ", agraniIncentive='" + getAgraniIncentive() + "'" +
            ", incentive='" + getIncentive() + "'" +
            ", remitterName='" + getRemitterName() + "'" +
            ", downloadDateTime='" + getDownloadDateTime() + "'" +
            ", uploadDateTime='" + getUploadDateTime() + "'" +
            ", uploadUserId='" + getUploadUserId() + "'" +
            ", fileInfoModelId='" + getFileInfoModelId() + "'" +
            ", type='" + getType() + "'" +
            ", zoneCode='" + getZoneCode() + "'" +
            ", circleCode='" + getCircleCode() + "'" +
            ", districtCode='" + getDistrictCode() + "'" +
            ", reportDate='" + getReportDate() + "'" +
            "}";
    }
    

}
