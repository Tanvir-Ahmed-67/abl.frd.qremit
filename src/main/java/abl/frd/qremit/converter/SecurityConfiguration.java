package abl.frd.qremit.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    private LoginSuccessHandler loginSuccessHandler;
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);

    }
    protected void configure(HttpSecurity http) throws Exception{
        http.authorizeRequests()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("**/upload","**/allUsers","**/downloadaccountpayee/**","**/downloadbeftn/**","**/downloadcoc/**","**/downloadonline/**").hasAnyRole("ADMIN","USER","SUPERADMIN")
                .antMatchers("**/newUserCreationForm/**","**/createNewUser/**").hasRole("SUPERADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login").usernameParameter("userEmail").passwordParameter("password")
                .successHandler(loginSuccessHandler)
                .and()
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .sessionManagement(session -> session.invalidSessionUrl("/logout"))
                .logout(logout -> logout.deleteCookies("JSESSIONID").invalidateHttpSession(true));

    }


    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }
}
