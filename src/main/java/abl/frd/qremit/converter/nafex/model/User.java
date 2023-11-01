package abl.frd.qremit.converter.nafex.model;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="user")
public class User {
    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String userName;
    private String userEmail;
    private String password;
    private boolean status;
    private String nrtaCode;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "userModel")
    private List<FileInfoModel> fileInfoModels;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
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

    public int getId() {
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
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean active) {
        this.status = active;
    }

    public String getNrtaCode() {
        return nrtaCode;
    }

    public void setNrtaCode(String nrtaCode) {
        this.nrtaCode = nrtaCode;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
