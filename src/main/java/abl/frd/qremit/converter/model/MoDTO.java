package abl.frd.qremit.converter.model;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDate;

public class MoDTO {
    private LocalDate moDate;
    private Long totalNumberBeftn;
    private BigDecimal totalAmountBeftn;
    private Long totalNumberAllOtherBranch;
    private BigDecimal totalAmountAllOtherBranch;
    private Long totalNumberIcash;
    private BigDecimal totalAmountIcash;
    private Long totalNumberOnline;
    private BigDecimal totalAmountOnline;
    private Long totalNumberApi;
    private BigDecimal totalAmountApi;
    private Long grandTotalNumber = 0L;
    private BigDecimal grandTotalAmount = BigDecimal.ZERO;
    private String totalAmountInWords;

    public LocalDate getMoDate() {
        return moDate;
    }

    public void setMoDate(LocalDate moDate) {
        this.moDate = moDate;
    }

    public Long getTotalNumberBeftn() {
        return totalNumberBeftn;
    }

    public void setTotalNumberBeftn(Long totalNumberBeftn) {
        this.totalNumberBeftn = totalNumberBeftn;
    }

    public BigDecimal getTotalAmountBeftn() {
        return totalAmountBeftn;
    }

    public void setTotalAmountBeftn(BigDecimal totalAmountBeftn) {
        this.totalAmountBeftn = totalAmountBeftn;
    }

    public Long getTotalNumberAllOtherBranch() {
        return totalNumberAllOtherBranch;
    }

    public void setTotalNumberAllOtherBranch(Long totalNumberAllOtherBranch) {
        this.totalNumberAllOtherBranch = totalNumberAllOtherBranch;
    }

    public BigDecimal getTotalAmountAllOtherBranch() {
        return totalAmountAllOtherBranch;
    }

    public void setTotalAmountAllOtherBranch(BigDecimal totalAmountAllOtherBranch) {
        this.totalAmountAllOtherBranch = totalAmountAllOtherBranch;
    }

    public Long getTotalNumberIcash() {
        return totalNumberIcash;
    }

    public void setTotalNumberIcash(Long totalNumberIcash) {
        this.totalNumberIcash = totalNumberIcash;
    }

    public BigDecimal getTotalAmountIcash() {
        return totalAmountIcash;
    }

    public void setTotalAmountIcash(BigDecimal totalAmountIcash) {
        this.totalAmountIcash = totalAmountIcash;
    }

    public Long getTotalNumberOnline() {
        return totalNumberOnline;
    }

    public void setTotalNumberOnline(Long totalNumberOnline) {
        this.totalNumberOnline = totalNumberOnline;
    }

    public BigDecimal getTotalAmountOnline() {
        return totalAmountOnline;
    }

    public void setTotalAmountOnline(BigDecimal totalAmountOnline) {
        this.totalAmountOnline = totalAmountOnline;
    }

    public Long getTotalNumberApi() {
        return totalNumberApi;
    }

    public void setTotalNumberApi(Long totalNumberApi) {
        this.totalNumberApi = totalNumberApi;
    }

    public BigDecimal getTotalAmountApi() {
        return totalAmountApi;
    }

    public void setTotalAmountApi(BigDecimal totalAmountApi) {
        this.totalAmountApi = totalAmountApi;
    }

    public Long getGrandTotalNumber() {
        return grandTotalNumber;
    }

    public void setGrandTotalNumber(Long grandTotalNumber) {
        this.grandTotalNumber = grandTotalNumber;
    }

    public BigDecimal getGrandTotalAmount() {
        return grandTotalAmount;
    }

    public void setGrandTotalAmount(BigDecimal grandTotalAmount) {
        this.grandTotalAmount = grandTotalAmount;
    }

    public String getTotalAmountInWords() {
        return totalAmountInWords;
    }

    public void setTotalAmountInWords(String totalAmountInWords) {
        this.totalAmountInWords = totalAmountInWords;
    }

    public Long doSumGrandTotalNumber(Long totalNumber){
        return this.grandTotalNumber = this.grandTotalNumber+totalNumber;
    }
    public BigDecimal doSumGrandTotalAmount(BigDecimal totalAmount){
        return this.grandTotalAmount = this.grandTotalAmount.add(totalAmount);
    }
}
