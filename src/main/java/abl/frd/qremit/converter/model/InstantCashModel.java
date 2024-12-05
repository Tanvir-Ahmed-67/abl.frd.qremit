package abl.frd.qremit.converter.model;
import java.time.LocalDateTime;
import javax.persistence.*;



@Entity
@Table(name="base_data_table_instantcash",  uniqueConstraints = { @UniqueConstraint(columnNames = { "transaction_no", "amount", "exchange_code"})},
    indexes = { @Index(name = "idx_file_info_model_id", columnList = "file_info_model_id") }
)
public class InstantCashModel {
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
    @Column(name = "process_flag", length=10)
    private String processFlag;
    @Column(name = "type_flag")
    private String typeFlag;
    @Column(name = "processed_by", length=32)
    private String processedBy;
    @Column(name = "processed_date", length=32)
    private String processedDate;
    @Column(name = "upload_date_time", columnDefinition = "DATETIME")
    private LocalDateTime uploadDateTime;

    @ManyToOne(cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="file_info_model_id")
    private FileInfoModel fileInfoModel;

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="user_id")
    private User userModel;

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

    public InstantCashModel() {

    }

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

    public String getEnteredDate() {
        return enteredDate;
    }

    public void setEnteredDate(String enteredDate) {
        this.enteredDate = enteredDate;
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

    public String getDraweeBranchName() {
        return draweeBranchName;
    }

    public void setDraweeBranchName(String draweeBranchName) {
        this.draweeBranchName = draweeBranchName;
    }

    public String getDraweeBranchCode() {
        return draweeBranchCode;
    }

    public void setDraweeBranchCode(String draweeBranchCode) {
        this.draweeBranchCode = draweeBranchCode;
    }

    public String getPurposeOfRemittance() {
        return purposeOfRemittance;
    }

    public void setPurposeOfRemittance(String purposeOfRemittance) {
        this.purposeOfRemittance = purposeOfRemittance;
    }

    public String getSourceOfIncome() {
        return sourceOfIncome;
    }

    public void setSourceOfIncome(String sourceOfIncome) {
        this.sourceOfIncome = sourceOfIncome;
    }

    public String getRemitterMobile() {
        return remitterMobile;
    }

    public void setRemitterMobile(String remitterMobile) {
        this.remitterMobile = remitterMobile;
    }

    public String getProcessFlag() {
        return processFlag;
    }

    public void setProcessFlag(String processFlag) {
        this.processFlag = processFlag;
    }

    public String getTypeFlag() {
        return typeFlag;
    }

    public void setTypeFlag(String typeFlag) {
        this.typeFlag = typeFlag;
    }

    public String getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(String extraA) {
        this.processedBy = extraA;
    }

    public String getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(String processedDate) {
        this.processedDate = processedDate;
    }

    public LocalDateTime getUploadDateTime() {
        return this.uploadDateTime;
    }

    public void setUploadDateTime(LocalDateTime uploadDateTime) {
        this.uploadDateTime = uploadDateTime;
    }


    public InstantCashModel(String exchangeCode, String transactionNo, String currency, Double amount, String enteredDate, String remitterName, String remitterMobile, String beneficiaryName, String beneficiaryAccount, String beneficiaryMobile, String bankName, String bankCode, String branchName, String branchCode, String draweeBranchName, String draweeBranchCode, String purposeOfRemittance, String sourceOfIncome, String processFlag, String typeFlag, String processedBy, String processedDate, LocalDateTime uploadDateTime) {
        this.exchangeCode = exchangeCode;
        this.transactionNo = transactionNo;
        this.currency = currency;
        this.amount = amount;
        this.enteredDate = enteredDate;
        this.remitterName = remitterName;
        this.remitterMobile = remitterMobile;
        this.beneficiaryName = beneficiaryName;
        this.beneficiaryAccount = beneficiaryAccount;
        this.beneficiaryMobile = beneficiaryMobile;
        this.bankName = bankName;
        this.bankCode = bankCode;
        this.branchName = branchName;
        this.branchCode = branchCode;
        this.draweeBranchName = draweeBranchName;
        this.draweeBranchCode = draweeBranchCode;
        this.purposeOfRemittance = purposeOfRemittance;
        this.sourceOfIncome = sourceOfIncome;
        this.processFlag = processFlag;
        this.typeFlag = typeFlag;
        this.processedBy = processedBy;
        this.processedDate = processedDate;
        this.uploadDateTime = uploadDateTime;
    }

    public InstantCashModel(String exchangeCode, String transactionNo, String currency, Double amount, String enteredDate, String remitterName, String remitterMobile, String beneficiaryName, String beneficiaryAccount, String beneficiaryMobile, String bankName, String bankCode, String branchName, String branchCode, String draweeBranchName, String draweeBranchCode, String purposeOfRemittance, String sourceOfIncome, String processFlag, String typeFlag, String processedBy, String processedDate, LocalDateTime upLocalDateTime, FileInfoModel fileInfoModel, User user) {
        this(exchangeCode, transactionNo, currency, amount, enteredDate, remitterName, remitterMobile, beneficiaryName, beneficiaryAccount, beneficiaryMobile, bankName, bankCode, branchName, branchCode, draweeBranchName, draweeBranchCode, purposeOfRemittance, sourceOfIncome, processFlag, typeFlag, processedBy, processedDate, upLocalDateTime);
        this.fileInfoModel = fileInfoModel;
        this.userModel = user;
    }

    @Override
    public String toString() {
        return "BecModel{" +
                "id=" + id +
                ", exchangeCode='" + exchangeCode + '\'' +
                ", transactionNo='" + transactionNo + '\'' +
                ", currency='" + currency + '\'' +
                ", amount=" + amount +
                ", enteredDate='" + enteredDate + '\'' +
                ", remitterName='" + remitterName + '\'' +
                ", beneficiaryName='" + beneficiaryName + '\'' +
                ", beneficiaryAccount='" + beneficiaryAccount + '\'' +
                ", bankName='" + bankName + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", branchName='" + branchName + '\'' +
                ", branchCode='" + branchCode + '\'' +
                ", beneficiaryMobile='" + beneficiaryMobile + '\'' +
                ", draweeBranchName='" + draweeBranchName + '\'' +
                ", draweeBranchCode='" + draweeBranchCode + '\'' +
                ", purposeOfRemittance='" + purposeOfRemittance + '\'' +
                ", sourceOfIncome='" + sourceOfIncome + '\'' +
                ", remitterMobile='" + remitterMobile + '\'' +
                ", processFlag='" + processFlag + '\'' +
                ", typeFlag='" + typeFlag + '\'' +
                ", processedBy='" + processedBy + '\'' +
                ", processedDate='" + processedDate + '\'' +
                ", uploadDateTime='" + uploadDateTime + '\'' +
                '}';
    }



}
