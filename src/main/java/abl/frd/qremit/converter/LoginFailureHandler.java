package abl.frd.qremit.converter;

import abl.frd.qremit.converter.model.User;
import abl.frd.qremit.converter.service.CustomLoginRestrictionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
    private final CustomLoginRestrictionsService customLoginRestrictionsService;
    @Autowired
    public LoginFailureHandler(CustomLoginRestrictionsService customLoginRestrictionsService) {
        this.customLoginRestrictionsService = customLoginRestrictionsService;
    }
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errorMessage = "Incorrect password!";
        if (exception instanceof UsernameNotFoundException) {
            errorMessage = "User not found!";
        } else if (exception instanceof BadCredentialsException) {
            String userEmail = request.getParameter("userEmail");
            if (userEmail != null && !userEmail.isEmpty()) {
                try {
                    customLoginRestrictionsService.loginFailed(userEmail);
                } catch (UsernameNotFoundException e) {
                    errorMessage = "User not found!";
                }
            }
        } else if (exception instanceof LockedException) {
            errorMessage = "User Locked. Please Contact With Authorities";
        } else if (exception.getMessage().contains("Outside allowed login hours")) {
            errorMessage = "Access denied: Outside allowed login hours";
        } else if(exception instanceof DisabledException) {
            errorMessage = "User Disabled. Please Contact With Authorities";
        }

        // Redirect to the login page with the error message as a query parameter
        response.sendRedirect("login?error=" + errorMessage);
    }
}
