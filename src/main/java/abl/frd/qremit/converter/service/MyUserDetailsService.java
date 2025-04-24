package abl.frd.qremit.converter.service;
import abl.frd.qremit.converter.helper.MyUserDetails;
import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.repository.UserModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private static final String other = null;
    @Autowired
    UserModelRepository userModelRepository;
    /*
    public User loadUserByUserEmail(String userEmail) throws UsernameNotFoundException {
        User user = userModelRepository.findByUserEmail(userEmail);
        if (user == null) {
            throw new UsernameNotFoundException("Could not find user");
        }
        // Check if the user is locked
        if (user.getFailedAttempt() >= 5) {
            throw new LockedException("User Locked. Please Contact With Admin");
        }
        return user;
    }
    */
    public User loadUserByLoginId(String loginId) throws UsernameNotFoundException{
        User user = userModelRepository.findByLoginId(loginId);
        if(user == null)    throw new UsernameNotFoundException("Could not find user");
        // Check if the user is locked
        if (user.getFailedAttempt() >= 5) {
            throw new LockedException("User Locked. Please Contact With Admin");
        }
        return user;
    }

    
    
    public Map<String, String> getLoggedInUserMenu(MyUserDetails userDetails){
        Map<String, String> exchangeNamesMap = getExchangeNamesByUserId(userDetails.getUser().getId());
        
        String exchangeCode = exchangeNamesMap.get("exchange_code");
        String exchangeShortNamesStr = exchangeNamesMap.get("exchange_short_name");
        
        List<String> exchangeCodes = Arrays.asList(exchangeCode.split(","));
        List<String> exchangeShortNames = Arrays.asList(exchangeShortNamesStr.split(","));
       
        Map<String, String> exchangeMap =  new HashMap<String, String>();
     
        if (exchangeCodes.size() == exchangeShortNames.size()) {
            for (int i = 0; i < exchangeCodes.size(); i++) {
                exchangeMap.put(exchangeShortNames.get(i), exchangeCodes.get(i));
            }
            return exchangeMap;
        }
        return exchangeMap;
    }
    
    /*
    //exchange house code map to seperate table using user id
    public Map<String, String> getLoggedInUserMenu(MyUserDetails userDetails){
        Map<String, String> resp = new HashMap<>();
        int userId = userDetails.getUser().getId();
        List<ExchangeHouseModel> exchangeHouseList = userModelRepository.findExchangeHouseByUserId(userId);
        for(ExchangeHouseModel eList: exchangeHouseList){
            resp.put(eList.getExchangeShortName(), eList.getExchangeCode());
        }
        return resp;
    }
    */

    public Map<String, Integer> getLoggedInUserRole(Authentication authentication){
        Map<String, Integer> resp = new HashMap<>();
        int isAdmin = authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")) ? 1:0;
        int isUser = authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")) ? 1:0;
        int isSuperAdmin = authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_SUPERADMIN")) ? 1:0;
        resp.put("isAdmin", isAdmin);
        resp.put("isUser", isUser);
        resp.put("isSuperAdmin", isSuperAdmin);
        return resp;
    }

    public Map<String, Object> getLoggedInUserDetails(Authentication authentication, MyUserDetails myUserDetails){
        Map<String, Object> resp = new HashMap<>();
        Map<String, Integer> role = getLoggedInUserRole(authentication);
        int userId = 0;
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User user = myUserDetails.getUser();
            resp.put("user", user);
            resp.put("role", role);
            if(role.get("isUser") == 1){
                userId = user.getId();
                if(myUserDetails != null) resp.put("exchangeMap",getLoggedInUserMenu(myUserDetails));
            }else if(role.get("isAdmin") == 1){
                resp.put("adminUserId", user.getId());
            }else if(role.get("isSuperAdmin") == 1){
                userId = 8888; //for SuperAdmin
            }
            resp.put("status", HttpStatus.OK);
        }else{
            resp.put("status", HttpStatus.UNAUTHORIZED);
        }
        resp.put("userid", userId);  
        return resp;
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
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        User user = userModelRepository.findByLoginId(loginId);
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
    public List<Object[]> loadAllUsersAndRoles(){
        return userModelRepository.loadAllUsersAndRoles();
    }
    public void insertUser(User user) throws UsernameNotFoundException {
        userModelRepository.save(user);
    }
    public void editUser(User user){
        int userId = user.getId();
        String userName = user.getUserName();
        String userEmail = user.getUserEmail();
        String loginId = user.getLoginId();
        String exchangeCode = user.getExchangeCode();
        String allowedIps = user.getAllowedIps();
        String startTime = user.getStartTime();
        String endTime = user.getEndTime();
        userModelRepository.updateUser(userId, userName, userEmail, loginId, exchangeCode, allowedIps, startTime, endTime);
    }
    public void updatePasswordForFirstTimeUserLogging(User user){
        int userId = user.getId();
        user.setPasswordChangeRequired(false);
        boolean passwordChangeRequired = user.isPasswordChangeRequired();
        String password = user.getPassword();
        userModelRepository.updatePasswordForFirstTimeUserLogging(userId, password, passwordChangeRequired);
    }
    public List<User> loadAllInactiveUsers(){
        List<User> inactiveUsersList = userModelRepository.loadAllInactiveUsers();
        return inactiveUsersList;
    }
    public boolean updateInactiveUser(int userId){
        userModelRepository.updateInactiveUser(userId);
        return true;
    }

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
    @Transactional
    public boolean setLoginTimeRestrictionsForAllUsers(String startTime, String endTime){
        int rowsUpdated = userModelRepository.setLoginTimeRestrictionsForAllUsers(startTime, endTime);
        return rowsUpdated > 0;
    }
    @Transactional
    public int resetPassword(int userId, String password){
        return userModelRepository.updatePasswordForFirstTimeUserLogging(userId, password, true);
    }

}
