package com.bmw.seckill.security.manager;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.data.annotation.Transient;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author zhangwenjuan
 * @date 2022/8/9
 * AdminUser需要实现SpringSecurity的UserDetails接口
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AdminUser implements UserDetails {

    private Long id;

    private String username;

    private String password;

    private boolean enable;

    private String roles;

    private Date createDate;

    private Date modifyDate;

    @Transient
    private List<GrantedAuthority> authorities;


    private String token;

    /**
     * 登陆时间戳（毫秒）
     */
    private Long loginTime;
    /**
     * 过期时间戳
     */
    private Long expireTime;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}