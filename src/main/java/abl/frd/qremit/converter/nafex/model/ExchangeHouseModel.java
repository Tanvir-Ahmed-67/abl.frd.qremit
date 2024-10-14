package abl.frd.qremit.converter.nafex.model;

import javax.persistence.*;

@Entity
@Table(name="ex_house_list",
    indexes = { @Index(name = "idx_is_settlement", columnList = "is_settlement") }
)
public class ExchangeHouseModel {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    @Column(nullable=false)
    private String exchangeName;
    @Column(nullable=false)
    private String nrtaCode;
    @Column(nullable=false)
    private String exchangeCode;
    @Column(nullable=false)
    private String exchangeShortName;
    @Column(nullable=false)
    private String baseTableName;

    @Column(name = "active_status", columnDefinition = "TINYINT(1) DEFAULT 0")
    private int activeStatus = 0;
    @Column(name = "is_settlement", columnDefinition = "TINYINT(1) DEFAULT 0")
    private int isSettlement = 0;

    @Column(name = "class_name", length = 32, nullable = false)
    private String className;
    @Column(name = "repository_name", length = 32, nullable = false)
    private String repositoryName;
    public String getClassName() {
        return this.className;
    }
    public void setClassName(String className) {
        this.className = className;
    }

    public String getRepositoryName() {
        return this.repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }


    public int getIsSettlement() {
        return this.isSettlement;
    }

    public void setIsSettlement(int isSettlement) {
        this.isSettlement = isSettlement;
    }


    public int getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(int isActive) {
        this.activeStatus = isActive;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getNrtaCode() {
        return nrtaCode;
    }

    public void setNrtaCode(String nrtaCode) {
        this.nrtaCode = nrtaCode;
    }

    public String getExchangeCode() {
        return exchangeCode;
    }

    public void setBaseTableName(String baseTableName){
        this.baseTableName = baseTableName;
    }

    public String getBaseTableName() {
        return baseTableName;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    public String getExchangeShortName() {
        return exchangeShortName;
    }

    public void setExchangeShortName(String exchangeShortName) {
        this.exchangeShortName = exchangeShortName;
    }

    @Override
    public String toString() {
        return "ExchangeHouseModel{" +
                "Id=" + Id +
                ", exchangeName='" + exchangeName + '\'' +
                ", nrtaCode='" + nrtaCode + '\'' +
                ", exchangeCode='" + exchangeCode + '\'' +
                ", exchangeShortName='" + exchangeShortName + '\'' +
                ", baseTableName='" + baseTableName + '\'' +
                ", isActive='" + activeStatus + '\'' +
                '}';
    }
}
