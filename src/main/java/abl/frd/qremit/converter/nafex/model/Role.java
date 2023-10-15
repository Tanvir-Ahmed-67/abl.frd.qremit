package abl.frd.qremit.converter.nafex.model;


import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name="role")
public class Role {
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    private String roleName;
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<User> users;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(Collection<User> users) {
        this.users = users;
    }
}
