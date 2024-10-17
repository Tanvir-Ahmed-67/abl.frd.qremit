package abl.frd.qremit.converter.nafex.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="upload_file_info", 
    uniqueConstraints = @UniqueConstraint(columnNames = "file_name"),
    indexes = { @Index(name = "idx_exchange_code", columnList = "exchange_code"),
        @Index(name = "idx_upload_date_time", columnList = "upload_date_time"),
        @Index(name = "idx_is_settlement", columnList = "is_settlement"),
        @Index(name = "idx_total_count", columnList = "total_count"),
        @Index(name = "idx_error_count", columnList = "error_count"),
    }
)
public class FileInfoModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    @Column(name = "exchange_code", length = 20)
    private String exchangeCode;
    @Column(name = "upload_date_time", columnDefinition = "DATETIME")
    private LocalDateTime uploadDateTime;
    @Column(name = "file_name", unique = true, nullable = false)
    private String fileName;
    @Column(name = "coc_count", length = 10)
    private String cocCount;
    @Column(name = "beftn_count", length = 10)
    private String beftnCount;
    @Column(name = "online_count", length = 10)
    private String onlineCount;
    @Column(name = "account_payee_count", length = 10)
    private String accountPayeeCount;
    @Column(name = "unprocessed_count", length = 10)
    private String unprocessedCount;
    @Column(name = "error_count", length = 10)
    private int errorCount = 0;
    @Column(name = "total_count", length = 10)
    private String totalCount;
    @Column(name = "is_processed", columnDefinition = "TINYINT(1) DEFAULT 0")
    private int isProcessed;
    @Column(name = "is_settlement", columnDefinition = "TINYINT(1) DEFAULT 0")
    private int isSettlement = 0;

    //@ManyToOne(cascade=CascadeType.ALL)
    @ManyToOne(cascade = CascadeType.PERSIST)
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

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel")
    //@OneToMany(cascade = CascadeType.ALL, mappedBy = "fileInfoModel")
    @JsonIgnore
    private List<CocModel> cocModelList;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel")
    @JsonIgnore
    private List<CocPaidModel> cocPaidModelList;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel")
    //@OneToMany(cascade = CascadeType.ALL, mappedBy = "fileInfoModel")
    @JsonIgnore
    private List<AccountPayeeModel> accountPayeeModelList;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel")
    //@OneToMany(cascade = CascadeType.ALL, mappedBy = "fileInfoModel")
    @JsonIgnore
    private List<BeftnModel> beftnModelList;

    @OneToMany(cascade= { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel")
    //@OneToMany(cascade = CascadeType.ALL, mappedBy = "fileInfoModel")
    @JsonIgnore
    private List<OnlineModel> onlineModelList;

    @OneToMany(cascade={ CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
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

    public FileInfoModel(String exchangeCode, LocalDateTime uploadDateTime, String fileName, String cocCount, String beftnCount, String onlineCount, String accountPayeeCount, String unprocessedCount, int isProcessed, String totalCount, int errorCount, int isSettlement) {
        this.exchangeCode = exchangeCode;
        this.uploadDateTime = uploadDateTime;
        this.fileName = fileName;
        this.cocCount = cocCount;
        this.beftnCount = beftnCount;
        this.onlineCount = onlineCount;
        this.accountPayeeCount = accountPayeeCount;
        this.unprocessedCount = unprocessedCount;
        this.isSettlement = isSettlement;
        this.errorCount = errorCount;
        this.totalCount = totalCount;
        this.isProcessed = isProcessed;
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
                ", errorCount='" + errorCount + '\'' +
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

    public int getIsProcessed() {
        return this.isProcessed;
    }

    public void setIsProcessed(int isProcessed) {
        this.isProcessed = isProcessed;
    }

    public int getErrorCount() {
        return this.errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }
    
    public int getIsSettlement() {
        return this.isSettlement;
    }

    public void setIsSettlement(int isSettlement) {
        this.isSettlement = isSettlement;
    }

    public List<BecModel> getBecModel() {
        return this.becModel;
    }

    public List<MuzainiModel> getMuzainiModel() {
        return this.muzainiModel;
    }

    public void setErrorDataModelList(List<ErrorDataModel> errorDataModelList) {
        this.errorDataModelList = errorDataModelList;
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

    public List<CocPaidModel> getCocPaidModelList() {
        return cocPaidModelList;
    }

    public void setCocPaidModelList(List<CocPaidModel> cocPaidModelList) {
        this.cocPaidModelList = cocPaidModelList;
    }
}
