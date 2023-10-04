package abl.frd.qremit.converter.nafex.model;

import javax.persistence.*;
import java.util.List;

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
    @Column(name = "total_count")
    private String totalCount;

    /*
    @ManyToOne
    @JoinColumn(name = "eh_mst_id", referencedColumnName = "id")
    private NafexEhMstModel nafexEhMstModel;
*/
    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="user_id")
    private UserModel userModel;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "fileInfoModel")
    private List<NafexEhMstModel> nafexEhMstModel;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "fileInfoModel")
    private List<CocModel> cocModelList;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "fileInfoModel")
    private List<AccountPayeeModel> accountPayeeModelList;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "fileInfoModel")
    private List<BeftnModel> beftnModelList;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "fileInfoModel")
    private List<OnlineModel> onlineModelList;

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public List<OnlineModel> getOnlineModelList() {
        return onlineModelList;
    }

    public void setOnlineModelList(List<OnlineModel> onlineModelList) {
        this.onlineModelList = onlineModelList;
    }

    public List<BeftnModel> getBeftnModelList() {
        return beftnModelList;
    }

    public void setBeftnModelList(List<BeftnModel> beftnModelList) {
        this.beftnModelList = beftnModelList;
    }

    public List<AccountPayeeModel> getAccountPayeeModelList() {
        return accountPayeeModelList;
    }

    public void setAccountPayeeModelList(List<AccountPayeeModel> accountPayeeModelList) {
        this.accountPayeeModelList = accountPayeeModelList;
    }

    public List<CocModel> getCocModelList() {
        return cocModelList;
    }

    public void setCocModelList(List<CocModel> cocModelList) {
        this.cocModelList = cocModelList;
    }

    public FileInfoModel(String exchangeCode, String uploadDate, String fileName, String cocCount, String beftnCount, String onlineCount, String accountPayeeCount, String unprocessedCount, String processedCount, String totalCount, List<NafexEhMstModel> nafexEhMstModelSet) {
        this.exchangeCode = exchangeCode;
        this.uploadDate = uploadDate;
        this.fileName = fileName;
        this.cocCount = cocCount;
        this.beftnCount = beftnCount;
        this.onlineCount = onlineCount;
        this.accountPayeeCount = accountPayeeCount;
        this.unprocessedCount = unprocessedCount;
        this.processedCount = processedCount;
        this.totalCount = totalCount;
        this.nafexEhMstModel = nafexEhMstModelSet;
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
                ", totalCount='" + totalCount + '\'' +
                ", nafexEhMstModelSet=" + nafexEhMstModel +
                '}';
    }

    public List<NafexEhMstModel> getNafexEhMstModel() {
        return nafexEhMstModel;
    }

    public void setNafexEhMstModel(List<NafexEhMstModel> nafexEhMstModelSet) {
        this.nafexEhMstModel = nafexEhMstModelSet;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public FileInfoModel() {

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
