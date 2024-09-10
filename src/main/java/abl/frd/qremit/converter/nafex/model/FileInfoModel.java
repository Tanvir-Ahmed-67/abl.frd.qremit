package abl.frd.qremit.converter.nafex.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="upload_file_info", uniqueConstraints = @UniqueConstraint(columnNames = "file_name"))
public class FileInfoModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    @Column(name = "exchange_code")
    private String exchangeCode;
    @Column(name = "upload_date_time")
    private LocalDateTime uploadDateTime;
    @Column(name = "file_name", unique = true, nullable = false)
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

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="user_id")
    @JsonIgnore 
    private User userModel;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<NafexEhMstModel> nafexEhMstModel;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<AgexSingaporeModel> agexSingaporeModel;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<EzRemitModel> ezRemitModel;
    @OneToMany(cascade=CascadeType.ALL, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<RiaModel> riaModel;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<BecModel> becModel;
    @OneToMany(cascade=CascadeType.ALL, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<ApiBeftnModel> apiBeftnModel;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<ApiT24Model> apiT24Model;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<MuzainiModel> muzainiModel;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "fileInfoModel")
    @JsonIgnore
    private List<CocModel> cocModelList;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "fileInfoModel")
    @JsonIgnore
    private List<AccountPayeeModel> accountPayeeModelList;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "fileInfoModel")
    @JsonIgnore
    private List<BeftnModel> beftnModelList;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "fileInfoModel")
    @JsonIgnore
    private List<OnlineModel> onlineModelList;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<ErrorDataModel> errorDataModelList;

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

    public List<ErrorDataModel> getErrorDataModelList() {
        return errorDataModelList;
    }

    public void seterrorDataModelList(List<ErrorDataModel> errorDataModelList ) {
        this.errorDataModelList = errorDataModelList;
    }

    public FileInfoModel(String exchangeCode, LocalDateTime uploadDateTime, String fileName, String cocCount, String beftnCount, String onlineCount, String accountPayeeCount, String unprocessedCount, String processedCount, String totalCount) {
        this.exchangeCode = exchangeCode;
        this.uploadDateTime = uploadDateTime;
        this.fileName = fileName;
        this.cocCount = cocCount;
        this.beftnCount = beftnCount;
        this.onlineCount = onlineCount;
        this.accountPayeeCount = accountPayeeCount;
        this.unprocessedCount = unprocessedCount;
        this.processedCount = processedCount;
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return "FileInfoModel{" +
                "id=" + id +
                ", exchangeCode='" + exchangeCode + '\'' +
                ", uploadDate='" + uploadDateTime + '\'' +
                ", fileName='" + fileName + '\'' +
                ", cocCount='" + cocCount + '\'' +
                ", beftnCount='" + beftnCount + '\'' +
                ", onlineCount='" + onlineCount + '\'' +
                ", accountPayeeCount='" + accountPayeeCount + '\'' +
                ", unprocessedCount='" + unprocessedCount + '\'' +
                ", processedCount='" + processedCount + '\'' +
                ", totalCount='" + totalCount + '\'' +
                '}';
    }

    public User getUserModel() {
        return userModel;
    }

    public void setUserModel(User userModel) {
        this.userModel = userModel;
    }

    public List<NafexEhMstModel> getNafexEhMstModel() {
        return nafexEhMstModel;
    }

    public void setNafexEhMstModel(List<NafexEhMstModel> nafexEhMstModelSet) {
        this.nafexEhMstModel = nafexEhMstModelSet;
    }

    public void setBecModel(List<BecModel> becModelSet) {
        this.becModel = becModelSet;
    }

    public void setMuzainiModel(List<MuzainiModel> muzainiModelSet) {
        this.muzainiModel = muzainiModelSet;
    }

    

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public FileInfoModel() {

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

    public LocalDateTime getUploadDateTime() {
        return uploadDateTime;
    }

    public void setUploadDateTime(LocalDateTime uploadDate) {
        this.uploadDateTime = uploadDate;
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

    public List<AgexSingaporeModel> getAgexSingaporeModel() {
        return agexSingaporeModel;
    }

    public void setAgexSingaporeModel(List<AgexSingaporeModel> agexSingaporeModel) {
        this.agexSingaporeModel = agexSingaporeModel;
    }

    public List<EzRemitModel> getEzRemitModel() {
        return ezRemitModel;
    }

    public void setEzRemitModel(List<EzRemitModel> ezRemitModel) {
        this.ezRemitModel = ezRemitModel;
    }

    public List<RiaModel> getRiaModel() {
        return riaModel;
    }

    public void setRiaModel(List<RiaModel> riaModel) {
        this.riaModel = riaModel;
    }

    public List<ApiBeftnModel> getApiBeftnModel() {
        return apiBeftnModel;
    }

    public void setApiBeftnModel(List<ApiBeftnModel> apiBeftnModel) {
        this.apiBeftnModel = apiBeftnModel;
    }

    public List<ApiT24Model> getApiT24Model() {
        return apiT24Model;
    }

    public void setApiT24Model(List<ApiT24Model> apiT24Model) {
        this.apiT24Model = apiT24Model;
    }
}
