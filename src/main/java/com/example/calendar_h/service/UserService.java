package com.example.calendar_h.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.calendar_h.entity.User;
import com.example.calendar_h.form.SignupForm;
import com.example.calendar_h.form.UserEditForm;
import com.example.calendar_h.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Transactional
	public User createUser(SignupForm signupForm) {
		User user = new User();
		
		user.setName(signupForm.getName());
		user.setFurigana(signupForm.getFurigana());
		user.setPhoneNumber(signupForm.getPhoneNumber());
		user.setEmail(signupForm.getEmail());
		user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
		user.setEnabled(true);
		
		return userRepository.save(user);
	}
	
    @Transactional
    public void updateUser(UserEditForm userEditForm, User user) {
        user.setName(userEditForm.getName());
        user.setFurigana(userEditForm.getFurigana());
        user.setPhoneNumber(userEditForm.getPhoneNumber());
        user.setEmail(userEditForm.getEmail());

        userRepository.save(user);
    }
	
    // メールアドレスが登録済みかどうかをチェックする
    public boolean isEmailRegistered(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        return user != null;
    }  
    
    // パスワードとパスワード（確認用）の入力値が一致するかどうかをチェックする
    public boolean isSamePassword(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }
    
    // ユーザーを有効にする
    @Transactional
    public void enableUser(User user) {
        user.setEnabled(true);
        userRepository.save(user);
    }
    
    // メールアドレスが変更されたかどうかをチェックする
    public boolean isEmailChanged(UserEditForm userEditForm, User user) {
        return !userEditForm.getEmail().equals(user.getEmail());
    }   
}
