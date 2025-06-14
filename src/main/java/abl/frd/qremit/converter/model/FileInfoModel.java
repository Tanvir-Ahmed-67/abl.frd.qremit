package abl.frd.qremit.converter.model;

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
    private String cocCount = "0";
    @Column(name = "beftn_count", length = 10)
    private String beftnCount = "0";
    @Column(name = "online_count", length = 10)
    private String onlineCount = "0";
    @Column(name = "account_payee_count", length = 10)
    private String accountPayeeCount = "0";
    @Column(name = "unprocessed_count", length = 10)
    private String unprocessedCount;
    @Column(name = "error_count", length = 10)
    private int errorCount = 0;
    @Column(name = "total_count", length = 10)
    private String totalCount = "0";
    @Column(name = "is_processed", columnDefinition = "TINYINT(1) DEFAULT 0")
    private int isProcessed;
    @Column(name = "is_settlement", columnDefinition = "TINYINT(1) DEFAULT 0")
    private int isSettlement = 0;
    @Column(name = "total_amount", length = 20)
    private String totalAmount = "0";
    //@ManyToOne(cascade=CascadeType.ALL)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name="user_id")
    @JsonIgnore 
    private User userModel;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<NafexEhMstModel> nafexEhMstModel;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<AgexSingaporeModel> agexSingaporeModel;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<EzRemitModel> ezRemitModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<RiaModel> riaModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<BecModel> becModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<NecItalyModel> necItalyModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<NecUkModel> necUkModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<ArhMalaysiaModel> agraniMalaysiaModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<SunmanModel> sunmanModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<OmanModel> omanModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<OmanKuwaitModel> omanKuwaitModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<MultinetModel> multinetModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<NblMalaysiaModel> nblMalaysiaModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<NblMaldivesModel> nblMaldivesModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    
    @JsonIgnore
    private List<SwiftModel> swiftModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
   
    @JsonIgnore
    private List<NblUsaModel> nblUsaModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<AlawnehModel> alawnehModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<AlBiladModel> alBiladModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<MerchantradeModel> merchantradeModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
   
    @JsonIgnore
    private List<BelhashaGlobalModel> belhashaGlobalModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<InstantCashModel> instantCashModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<UnimoniModel> unimoniModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<SigueModel> sigueModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<AlzadeedModel> alzadeedModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<SaibModel> saibModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<AlRostamaniModel> alRostamaniModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<ProgotiModel> progotiModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<UremitModel> uremitModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<AnbModel> anbModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<AmanModel> amanModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<KandHModel> kandhModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<StandardModel>standardModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<AlansariModel> alansariModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<EasternModel> easternModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<AlZamanModel> alZamanModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<FsieModel> fsieModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<IndexModel> indexModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<LariModel> lariModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<PrabhuModel> prabhuModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<GenericModel> genericModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<AlRajiModel> alRajiModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<ShahGlobalModel> shahGlobalModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<CityModel> cityModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<AlFardanDohaModel> alFardanDohaModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<AlFardanAbuDhabiModel> alFardanAbuDhabiModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)

    @JsonIgnore
    private List<ApiBeftnModel> apiBeftnModel;
    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<ApiT24Model> apiT24Model;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<MuzainiModel> muzainiModel;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel")
    @JsonIgnore
    private List<CocPaidModel> cocPaidModelList;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel")
    @JsonIgnore
    private List<CocModel> cocModelList;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel")
    @JsonIgnore
    private List<AccountPayeeModel> accountPayeeModelList;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel")
    @JsonIgnore
    private List<BeftnModel> beftnModelList;

    @OneToMany(cascade= { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel")
    @JsonIgnore
    private List<OnlineModel> onlineModelList;

    @OneToMany(cascade={ CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "fileInfoModel", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<ErrorDataModel> errorDataModelList;

    public List<MerchantradeModel> getMerchantradeModel() {
        return this.merchantradeModel;
    }

    public void setMerchantradeModel(List<MerchantradeModel> merchantradeModel) {
        this.merchantradeModel = merchantradeModel;
    }

    public List<SaibModel> getSaibModel() {
        return this.saibModel;
    }

    public void setSaibModel(List<SaibModel> saibModel) {
        this.saibModel = saibModel;
    }

    public List<AlRostamaniModel> getAlRostamaniModel() {
        return this.alRostamaniModel;
    }

    public List<AlFardanDohaModel> getAlFardanDohaModel() {
        return this.alFardanDohaModel;
    }

    public void setAlFardanDohaModel(List<AlFardanDohaModel> alFardanDohaModel) {
        this.alFardanDohaModel = alFardanDohaModel;
    }

    public List<AlFardanAbuDhabiModel> getAlFardanAbuDhabiModel() {
        return this.alFardanAbuDhabiModel;
    }

    public void setAlFardanAbuDhabiModel(List<AlFardanAbuDhabiModel> alFardanAbuDhabiModel) {
        this.alFardanAbuDhabiModel = alFardanAbuDhabiModel;
    }

    public List<KandHModel> getKandhModel() {
        return this.kandhModel;
    }
    public void setKandhModel(List<KandHModel> kandhModel) {
        this.kandhModel = kandhModel;
    }

    public List<AlRajiModel> getAlRajiModel() {
        return this.alRajiModel;
    }

    public void setAlRajiModel(List<AlRajiModel> alRajiModel) {
        this.alRajiModel = alRajiModel;
    }


    public List<SwiftModel> getSwiftModel() {
        return this.swiftModel;
    }

    public void setSwiftModel(List<SwiftModel> swiftModel) {
        this.swiftModel = swiftModel;
    }

    public void setAlRostamaniModel(List<AlRostamaniModel> alRostamaniModel) {
        this.alRostamaniModel = alRostamaniModel;
    }

    public List<AnbModel> getAnbModel() {
        return this.anbModel;
    }

    public void setAnbModel(List<AnbModel> anbModel) {
        this.anbModel = anbModel;
    }

    public List<ShahGlobalModel> getShahGlobalModel() {
        return this.shahGlobalModel;
    }

    public List<CityModel> getCityModel() {
        return this.cityModel;
    }

    public void setCityModel(List<CityModel> cityModel) {
        this.cityModel = cityModel;
    }

    public void setShahGlobalModel(List<ShahGlobalModel> shahGlobalModel) {
        this.shahGlobalModel = shahGlobalModel;
    }

    public List<AlBiladModel> getAlBiladModel() {
        return this.alBiladModel;
    }

    public void setAlBiladModel(List<AlBiladModel> alBiladModel) {
        this.alBiladModel = alBiladModel;
    }

    public List<IndexModel> getIndexModel() {
        return this.indexModel;
    }

    public void setIndexModel(List<IndexModel> indexModel) {
        this.indexModel = indexModel;
    }

    public List<LariModel> getLariModel() {
        return this.lariModel;
    }

    public void setLariModel(List<LariModel> lariModel) {
        this.lariModel = lariModel;
    }

    public List<PrabhuModel> getPrabhuModel() {
        return this.prabhuModel;
    }

    public void setPrabhuModel(List<PrabhuModel> prabhuModel) {
        this.prabhuModel = prabhuModel;
    }

    public List<GenericModel> getGenericModel() {
        return this.genericModel;
    }

    public void setGenericModel(List<GenericModel> genericModel) {
        this.genericModel = genericModel;
    }

    public List<UremitModel> getUremitModel() {
        return this.uremitModel;
    }

    public void setUremitModel(List<UremitModel> uremitModel) {
        this.uremitModel = uremitModel;
    }

    public List<AlZamanModel> getAlZamanModel() {
        return this.alZamanModel;
    }

    public void setAlZamanModel(List<AlZamanModel> alZamanModel) {
        this.alZamanModel = alZamanModel;
    }

    public List<FsieModel> getFsieModel() {
        return this.fsieModel;
    }

    public void setFsieModel(List<FsieModel> fsieModel) {
        this.fsieModel = fsieModel;
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

    public List<NblMaldivesModel> getNblMaldivesModel() {
        return this.nblMaldivesModel;
    }

    public void setNblMaldivesModel(List<NblMaldivesModel> nblMaldivesModel) {
        this.nblMaldivesModel = nblMaldivesModel;
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

    public List<EasternModel> getEasternModel() {
        return this.easternModel;
    }

    public void setEasternModel(List<EasternModel> easternModel) {
        this.easternModel = easternModel;
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
                ", totalAmount='" + totalAmount + '\'' +
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


    public List<MultinetModel> getMultinetModel() {
        return this.multinetModel;
    }

    public List<ProgotiModel> getProgotiModel() {
        return this.progotiModel;
    }

    public void setProgotiModel(List<ProgotiModel> progotiModel) {
        this.progotiModel = progotiModel;
    }

    public void setMultinetModel(List<MultinetModel> multinetModel) {
        this.multinetModel = multinetModel;
    }


    public List<NecItalyModel> getNecItalyModel() {
        return this.necItalyModel;
    }

    public void setNecItalyModel(List<NecItalyModel> necItalyModel) {
        this.necItalyModel = necItalyModel;
    }

   

    public List<NecUkModel> getNecUkModel() {
        return this.necUkModel;
    }

    public void setNecUkModel(List<NecUkModel> necUkModel) {
        this.necUkModel = necUkModel;
    }
    public List<SunmanModel> getSunmanModel() {
        return this.sunmanModel;
    }

    public void setSunmanModel(List<SunmanModel> sunmanModel) {
        this.sunmanModel = sunmanModel;
    }

    public List<OmanModel> getOmanModel() {
        return this.omanModel;
    }

    public void setOmanModel(List<OmanModel> omanModel) {
        this.omanModel = omanModel;
    }

    public List<OmanKuwaitModel> getOmanKuwaitModel() {
        return this.omanKuwaitModel;
    }

    public void setOmanKuwaitModel(List<OmanKuwaitModel> omanKuwaitModel) {
        this.omanKuwaitModel = omanKuwaitModel;
    }
    public List<AlawnehModel> getAlawnehModel() {
        return this.alawnehModel;
    }

    public void setAlawnehModel(List<AlawnehModel> alawnehModel) {
        this.alawnehModel = alawnehModel;
    }


    public List<BelhashaGlobalModel> getBelhashaGlobalModel() {
        return this.belhashaGlobalModel;
    }

    public void setBelhashaGlobalModel(List<BelhashaGlobalModel> belhashaGlobalModel) {
        this.belhashaGlobalModel = belhashaGlobalModel;
    }

    public List<InstantCashModel> getInstantCashModel() {
        return this.instantCashModel;
    }

    public void setInstantCashModel(List<InstantCashModel> instantCashModel) {
        this.instantCashModel = instantCashModel;
    }
    public List<UnimoniModel> getUnimoniModel() {
        return this.unimoniModel;
    }

    public void setUnimoniModel(List<UnimoniModel> unimoniModel) {
        this.unimoniModel = unimoniModel;
    }
    
    public List<AlzadeedModel> getAlzadeedModel() {
        return this.alzadeedModel;
    }

    public List<SigueModel> getSigueModel() {
        return this.sigueModel;
    }

    public void setSigueModel(List<SigueModel> sigueModel) {
        this.sigueModel = sigueModel;
    }

    public void setAlzadeedModel(List<AlzadeedModel> alzadeedModel) {
        this.alzadeedModel = alzadeedModel;
    }

    public List<NblMalaysiaModel> getNblMalaysiaModel() {
        return this.nblMalaysiaModel;
    }

    public void setNblMalaysiaModel(List<NblMalaysiaModel> nblMalaysiaModel) {
        this.nblMalaysiaModel = nblMalaysiaModel;
    }


    public List<NblUsaModel> getNblUsaModel() {
        return this.nblUsaModel;
    }

    public void setNblUsaModel(List<NblUsaModel> nblUsaModel) {
        this.nblUsaModel = nblUsaModel;
    }


    public List<AmanModel> getAmanModel() {
        return this.amanModel;
    }

    public void setAmanModel(List<AmanModel> amanModel) {
        this.amanModel = amanModel;
    }

    public List<StandardModel> getStandardModel() {
        return this.standardModel;
    }

    public void setStandardModel(List<StandardModel> standardModel) {
        this.standardModel = standardModel;
    }

    public List<AlansariModel> getAlansariModel() {
        return this.alansariModel;
    }

    public void setAlansariModel(List<AlansariModel> alansariModel) {
        this.alansariModel = alansariModel;
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

    public String getTotalAmount() {
        return this.totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setErrorDataModelList(List<ErrorDataModel> errorDataModelList) {
        this.errorDataModelList = errorDataModelList;
    }

    public List<ArhMalaysiaModel> getAgraniMalaysiaModel() {
        return this.agraniMalaysiaModel;
    }

    public void setAgraniMalaysiaModel(List<ArhMalaysiaModel> agraniMalaysiaModel) {
        this.agraniMalaysiaModel = agraniMalaysiaModel;
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
