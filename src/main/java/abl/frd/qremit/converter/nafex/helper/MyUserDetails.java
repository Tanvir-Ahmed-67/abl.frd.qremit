package abl.frd.qremit.converter.nafex.helper;

import abl.frd.qremit.converter.nafex.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

public class MyUserDetails implements UserDetails {
    private String userName;
    private String password;
    private boolean status;
    private String nrtaCode;

    private List<GrantedAuthority> authorityList;

    public MyUserDetails(User user){
        this.userName = user.getUserName();
        this.password = user.getPassword();
        this.status = user.isStatus();
        this.nrtaCode = user.getNrtaCode();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorityList;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isEnabled() {
        return status;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    public boolean hasRole(String roleName){
        Collection<? extends GrantedAuthority> authoritiesList = getAuthorities();
        if(authoritiesList.toString().contains(roleName)){
            return true;
        }
        else
            return false;
    }
}
