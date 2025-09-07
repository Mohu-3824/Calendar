package com.example.calendar_h.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.calendar_h.entity.User;
import com.example.calendar_h.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private final UserRepository userRepository;
	
	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		 // emailでユーザーを検索し、見つからなければ例外
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりませんでした: " + email));

        // UserをUserDetailsImplに変換
        return new UserDetailsImpl(user);
	}

}
