package uz.husan.rentcar24.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import uz.husan.rentcar24.dao.UserDAO;
import uz.husan.rentcar24.entity.User;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    final UserDAO userDAO;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomLoginSuccessHandler successHandler) throws Exception {
        http.csrf(csrf -> csrf.disable());

        http.authorizeRequests(req ->
                req.
                        requestMatchers("/admin/**").
                        hasAnyRole("ADMIN").
                        requestMatchers("/user/**").
                        hasAnyRole("USER").
                        requestMatchers("/", "/auth/**","/aloqa").
                        permitAll().
                        anyRequest()
                        .authenticated()
        );
        http
                .formLogin(form -> form
                        .loginPage("/auth/signin")
                        .loginProcessingUrl("/auth/signin")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(successHandler)
                        .failureUrl("/")
                        .permitAll()// bu yerda custom handler qo‘shiladi
                );
        http
                .userDetailsService(userDetailsService());
        return http.build();
    }
    @Bean
    public UserDetailsService userDetailsService() {
        return  (username) -> (User) userDAO.getUserByEmail(username).orElseThrow(()->new UsernameNotFoundException("User not found"));
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

