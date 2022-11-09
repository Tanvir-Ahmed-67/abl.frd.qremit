package abl.frd.qremit.converter.nafex.model;

import javax.persistence.*;

@Entity
@Table(name="converted_data_beftn")
public class BeftnModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    @Column(name = "eh_mst_id")
    private long ehMstId;
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
    @JoinColumn(name = "ehMstId", referencedColumnName = "id")
    private NafexEhMstModel nafexEhMstModel;

    public NafexEhMstModel getNafexEhMstModel() {
        return nafexEhMstModel;
    }

    public void setNafexEhMstModel(NafexEhMstModel nafexEhMstModel) {
        this.nafexEhMstModel = nafexEhMstModel;
    }

    public BeftnModel() {

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

    public BeftnModel(long id, long ehMstId, String transactionNo, String orgCustomerNo, String orgName, String orgAccountNo, String orgAccountType, Double amount, String beneficiaryName, String beneficiaryAccount, String beneficiaryAccountType, String exchangeCode, String routingNo, Double incentive, String extraA, String extraB, String extraC, String extraD, String extraE) {
        this.id = id;
        this.ehMstId = ehMstId;
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
        this.extraA = extraA;
        this.extraB = extraB;
        this.extraC = extraC;
        this.extraD = extraD;
        this.extraE = extraE;
    }

    @Override
    public String toString() {
        return "BeftnModel{" +
                "id=" + id +
                ", ehMstId=" + ehMstId +
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
                ", extraA='" + extraA + '\'' +
                ", extraB='" + extraB + '\'' +
                ", extraC='" + extraC + '\'' +
                ", extraD='" + extraD + '\'' +
                ", extraE='" + extraE + '\'' +
                '}';
    }
}
