package uz.husan.signup.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uz.husan.signup.entity.User;
import uz.husan.signup.service.impl.JWTService;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@Component
public class MyFilter extends OncePerRequestFilter {
    @Autowired
    @Lazy
    UserDetailsService userDetailsService;
    @Autowired
    private  JWTService jwtService;
    @Bean
    public AuditorAware<Long> auditorAware() {
        return () -> {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return Optional.ofNullable(user.getId());
        };
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if(authorization == null) {
            filterChain.doFilter(request, response);
            return;
        }
        if (authorization.startsWith("Bearer ")) {
            authorization = authorization.substring(7);
            if (!jwtService.isValid(authorization)){
                doFilter(request,response,filterChain);
                return;
            }
            String email = jwtService.getEmailFromToken(authorization);
            System.out.println(email);
            User user = (User) userDetailsService.loadUserByUsername(email);
            setUserToContextHolder(user);
        }
        if (authorization.startsWith("Basic ")){
            authorization = authorization.substring(6);
            //......
        }
        filterChain.doFilter(request, response);
    }
    public void setUserToContextHolder(User user) {
        var principal=new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(principal);
    }
}
