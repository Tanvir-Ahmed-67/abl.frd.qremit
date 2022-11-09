package abl.frd.qremit.nafex.model;

import javax.persistence.*;
@Entity
@Table(name="converted_data_coc")
public class CocModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    @Column(name = "eh_mst_id")
    private long ehMstId;
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
    @Column(name = "coc_code")
    private String cocCode;
    @Column(name = "extra_a")
    private String extraA;
    @Column(name = "extra_b")
    private String extraB;
    @Column(name = "extra_c")
    private String extraC;
    @Column(name = "extra_d")
    private String extraD;
    @Column(name = "extra_e")
    private String extraE;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "eh_mst_id", referencedColumnName = "id")
    private NafexEhMstModel nafexEhMstModel;

    public NafexEhMstModel getNafexEhMstModel() {
        return nafexEhMstModel;
    }

    public void setNafexEhMstModel(NafexEhMstModel nafexEhMstModel) {
        this.nafexEhMstModel = nafexEhMstModel;
    }

    public CocModel() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEhMstId() {
        return ehMstId;
    }

    public void setEhMstId(long ehMstId) {
        this.ehMstId = ehMstId;
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

    public String getCocCode() {
        return cocCode;
    }

    public void setCocCode(String cocCode) {
        this.cocCode = cocCode;
    }

    public String getExtraA() {
        return extraA;
    }

    public void setExtraA(String extraA) {
        this.extraA = extraA;
    }

    public String getExtraB() {
        return extraB;
    }

    public void setExtraB(String extraB) {
        this.extraB = extraB;
    }

    public String getExtraC() {
        return extraC;
    }

    public void setExtraC(String extraC) {
        this.extraC = extraC;
    }

    public String getExtraD() {
        return extraD;
    }

    public void setExtraD(String extraD) {
        this.extraD = extraD;
    }

    public String getExtraE() {
        return extraE;
    }

    public void setExtraE(String extraE) {
        this.extraE = extraE;
    }

    public CocModel(long id, long ehMstId, String transactionNo, String creditMark, String enteredDate, String currency, Double amount, String beneficiaryName, String exchangeCode, String bankName, String bankCode, String branchName, String branchCode, String beneficiaryAccount, String remitterName, Double incentive, String cocCode, String extraA, String extraB, String extraC, String extraD, String extraE) {
        this.id = id;
        this.ehMstId = ehMstId;
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
        this.cocCode = cocCode;
        this.extraA = extraA;
        this.extraB = extraB;
        this.extraC = extraC;
        this.extraD = extraD;
        this.extraE = extraE;
    }

    @Override
    public String toString() {
        return "CocModel{" +
                "id=" + id +
                ", ehMstId=" + ehMstId +
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
                ", cocCode='" + cocCode + '\'' +
                ", extraA='" + extraA + '\'' +
                ", extraB='" + extraB + '\'' +
                ", extraC='" + extraC + '\'' +
                ", extraD='" + extraD + '\'' +
                ", extraE='" + extraE + '\'' +
                '}';
    }
}
