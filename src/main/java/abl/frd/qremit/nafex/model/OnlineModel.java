package abl.frd.qremit.nafex.model;

import javax.persistence.*;

@Entity
@Table(name="converted_data_online")
public class OnlineModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    @Column(name = "eh_mst_id")
    private long ehMstId;
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

    public OnlineModel() {

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

    public OnlineModel(long id, long ehMstId, String transactionNo, String exchangeCode, String beneficiaryName, String beneficiaryAccount, Double amount, String remitterName, String extraA, String extraB, String extraC, String extraD, String extraE) {
        this.id = id;
        this.ehMstId = ehMstId;
        this.transactionNo = transactionNo;
        this.exchangeCode = exchangeCode;
        this.beneficiaryName = beneficiaryName;
        this.beneficiaryAccount = beneficiaryAccount;
        this.amount = amount;
        this.remitterName = remitterName;
        this.extraA = extraA;
        this.extraB = extraB;
        this.extraC = extraC;
        this.extraD = extraD;
        this.extraE = extraE;
    }

    @Override
    public String toString() {
        return "OnlineModel{" +
                "id=" + id +
                ", ehMstId=" + ehMstId +
                ", transactionNo='" + transactionNo + '\'' +
                ", exchangeCode='" + exchangeCode + '\'' +
                ", beneficiaryName='" + beneficiaryName + '\'' +
                ", beneficiaryAccount='" + beneficiaryAccount + '\'' +
                ", amount=" + amount +
                ", remitterName='" + remitterName + '\'' +
                ", extraA='" + extraA + '\'' +
                ", extraB='" + extraB + '\'' +
                ", extraC='" + extraC + '\'' +
                ", extraD='" + extraD + '\'' +
                ", extraE='" + extraE + '\'' +
                '}';
    }

}
