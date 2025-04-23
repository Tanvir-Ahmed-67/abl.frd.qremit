package abl.frd.qremit.converter.model;
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
    @Column(name="login_id", length=32, nullable=false, unique=true)
    private String loginId;
    @Column(nullable=false)
    private String password;
    private boolean activeStatus;
    @Column(name ="exchange_code", columnDefinition = "TEXT")
    private String exchangeCode;
    private boolean passwordChangeRequired;
    @Column(name="mobile_no")
    private String mobileNo;

    @Column(name="allowed_ips")
    private String allowedIps;

    @Column(name="start_time")
    private String startTime;

    @Column(name="end_time")
    private String endTime;

    @Column(name="failed_attempt")
    private int failedAttempt;

    public int getFailedAttempt() {
        return failedAttempt;
    }
    public void setFailedAttempt(int failedAttempt) {
        this.failedAttempt = failedAttempt;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAllowedIps() {
        return allowedIps;
    }

    public void setAllowedIps(String allowedIps) {
        this.allowedIps = allowedIps;
    }

    public String getLoginId() {
        return this.loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public boolean isActiveStatus() {
        return this.activeStatus;
    }

    public boolean getPasswordChangeRequired() {
        return this.passwordChangeRequired;
    }

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "userModel")
    private List<AccountPayeeModel> accountPayeeModel;
    @OneToMany(cascade=CascadeType.ALL, mappedBy = "userModel")
    private List<BeftnModel> beftnModel;
    @OneToMany(cascade=CascadeType.ALL, mappedBy = "userModel")
    private List<CocModel> cocModel;
    @OneToMany(cascade= { CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "userModel")
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

    public String getMobileNo(){
        return mobileNo;
    }

    public void setMobileNo(String mobileNo){
        this.mobileNo = mobileNo;
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
