// package com.sanjittech.hms.model; // Or a new package like com.sanjittech.hms.security
// You might create a new package for this if you want to keep models clean
package com.sanjittech.hms.config; // Recommended new package

import com.sanjittech.hms.model.User; // Import your JPA User entity
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// This class acts as the true UserDetails object for Spring Security
public class SecurityUser implements UserDetails {

    private final String username;
    private final String password; // Raw password as String
    private final List<GrantedAuthority> authorities;
    private final UserRole role; // Optionally store the role for later checks

    // Constructor from your JPA User entity
    public SecurityUser(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword(); // Use the hashed password from the entity
        this.role = user.getRole(); // Store the role
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Implement your logic if needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Implement your logic if needed
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Implement your logic if needed
    }

    @Override
    public boolean isEnabled() {
        return true; // Implement your logic if needed
    }

    // IMPORTANT: No Lombok @ToString, @EqualsAndHashCode here!
    // If you absolutely need toString() for debugging, implement it simply
    @Override
    public String toString() {
        return "SecurityUser{" +
                "username='" + username + '\'' +
                ", authorities=" + authorities +
                ", role=" + role +
                '}'; // Exclude password from toString!
    }
}
