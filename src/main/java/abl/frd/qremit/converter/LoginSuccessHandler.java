package abl.frd.qremit.converter;

import abl.frd.qremit.converter.nafex.helper.MyUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    SimpleUrlAuthenticationSuccessHandler superAdminSuccessHandler =
            new SimpleUrlAuthenticationSuccessHandler("/super-admin-home-page");
    SimpleUrlAuthenticationSuccessHandler adminSuccessHandler =
            new SimpleUrlAuthenticationSuccessHandler("/admin-home-page");
    SimpleUrlAuthenticationSuccessHandler userSuccessHandler =
            new SimpleUrlAuthenticationSuccessHandler("/user-home-page");

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        for (final GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            if (authorityName.equals("ROLE_SUPERADMIN")) {
                // if the user is a SUPER ADMIN delegate to the adminSuccessHandler
                this.superAdminSuccessHandler.onAuthenticationSuccess(request, response, authentication);
                return;
            }
            if (authorityName.equals("ROLE_ADMIN")) {
                // if the user is an ADMIN delegate to the adminSuccessHandler
                this.adminSuccessHandler.onAuthenticationSuccess(request, response, authentication);
                return;
            }
        }
        if (userDetails.isPasswordChangeRequired()) {
            response.sendRedirect("/change-password");
        } else {
            response.sendRedirect("/user-home-page");
        }
        // if the user is not an Admin or Super Admin delegate to the userSuccessHandler
        //this.userSuccessHandler.onAuthenticationSuccess(request, response, authentication);
    }
}
