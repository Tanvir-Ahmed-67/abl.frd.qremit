package abl.frd.qremit.converter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) // Enable method-level security annotations
public class SecurityConfiguration {

    private final UserDetailsService userDetailsService;
    private final LoginSuccessHandler loginSuccessHandler;

    public SecurityConfiguration(UserDetailsService userDetailsService, LoginSuccessHandler loginSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .antMatchers("/css/**", "/js/**", "/images/**", "/login", "/change-password", "/change-password-for-first-time-login").permitAll()
                        .antMatchers("**/upload", "**/allUsers", "**/downloadaccountpayee/**", "**/downloadbeftn/**", "**/downloadcoc/**", "**/downloadonline/**", "**/apibeftntransfer/**").hasAnyRole("ADMIN", "USER", "SUPERADMIN")
                        .antMatchers("**/newUserCreationForm/**", "**/createNewUser/**", "**/showInactiveUsers/**").hasRole("SUPERADMIN")
                        .antMatchers("**/exchangeHouseEditForm/**", "**/editExchangeHouse/**").hasRole("ADMIN")
                        .antMatchers("**/bec/index/**", "**/upload/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("userEmail")
                        .passwordParameter("password")
                        .successHandler(loginSuccessHandler)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .invalidSessionUrl("/login")
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                        .sessionRegistry(sessionRegistry())
                )
                .logout(logout -> logout
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
