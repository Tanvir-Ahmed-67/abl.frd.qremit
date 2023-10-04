package abl.frd.qremit.converter.nafex.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="user_table")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String userName;
    private String password;
    private boolean active;
    private String roles;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "userModel")
    private List<FileInfoModel> fileInfoModel;
    @OneToMany(cascade=CascadeType.ALL, mappedBy = "userModel")
    private List<NafexEhMstModel> nafexEhMstModel;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "userModel")
    private List<CocModel> cocModelList;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "userModel")
    private List<AccountPayeeModel> accountPayeeModelList;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "userModel")
    private List<BeftnModel> beftnModelList;

    @OneToMany(cascade=CascadeType.ALL, mappedBy = "userModel")
    private List<OnlineModel> onlineModelList;

    public List<FileInfoModel> getFileInfoModel() {
        return fileInfoModel;
    }

    public void setFileInfoModel(List<FileInfoModel> fileInfoModel) {
        this.fileInfoModel = fileInfoModel;
    }

    public List<NafexEhMstModel> getNafexEhMstModel() {
        return nafexEhMstModel;
    }

    public void setNafexEhMstModel(List<NafexEhMstModel> nafexEhMstModel) {
        this.nafexEhMstModel = nafexEhMstModel;
    }

    public List<CocModel> getCocModelList() {
        return cocModelList;
    }

    public void setCocModelList(List<CocModel> cocModelList) {
        this.cocModelList = cocModelList;
    }

    public List<AccountPayeeModel> getAccountPayeeModelList() {
        return accountPayeeModelList;
    }

    public void setAccountPayeeModelList(List<AccountPayeeModel> accountPayeeModelList) {
        this.accountPayeeModelList = accountPayeeModelList;
    }

    public List<BeftnModel> getBeftnModelList() {
        return beftnModelList;
    }

    public void setBeftnModelList(List<BeftnModel> beftnModelList) {
        this.beftnModelList = beftnModelList;
    }

    public List<OnlineModel> getOnlineModelList() {
        return onlineModelList;
    }

    public void setOnlineModelList(List<OnlineModel> onlineModelList) {
        this.onlineModelList = onlineModelList;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
