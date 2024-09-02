package abl.frd.qremit.converter.nafex.model;

import javax.persistence.Column;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

public class ExchangeReportDTO {
    private String exchangeCode;
    private String exchangeName;
    private String transactionNo;
    private String currency;
    private Double amount;
    private LocalDateTime enteredDate;
    private String remitterName;
    private String remitterMobile;
    private String beneficiaryName;
    private String beneficiaryAccount;
    private String beneficiaryMobile;

    private Double sumOfAmount = 0.00;
    private int totalRowCount = 0;

    public DecimalFormat formattedAmount = new DecimalFormat("#,##,###.00");

    public String doFormatAmount(Double amount){
        return formattedAmount.format(amount);
    }
    public ExchangeReportDTO(){}

    public ExchangeReportDTO(String exchangeCode, String transactionNo, Double amount, String beneficiaryName, String beneficiaryAccount, LocalDateTime enteredDate) {
        this.exchangeCode = exchangeCode;
        this.transactionNo = transactionNo;
        this.amount = amount;
        this.beneficiaryName = beneficiaryName;
        this.beneficiaryAccount = beneficiaryAccount;
        this.enteredDate = enteredDate;
    }
    public void doSum(Double amount){
        this.sumOfAmount = this.sumOfAmount+amount;
    }
    public void doCount(){
        this.totalRowCount = this.totalRowCount+1;
    }

    public double getTotalAmountCount(){
        return this.sumOfAmount;
    }
    public int getTotalRowCount(){
        return this.totalRowCount;
    }

    public String getExchangeCode() {
        return exchangeCode;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
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

    public LocalDateTime getEnteredDate() {
        return enteredDate;
    }

    public void setEnteredDate(LocalDateTime enteredDate) {
        this.enteredDate = enteredDate;
    }

    public String getRemitterName() {
        return remitterName;
    }

    public void setRemitterName(String remitterName) {
        this.remitterName = remitterName;
    }

    public String getRemitterMobile() {
        return remitterMobile;
    }

    public void setRemitterMobile(String remitterMobile) {
        this.remitterMobile = remitterMobile;
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
}
