package uz.husan.rentcar24.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uz.husan.rentcar24.entity.enums.UserRole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
@Data
public class User implements UserDetails {
    private int id;
    private String email;
    private String password;
    private Double  balance;
    private String fullName;
    private UserRole role;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(
                role.getPermissions().stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList()
        );
        authorities.add(new SimpleGrantedAuthority("ROLE_"+role.name()));
        return authorities;
    }
    @Override
    public String getUsername() {
        return email;
    }
}
