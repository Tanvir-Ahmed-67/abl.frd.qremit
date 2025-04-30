package abl.frd.qremit.converter.model;
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
    @Column(name = "data_id", length = 10)
    private String dataId;
    @Column(name = "ip_address", length = 30)
    private String ipAddress;
    @Column(name = "info", columnDefinition = "TEXT")
    private String info;
    @Column(name = "action", length = 10)
    private String action;
    @Column(name = "file_info_model_id", length = 10)
    private int fileInfoModelId;
    
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

    public String getDataId() {
        return this.dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
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


    public int getFileInfoModelId() {
        return this.fileInfoModelId;
    }

    public void setFileInfoModelId(int fileInfoModelId) {
        this.fileInfoModelId = fileInfoModelId;
    }
    

    public LogModel(){
        
    }
    /*
     *action: 1- error data update, 2 - error data delete, 3 - file delete, 4- individual data update, 5- individual data delete
     */
    public LogModel(String userId, String dataId, int fileInfoModelId, String exchangeCode, String action, String info, String ipAddress){
        this.userId = userId;
        this.dataId = dataId;
        this.exchangeCode = exchangeCode;
        this.action = action;
        this.info = info;
        this.ipAddress = ipAddress;
        this.fileInfoModelId = fileInfoModelId;
    }

    public String toString(){
        return "LogModel{" +
                "userId='" + userId + '\'' +
                ", dataId='" + dataId + '\'' +
                ", exchangeCode='" + exchangeCode + '\'' +
                ", action='" + action + '\'' +
                ", info='" + info + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }

}
