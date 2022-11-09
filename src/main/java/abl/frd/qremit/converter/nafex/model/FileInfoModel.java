package abl.frd.qremit.converter.nafex.model;

import javax.persistence.*;

@Entity
@Table(name="upload_file_info")
public class FileInfoModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    @Column(name = "exchange_code")
    private String exchangeCode;
    @Column(name = "upload_date")
    private String uploadDate;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "coc_count")
    private String cocCount;
    @Column(name = "beftn_count")
    private String beftnCount;
    @Column(name = "online_count")
    private String onlineCount;
    @Column(name = "account_payee_count")
    private String accountPayeeCount;
    @Column(name = "unprocessed_count")
    private String unprocessedCount;
    @Column(name = "processed_count")
    private String processedCount;

    public FileInfoModel() {

    }

    @Override
    public String toString() {
        return "FileInfoModel{" +
                "id=" + id +
                ", exchangeCode='" + exchangeCode + '\'' +
                ", uploadDate='" + uploadDate + '\'' +
                ", fileName='" + fileName + '\'' +
                ", cocCount='" + cocCount + '\'' +
                ", beftnCount='" + beftnCount + '\'' +
                ", onlineCount='" + onlineCount + '\'' +
                ", accountPayeeCount='" + accountPayeeCount + '\'' +
                ", unprocessedCount='" + unprocessedCount + '\'' +
                ", processedCount='" + processedCount + '\'' +
                '}';
    }

    public FileInfoModel(long id, String exchangeCode, String uploadDate, String fileName, String cocCount, String beftnCount, String onlineCount, String accountPayeeCount, String unprocessedCount, String processedCount) {
        this.id = id;
        this.exchangeCode = exchangeCode;
        this.uploadDate = uploadDate;
        this.fileName = fileName;
        this.cocCount = cocCount;
        this.beftnCount = beftnCount;
        this.onlineCount = onlineCount;
        this.accountPayeeCount = accountPayeeCount;
        this.unprocessedCount = unprocessedCount;
        this.processedCount = processedCount;
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

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCocCount() {
        return cocCount;
    }

    public void setCocCount(String cocCount) {
        this.cocCount = cocCount;
    }

    public String getBeftnCount() {
        return beftnCount;
    }

    public void setBeftnCount(String beftnCount) {
        this.beftnCount = beftnCount;
    }

    public String getOnlineCount() {
        return onlineCount;
    }

    public void setOnlineCount(String onlineCount) {
        this.onlineCount = onlineCount;
    }

    public String getAccountPayeeCount() {
        return accountPayeeCount;
    }

    public void setAccountPayeeCount(String accountPayeeCount) {
        this.accountPayeeCount = accountPayeeCount;
    }

    public String getUnprocessedCount() {
        return unprocessedCount;
    }

    public void setUnprocessedCount(String unprocessedCount) {
        this.unprocessedCount = unprocessedCount;
    }

    public String getProcessedCount() {
        return processedCount;
    }

    public void setProcessedCount(String processedCount) {
        this.processedCount = processedCount;
    }
}
