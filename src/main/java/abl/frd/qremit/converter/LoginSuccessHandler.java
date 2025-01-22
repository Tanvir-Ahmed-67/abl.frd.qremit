package abl.frd.qremit.converter;

import abl.frd.qremit.converter.helper.MyUserDetails;
import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.repository.UserModelRepository;
import abl.frd.qremit.converter.service.CustomLoginRestrictionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final CustomLoginRestrictionsService customLoginRestrictionsService;
    private final UserModelRepository userModelRepository;

    @Autowired
    public LoginSuccessHandler(CustomLoginRestrictionsService customLoginRestrictionsService,
                               UserModelRepository userModelRepository) {
        this.customLoginRestrictionsService = customLoginRestrictionsService;
        this.userModelRepository = userModelRepository;
    }
    SimpleUrlAuthenticationSuccessHandler superAdminSuccessHandler =
            new SimpleUrlAuthenticationSuccessHandler("/super-admin-home-page");
    SimpleUrlAuthenticationSuccessHandler adminSuccessHandler =
            new SimpleUrlAuthenticationSuccessHandler("/admin-home-page");
    SimpleUrlAuthenticationSuccessHandler userSuccessHandler =
            new SimpleUrlAuthenticationSuccessHandler("/user-home-page");

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Check if the user is locked
        if (user.getFailedAttempt() >= customLoginRestrictionsService.getMaxAttempts() && user.getActiveStatus()) {
            response.sendRedirect("/login?error=User Locked. Please Contact With Authorities");
            return;
        }
        // Check if the user is Unlocked but Inactive
        if (user.getFailedAttempt() < customLoginRestrictionsService.getMaxAttempts() && !user.getActiveStatus()) {
            response.sendRedirect("/login?error=User Inactive. Please Contact With Authorities");
            return;
        }
        // Reset failed login attempts
        customLoginRestrictionsService.resetAttempts(user.getUserEmail());

        // Validate IP restriction
        String clientIP = request.getRemoteAddr();
        String allowedIps = customLoginRestrictionsService.getAllowedIpsForUser(user.getId());
        Set<String> allowedIpSet = new HashSet<>(
                Arrays.asList(allowedIps.split(","))
        );
        if (!allowedIpSet.contains(clientIP)) {
            response.sendRedirect("/login?error=Access Denied: Invalid IP Address");
            return;
        }
        // Check login time restriction for non-admin users
        if (!customLoginRestrictionsService.isLoginAllowed(user.getId(), authorities)) {
            response.sendRedirect("/login?error=Access denied: Outside allowed login hours");
            return;
        }
        // Redirect to password change page if required
        if (userDetails.isPasswordChangeRequired()) {
            response.sendRedirect("/change-password");
        }
        for (final GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            if (authorityName.equals("ROLE_SUPERADMIN")) {
                // Delegate to the superAdminSuccessHandler
                this.superAdminSuccessHandler.onAuthenticationSuccess(request, response, authentication);
                return;
            }
            if (authorityName.equals("ROLE_ADMIN")) {
                // Delegate to the adminSuccessHandler
                this.adminSuccessHandler.onAuthenticationSuccess(request, response, authentication);
                return;
            }
            else {
                // Delegate to the userSuccessHandler
                this.userSuccessHandler.onAuthenticationSuccess(request, response, authentication);
            }
        }
    }
}
