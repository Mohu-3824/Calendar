package com.example.calendar_h.security;

import javax.sql.DataSource;

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
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
	
	private final DataSource dataSource;
    private final UserDetailsServiceImpl userDetailsService;

    public WebSecurityConfig(DataSource dataSource, UserDetailsServiceImpl userDetailsService) {
    	this.dataSource = dataSource;
        this.userDetailsService = userDetailsService;
    }
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests((requests) -> requests
						.requestMatchers("/calendar/**", "/tasks/**").authenticated() // ログインが必要なURL
						.anyRequest().permitAll() // 上記以外のURLはすべてのユーザーにアクセスを許可する
				)
				.formLogin((form) -> form
						.loginPage("/login") // ログインページのURL
						.loginProcessingUrl("/login") // ログインフォームの送信先URL
						.defaultSuccessUrl("/calendar", true) // ログイン成功時のリダイレクト先URL
						.failureUrl("/login?error") // ログイン失敗時のリダイレクト先URL
						.permitAll()
				)
	            .rememberMe(remember -> remember
	                    .key("calendar_h_unique_key") // 自分専用の秘密鍵に変更
	                    .rememberMeParameter("remember-me") // フォームのname属性と一致
	                    .tokenValiditySeconds(14 * 24 * 60 * 60) // 2週間保持
	                    .userDetailsService(userDetailsService) // 認証ロジック
	                    .tokenRepository(persistentTokenRepository()) // DB保存設定
	            )
				.logout((logout) -> logout
						.logoutSuccessUrl("/?loggedOut") // ログアウト時のリダイレクト先URL
						.permitAll());

		return http.build();
	}
	
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        // 初回起動時にテーブル自動作成したい場合は true にする
        // tokenRepository.setCreateTableOnStartup(true);
        return tokenRepository;
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
