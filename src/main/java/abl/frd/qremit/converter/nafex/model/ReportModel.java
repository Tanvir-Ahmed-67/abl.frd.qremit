package abl.frd.qremit.converter.nafex.model;
import java.time.LocalDateTime;
import javax.persistence.*;

@Entity
@Table(name = "report",
    uniqueConstraints = @UniqueConstraint(name = "idx_transaction_no_exchange_code", columnNames = {"exchange_code", "transaction_no", "amount"}),
    indexes = { @Index(name = "idx_report_date", columnList = "report_date"), 
        @Index(name = "idx_exchange_code", columnList = "exchange_code"), @Index(name = "idx_zone_code", columnList = "zone_code"),
        @Index(name = "idx_circle_code", columnList = "circle_code"), @Index(name = "idx_type", columnList = "type"), 
        @Index(name = "idx_file_info_model_id", columnList = "file_info_model_id"), @Index(name = "idx_upload_user_id", columnList = "upload_user_id"),
        @Index(name = "idx_branch_code", columnList = "branch_code"),
    }
)

public class ReportModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    @Column(name = "exchange_code", length = 10)
    private String exchangeCode;
    @Column(name = "transaction_no", length = 20)
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
    @Column(name = "country_code", length = 10)
    private String countryCode;
    @Column(name = "district_code", length = 10)
    private String districtCode;
    @Column(name = "report_date", columnDefinition = "DATE")
    private LocalDateTime reportDate; 

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

    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public LocalDateTime getReportDate() {
        return this.reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public ReportModel() {
    }


    public ReportModel(String exchangeCode, String transactionNo, String bankCode, String bankName, String branchName, String branchCode, Double amount, String beneficiaryName, String beneficiaryAccount, Double incentive, String remitterName, LocalDateTime downloadDateTime, LocalDateTime uploadDateTime, int uploadUserId, int fileInfoModelId, String type, String zoneCode, String circleCode, String countryCode, String districtCode, LocalDateTime reportDate) {
        this.exchangeCode = exchangeCode;
        this.transactionNo = transactionNo;
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.branchName = branchName;
        this.branchCode = branchCode;
        this.amount = amount;
        this.beneficiaryName = beneficiaryName;
        this.beneficiaryAccount = beneficiaryAccount;
        this.incentive = incentive;
        this.remitterName = remitterName;
        this.downloadDateTime = downloadDateTime;
        this.uploadDateTime = uploadDateTime;
        this.uploadUserId = uploadUserId;
        this.fileInfoModelId = fileInfoModelId;
        this.type = type;
        this.zoneCode = zoneCode;
        this.circleCode = circleCode;
        this.countryCode = countryCode;
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
            ", incentive='" + getIncentive() + "'" +
            ", remitterName='" + getRemitterName() + "'" +
            ", downloadDateTime='" + getDownloadDateTime() + "'" +
            ", uploadDateTime='" + getUploadDateTime() + "'" +
            ", uploadUserId='" + getUploadUserId() + "'" +
            ", fileInfoModelId='" + getFileInfoModelId() + "'" +
            ", type='" + getType() + "'" +
            ", zoneCode='" + getZoneCode() + "'" +
            ", circleCode='" + getCircleCode() + "'" +
            ", countryCode='" + getCountryCode() + "'" +
            ", districtCode='" + getDistrictCode() + "'" +
            ", reportDate='" + getReportDate() + "'" +
            "}";
    }
    

}
