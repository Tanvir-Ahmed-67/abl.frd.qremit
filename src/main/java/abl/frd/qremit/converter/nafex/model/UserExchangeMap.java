package abl.frd.qremit.converter.nafex.model;
import javax.persistence.*;
@Entity
@Table(name="user_exchange_map", indexes = { @Index(name = "idx_exchange_code", columnList = "exchange_code") },
    uniqueConstraints = @UniqueConstraint(name = "key", columnNames = {"exchange_code", "user_id"})
)
public class UserExchangeMap {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    @Column(name = "exchange_code", length = 20)
    private String exchangeCode;
    @Column(name = "user_id")
    private int userId;
    @Column(name = "published", columnDefinition = "TINYINT(1) DEFAULT 0")
    private int published = 0;

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

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPublished() {
        return this.published;
    }

    public void setPublished(int published) {
        this.published = published;
    }

    public UserExchangeMap() {
    }

    public UserExchangeMap(String exchangeCode, int userId, int published) {
        this.exchangeCode = exchangeCode;
        this.userId = userId;
        this.published = published;
    }


    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", exchangeCode='" + getExchangeCode() + "'" +
            ", userId='" + getUserId() + "'" +
            ", published='" + getPublished() + "'" +
            "}";
    }

}
