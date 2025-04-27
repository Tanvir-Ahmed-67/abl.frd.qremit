package abl.frd.qremit.converter.model;
import javax.persistence.*;

@Entity
@Table(name="ip_range")
public class IpRange {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    @Column(name="start_ip", length = 20)
    private String startIp;
    @Column(name="end_ip", length = 20)
    private String endIp;
    @Column(name="cidr", length = 20)
    private String cidr;
    @Column(name = "published", columnDefinition = "TINYINT(1) DEFAULT 0")
    private int published;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartIp() {
        return this.startIp;
    }

    public void setStartIp(String startIp) {
        this.startIp = startIp;
    }

    public String getEndIp() {
        return this.endIp;
    }

    public void setEndIp(String endIp) {
        this.endIp = endIp;
    }

    public String getCidr() {
        return this.cidr;
    }

    public void setCidr(String cidr) {
        this.cidr = cidr;
    }

    public int getPublished() {
        return this.published;
    }

    public void setPublished(int published) {
        this.published = published;
    }

}
