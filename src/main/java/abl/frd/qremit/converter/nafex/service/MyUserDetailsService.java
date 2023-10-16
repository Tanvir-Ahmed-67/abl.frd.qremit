package abl.frd.qremit.converter.nafex.service;

import abl.frd.qremit.converter.nafex.helper.MyUserDetails;
import abl.frd.qremit.converter.nafex.model.User;
import abl.frd.qremit.converter.nafex.model.Role;
import abl.frd.qremit.converter.nafex.repository.UserModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    UserModelRepository userModelRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userModel = userModelRepository.findByUserName(username);
        userModel.orElseThrow(() -> new UsernameNotFoundException("Not Found " +username));
        return userModel.map(MyUserDetails::new).get();
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles){
        List<GrantedAuthority> authorityList = new ArrayList<>();
        roles.forEach(a -> authorityList.add(new SimpleGrantedAuthority(a.getRoleName())));
        return authorityList;
    }
}
