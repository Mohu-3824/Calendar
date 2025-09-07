package com.example.calendar_h.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.calendar_h.entity.User;

public class UserDetailsImpl implements UserDetails {
	private final User user;
	
	public UserDetailsImpl(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 今回は権限管理をしない想定なので空リストを返す
        return Collections.emptyList();
    }
    
	// ハッシュ化済のパスワードを返す
	@Override
	public String getPassword() {
		return user.getPassword();
	}
	
	// ログイン時に利用するユーザー名（メールアドレス）を返す
	@Override
	public String getUsername() {
		return user.getEmail();
	}

	// アカウントが期限切れでなければtrueを返す
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	// ユーザーがロックされていなければtrueを返す
	public boolean isAccountNonLocked() {
		return true;
	}
	
	// ユーザーのパスワードが期限切れでなければtrueを返す
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	// ユーザーが有効であればtrueを返す
	@Override
	public boolean isEnabled() {
		return user.getEnabled();
	}
}
