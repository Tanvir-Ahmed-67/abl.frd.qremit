package abl.frd.qremit.converter.nafex.model;
import java.time.LocalDateTime;
import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name="log")
public class LogModel {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    @Column(name = "exchange_code", length = 20)
    private String exchangeCode;
    @Column(name = "user_id", length = 10)
    private String userId;
    @Column(name = "error_data_id", length = 10)
    private String errorDataId;
    @Column(name = "ip_address", length = 30)
    private String ipAddress;
    @Column(name = "info", columnDefinition = "TEXT")
    private String info;
    @Column(name = "action", length = 10)
    private String action;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExchangeCode() {
        return this.exchangeCode;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getErrorDataId() {
        return this.errorDataId;
    }

    public void setErrorDataId(String errorDataId) {
        this.errorDataId = errorDataId;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LogModel(){
        
    }

    public LogModel(String userId, String errorDataId, String exchangeCode, String action, String info, String ipAddress){
        this.userId = userId;
        this.errorDataId = errorDataId;
        this.exchangeCode = exchangeCode;
        this.action = action;
        this.info = info;
        this.ipAddress = ipAddress;
    }

    public String toString(){
        return "LogModel{" +
                "userId='" + userId + '\'' +
                ", errorDataId='" + errorDataId + '\'' +
                ", exchangeCode='" + exchangeCode + '\'' +
                ", action='" + action + '\'' +
                ", info='" + info + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }

}
