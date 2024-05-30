package abl.frd.qremit.converter.nafex.service;

import abl.frd.qremit.converter.nafex.helper.MyUserDetails;
import abl.frd.qremit.converter.nafex.model.ExchangeHouseModel;
import abl.frd.qremit.converter.nafex.model.User;
import abl.frd.qremit.converter.nafex.repository.UserModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class MyUserDetailsService implements UserDetailsService {
    private static final String other = null;
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
    public Map<String, String> getLoggedInUserMenu(MyUserDetails userDetails){
         
      
        Map<String, String> exchangeNamesMap = getExchangeNamesByUserId(userDetails.getUser().getId());
        
        String exchangeNamesStr = exchangeNamesMap.get("exchange_name");
        String exchangeShortNamesStr = exchangeNamesMap.get("exchange_short_name");
        
        List<String> exchangeNames = Arrays.asList(exchangeNamesStr.split(","));
        List<String> exchangeShortNames = Arrays.asList(exchangeShortNamesStr.split(","));
       
        Map<String, String> exchangeMap =  new HashMap<String, String>();
     
        if (exchangeNames.size() == exchangeShortNames.size()) {
            for (int i = 0; i < exchangeNames.size(); i++) {
                exchangeMap.put(exchangeNames.get(i), exchangeShortNames.get(i));
            }
            return exchangeMap;
          //  model.addAttribute("exchangeMap", exchangeMap);
        } 
        // else {
        //     model.addAttribute("error", "Mismatch in the size of exchange names and short names lists");
        // }
        return exchangeMap;
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

   
    // public String findExchangeNameByUser(int user_id)  {
    //    String exchangeName = userModelRepository.findExchangeNamesByUserId(user_id);
    //     return exchangeName;
    // }

    // public String findExchangeNameControllerByUser(int user_id)  {
    //     String exchangeControllerName = userModelRepository.findExchangeNamesControllerByUserId(user_id);
    //      return exchangeControllerName;
    //  }

    public Map<String, String> getExchangeNamesByUserId(int userId) {
        return userModelRepository.findExchangeNamesByUserId(userId);
    }

    
    public List<User> loadUsersOnly() throws UsernameNotFoundException {
        List<User> users = userModelRepository.loadUsersOnly();
        if(users.isEmpty()){
            throw new UsernameNotFoundException("Could not find user");
        }
        return users;
    }
    public List<User> loadAdminsOnly() throws UsernameNotFoundException {
        List<User> admins = userModelRepository.loadAdminsOnly();
        if(admins.isEmpty()){
            throw new UsernameNotFoundException("Could not find Admin");
        }
        return admins;
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

    // public User updateUser(User user){
    //     User existingUser= userModelRepository.findByUserId(user.getId());
    //     existingUser.setUserName(user.getUserName());
    //     existingUser.setRoles(user.getRoles());
    //     existingUser.setUserEmail(user.getUserEmail());
    //     existingUser.setNrtaCode(user.getNrtaCode());

    //     return existingUser;

    // }

    public User getUserByIds(int id){
        Optional <User> optional = userModelRepository.findById(id);
        User user = null;
        if(optional.isPresent()){
            user =optional.get();
        }else {
            throw new RuntimeException("User not Found for id :: " + id);
        }
        return user;
    }

    // public static User getUserByIds(int id) {
    //     Optional <User> optional = userModelRepository.findById(id);
    //     User user = null;
    //     if(optional.isPresent()){
    //         user =optional.get();
    //     }else {
    //         throw new RuntimeException("User not Found for id :: " + id);
    //     }
    //     return user;
    // }
    public int getCurrentUser() {
        User user = null;
        int loggedInUserId = 1111;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof MyUserDetails) {
                user = ((MyUserDetails) principal).getUser();
            }
        }
        if(user != null){
            loggedInUserId = user.getId();
        }
        return loggedInUserId;
    }
    public String getUserExchangeCode() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserExchangeCode'");
    }

}
