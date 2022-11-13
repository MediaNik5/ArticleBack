package org.catblocks.articleback.security;

import org.catblocks.articleback.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UserPrincipal implements OAuth2User, UserDetails{
    private static final SimpleGrantedAuthority roleUser = new SimpleGrantedAuthority("ROLE_USER");
    private final String id;
    private final String username;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    public UserPrincipal(
        String id,
        String username,
        String email,
        Collection<? extends GrantedAuthority> authorities,
        Map<String, Object> attributes
    ){
        this.id = id;
        this.username = username;
        this.email = email;
        this.authorities = authorities;
        this.attributes = attributes;
    }
    public UserPrincipal(
        String id,
        String username,
        String email,
        Collection<? extends GrantedAuthority> authorities
    ){
        this.id = id;
        this.username = username;
        this.email = email;
        this.authorities = authorities;
        this.attributes = Map.of();
    }
    public static UserPrincipal create(User user){
        return new UserPrincipal(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            requireRoleUser(null)
        );
    }

    public static UserPrincipal create(User user, Map<String, Object> attributes){
        return new UserPrincipal(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            requireRoleUser(null),
            attributes
        );
    }

    private static List<GrantedAuthority> requireRoleUser(List<GrantedAuthority> authorities){
        if(authorities == null)
            authorities = new ArrayList<>(1);
        if(!authorities.contains(roleUser))
            authorities.add(roleUser);
        return authorities;
    }

    @Override
    public String getPassword(){
        return null;
    }

    @Override
    public String getUsername(){
        return id;
    }

    public String getId(){
        return id;
    }

    @Override
    public boolean isAccountNonExpired(){
        return true;
    }

    @Override
    public boolean isAccountNonLocked(){
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired(){
        return true;
    }

    @Override
    public boolean isEnabled(){
        return true;
    }

    @Override
    public Map<String, Object> getAttributes(){
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return authorities;
    }

    @Override
    public String getName(){
        return id;
    }
}
