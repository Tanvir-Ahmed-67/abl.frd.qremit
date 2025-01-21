package abl.frd.qremit.converter.service;

import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.repository.UserModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

@Service
public class CustomLoginRestrictionsService {
    @Autowired
    UserModelRepository userModelRepository;
    @Autowired
    MyUserDetailsService myUserDetailsService;
    private final int MAX_ATTEMPTS = 5;

    public int getMaxAttempts() {
        return MAX_ATTEMPTS;
    }

    public void loginFailed(String userEmail) {
        User user = myUserDetailsService.loadUserByUserEmail(userEmail);
        if (user != null) {
            user.setFailedAttempt(user.getFailedAttempt() + 1);
            if (user.getFailedAttempt() >= MAX_ATTEMPTS) {
                user.setActiveStatus(false); // Lock the user
            }
            userModelRepository.save(user);
        }
    }

    public void resetAttempts(String userEamil) {
        User user = myUserDetailsService.loadUserByUserEmail(userEamil);
        if (user != null) {
            user.setFailedAttempt(0);
            userModelRepository.save(user);
        }
    }

    public String getAllowedIpsForUser(int userId) {
        String allowedIps = userModelRepository.getAllowedIpsForUser(userId);
        return allowedIps;
    }

    public boolean isLoginAllowed(int userId, Collection<? extends GrantedAuthority> authorities) {
        /*
        // Check if the user has ADMIN or SUPERADMIN role

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_ADMIN") || role.equals("ROLE_SUPERADMIN")) {
                return true; // Allow login without time restriction
            }
        }
        */
        LocalTime startTime = LocalTime.parse(userModelRepository.getAllowedStartTime(userId));
        LocalTime endTime = LocalTime.parse(userModelRepository.getAllowedEndTime(userId));
        LocalTime now = LocalTime.now();
        return !now.isBefore(startTime) && !now.isAfter(endTime);
    }
}
