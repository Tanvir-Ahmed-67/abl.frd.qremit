package abl.frd.qremit.converter.nafex.model;
import javax.persistence.*;

@Entity
@Table(name="base_data_table_nafex")
public class NafexEhMstModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long  id;
    @Column(name = "exchange_code")
    private String exchangeCode;
    @Column(name = "transaction_no")
    private String transactionNo;
    @Column(name = "currency")
    private String currency;
    @Column(name = "amount")
    private Double amount;
    @Column(name = "entered_date")
    private String enteredDate;
    @Column(name = "remitter_name")
    private String remitterName;
    @Column(name = "beneficiary_name")
    private String beneficiaryName;
    @Column(name = "beneficiary_account_no")
    private String beneficiaryAccount;
    @Column(name = "beneficiary_mobile_no")
    private String beneficiaryMobile;
    @Column(name = "bank_name")
    private String bankName;
    @Column(name = "bank_code")
    private String bankCode;
    @Column(name = "branch_name")
    private String branchName;
    @Column(name = "branch_code")
    private String branchCode;
    @Column(name = "drawee_branch_name")
    private String draweeBranchName;
    @Column(name = "drawee_branch_code")
    private String draweeBranchCode;
    @Column(name = "purpose_of_remittance")
    private String purposeOfRemittance;
    @Column(name = "source_of_income")
    private String sourceOfIncome;
    @Column(name = "remitter_mobile_no")
    private String remitterMobile;
    @Column(name = "process_flag")
    private String processFlag;
    @Column(name = "type_flag")
    private String typeFlag;

    @Column(name = "processed_by")
    private String processedBy;
    @Column(name = "processed_date")
    private String processedDate;
    @Column(name = "extra_c")
    private String extraC;

    public NafexEhMstModel() {

    }

    @Override
    public String toString() {
        return "NafexEhMstModel{" +
                "id=" + id +
                ", exchangeCode='" + exchangeCode + '\'' +
                ", transactionNo='" + transactionNo + '\'' +
                ", currency='" + currency + '\'' +
                ", amount=" + amount +
                ", enteredDate='" + enteredDate + '\'' +
                ", remitterName='" + remitterName + '\'' +
                ", beneficiaryName='" + beneficiaryName + '\'' +
                ", beneficiaryAccount='" + beneficiaryAccount + '\'' +
                ", beneficiaryMobile='" + beneficiaryMobile + '\'' +
                ", bankName='" + bankName + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", branchName='" + branchName + '\'' +
                ", branchCode='" + branchCode + '\'' +
                ", draweeBranchName='" + draweeBranchName + '\'' +
                ", draweeBranchCode='" + draweeBranchCode + '\'' +
                ", purposeOfRemittance='" + purposeOfRemittance + '\'' +
                ", sourceOfIncome='" + sourceOfIncome + '\'' +
                ", remitterMobile='" + remitterMobile + '\'' +
                ", processFlag='" + processFlag + '\'' +
                ", typeFlag='" + typeFlag + '\'' +
                ", processedBy='" + processedBy + '\'' +
                ", processedDate='" + processedDate + '\'' +
                ", extraC='" + extraC + '\'' +
                '}';
    }

    public NafexEhMstModel(long id, String exchangeCode, String transactionNo, String currency, Double amount, String enteredDate, String remitterName, String beneficiaryName, String beneficiaryAccount, String beneficiaryMobile, String bankName, String bankCode, String branchName, String branchCode, String draweeBranchName, String draweeBranchCode, String purposeOfRemittance, String sourceOfIncome, String remitterMobile, String processFlag, String typeFlag, String processedBy, String processedDate, String extraC) {
        this.id = id;
        this.exchangeCode = exchangeCode;
        this.transactionNo = transactionNo;
        this.currency = currency;
        this.amount = amount;
        this.enteredDate = enteredDate;
        this.remitterName = remitterName;
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
        this.remitterMobile = remitterMobile;
        this.processFlag = processFlag;
        this.typeFlag = typeFlag;
        this.processedBy = processedBy;
        this.processedDate = processedDate;
        this.extraC = extraC;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getExtraC() {
        return extraC;
    }

    public void setExtraC(String extraC) {
        this.extraC = extraC;
    }
}

