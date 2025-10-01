package com.example.calendar_h.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {   
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// URLごとのアクセス制御
		http
				.authorizeHttpRequests((requests) -> requests
						.requestMatchers("/calendar/**", "/tasks/**").authenticated() // ログインが必要なURL
						.anyRequest().permitAll() // 上記以外のURLはすべてのユーザーにアクセスを許可する
				)
				// フォームログイン設定
				.formLogin((form) -> form
						.loginPage("/login") // ログインページのURL
						.loginProcessingUrl("/login") // ログインフォームの送信先URL
						.defaultSuccessUrl("/calendar", true) // ログイン成功時のリダイレクト先URL
						.failureUrl("/login?error") // ログイン失敗時のリダイレクト先URL
						.permitAll()
				)

				// ログアウト設定
				.logout(logout -> logout
	                .logoutSuccessUrl("/?loggedOut")
	                .deleteCookies("JSESSIONID", "remember-me")
	                .permitAll()
				);

		return http.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authManager(HttpSecurity http, PasswordEncoder passwordEncoder,
			UserDetailsService userDetailsService) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class)
				.userDetailsService(userDetailsService)
				.passwordEncoder(passwordEncoder)
				.and()
				.build();
	}
}
