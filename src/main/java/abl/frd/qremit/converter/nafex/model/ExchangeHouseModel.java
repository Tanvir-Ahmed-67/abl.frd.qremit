package abl.frd.qremit.converter.nafex.model;

import javax.persistence.*;

@Entity
@Table(name="ex_house_list")
public class ExchangeHouseModel {
    @Id
    @Column(name = "row_id", nullable = false)
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
    @Column
    private boolean activeStatus;

    public boolean getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(boolean isActive) {
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
                ", isActive='" + activeStatus + '\'' +
                '}';
    }
}
