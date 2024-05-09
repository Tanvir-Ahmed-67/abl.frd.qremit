package abl.frd.qremit.converter.nafex.service;

import abl.frd.qremit.converter.nafex.helper.MyUserDetails;
import abl.frd.qremit.converter.nafex.model.ExchangeHouseModel;
import abl.frd.qremit.converter.nafex.model.User;
import abl.frd.qremit.converter.nafex.repository.UserModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    UserModelRepository userModelRepository;
    public UserDetails loadUserByUserEmail(String userEmail)
            throws UsernameNotFoundException {
        User user = userModelRepository.findByUserEmail(userEmail);
        if (user == null) {
            throw new UsernameNotFoundException("Could not find user");
        }
        return new MyUserDetails(user);
    }
    public User loadUserByUserId(int userId)
            throws UsernameNotFoundException {
        User user = userModelRepository.findByUserId(userId);
        if (user == null) {
            throw new UsernameNotFoundException("Could not find user");
        }
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        User user = userModelRepository.findByUserEmail(userEmail);
        if (user == null) {
            throw new UsernameNotFoundException("Could not find user");
        }
        return new MyUserDetails(user);

    }
    public List<User> loadAllUser() throws UsernameNotFoundException {
        List<User> users = userModelRepository.loadAllUsers();
        if(users.isEmpty()){
            throw new UsernameNotFoundException("Could not find user");
        }
        return users;
    }
    public List<User> loadUsersOnly() throws UsernameNotFoundException {
        List<User> users = userModelRepository.loadUsersOnly();
        if(users.isEmpty()){
            throw new UsernameNotFoundException("Could not find user");
        }
        return users;
    }
    public void insertUser(User user) throws UsernameNotFoundException {
        userModelRepository.save(user);
    }
    public void editUser(User user){
        int userId = user.getId();
        String userName = user.getUserName();
        String userEmail = user.getUserEmail();
        String exchangeCode = user.getExchangeCode();
        userModelRepository.updateUser(userId, userName, userEmail, exchangeCode);
    }
    public List<User> loadAllInactiveUsers(){
        List<User> inactiveUsersList = userModelRepository.loadAllInactiveUsers();
        return inactiveUsersList;
    }
    public boolean updateInactiveUser(int userId){
        userModelRepository.updateInactiveUser(userId);
        return true;
    }
}
