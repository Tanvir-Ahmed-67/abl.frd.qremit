package abl.frd.qremit.converter.nafex.model;
import javax.persistence.*;
import java.util.*;
@Entity
@Table(name="user")
public class User {
    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable=false)
    private String userName;
    @Column(nullable=false, unique=true)
    private String userEmail;
    @Column(nullable=false)
    private String password;
    private boolean activeStatus;
    private String exchangeCode;
    private boolean passwordChangeRequired;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "userModel")
    private List<AccountPayeeModel> accountPayeeModel;
    @OneToMany(cascade=CascadeType.ALL, mappedBy = "userModel")
    private List<BeftnModel> beftnModel;
    @OneToMany(cascade=CascadeType.ALL, mappedBy = "userModel")
    private List<CocModel> cocModel;
    @OneToMany(cascade=CascadeType.ALL, mappedBy = "userModel")
    private List<FileInfoModel> fileInfoModels;
    @OneToMany(cascade=CascadeType.ALL, mappedBy = "userModel")
    private List<NafexEhMstModel> nafexEhMstModel;
    @OneToMany(cascade=CascadeType.ALL, mappedBy = "userModel")
    private List<OnlineModel> onlineModel;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private Set<Role> roles = new HashSet<>();

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public  int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public boolean getActiveStatus() { return activeStatus;}

    public void setActiveStatus(boolean active) {
        this.activeStatus = active;
    }

    public String getExchangeCode() {
        return exchangeCode;
    }

    public void setExchangeCode(String nrtaCode) {
        this.exchangeCode = nrtaCode;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isPasswordChangeRequired() {
        return passwordChangeRequired;
    }

    public void setPasswordChangeRequired(boolean passwordChangeRequired) {
        this.passwordChangeRequired = passwordChangeRequired;
    }
}
