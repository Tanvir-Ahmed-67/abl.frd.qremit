package abl.frd.qremit.converter.model;

import java.text.DecimalFormat;
import java.time.LocalDate;

public class ExchangeReportDTO {
    private String exchangeCode;
    private String exchangeName;
    private String transactionNo;
    private String currency;
    private Double amount;
    private LocalDate enteredDate;
    private String enteredDateForSearchFile;
    private LocalDate voucherDate;
    private String remitterName;
    private String remitterMobile;
    private String beneficiaryName;
    private String beneficiaryAccount;
    private String beneficiaryMobile;

    private String branchCode;
    private String branchName;
    private String bankCode;
    private String bankName;
    private String zoneCode;
    private String zoneName;
    private String districtCode;
    private String districtName;
    private String purposeOfRemittance;
    private String nrtAccountNo;
    private Double sumOfAmount = 0.00;
    private int totalRowCount = 0;
    private String totalAmountInWords;

    public DecimalFormat formattedAmount = new DecimalFormat("#,##,###.00");

    public String getTotalAmountInWords() {
        return totalAmountInWords;
    }

    public void setTotalAmountInWords(String totalAmountInWords) {
        this.totalAmountInWords = totalAmountInWords;
    }

    public String getNrtAccountNo() {
        return nrtAccountNo;
    }

    public void setNrtAccountNo(String nrtAccountNo) {
        this.nrtAccountNo = nrtAccountNo;
    }

    public void setTotalRowCount(int totalRowCount) {
        this.totalRowCount = totalRowCount;
    }

    public String doFormatAmount(Double amount) {
        return formattedAmount.format(amount);
    }

    public ExchangeReportDTO() {
    }

    public ExchangeReportDTO(String exchangeCode, String transactionNo, Double amount, String beneficiaryName, String beneficiaryAccount, LocalDate enteredDate) {
        this.exchangeCode = exchangeCode;
        this.transactionNo = transactionNo;
        this.amount = amount;
        this.beneficiaryName = beneficiaryName;
        this.beneficiaryAccount = beneficiaryAccount;
        this.enteredDate = enteredDate;
    }

    public String getZoneCode() {
        return zoneCode;
    }

    public void setZoneCode(String zoneCode) {
        this.zoneCode = zoneCode;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public void doSum(Double amount) {
        this.sumOfAmount = this.sumOfAmount + amount;
    }

    public void doCount() {
        this.totalRowCount = this.totalRowCount + 1;
    }

    public double getTotalAmountCount() {
        return this.sumOfAmount;
    }

    public int getTotalRowCount() {
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

    public LocalDate getEnteredDate() {
        return enteredDate;
    }

    public void setEnteredDate(LocalDate enteredDate) {
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

    public Double getSumOfAmount() {
        return sumOfAmount;
    }

    public void setSumOfAmount(Double sumOfAmount) {
        this.sumOfAmount = sumOfAmount;
    }

    public LocalDate getVoucherDate() {
        return voucherDate;
    }

    public void setVoucherDate(LocalDate voucherDate) {
        this.voucherDate = voucherDate;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getPurposeOfRemittance() {
        return purposeOfRemittance;
    }

    public void setPurposeOfRemittance(String purposeOfRemittance) {
        this.purposeOfRemittance = purposeOfRemittance;
    }

    public String getEnteredDateForSearchFile() {
        return enteredDateForSearchFile;
    }

    public void setEnteredDateForSearchFile(String enteredDateForSearchFile) {
        this.enteredDateForSearchFile = enteredDateForSearchFile;
    }
}
