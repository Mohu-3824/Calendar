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
				// Remember-Me設定
				.rememberMe(remember -> {
			        remember.key("calendar_h_unique_key");
			        remember.rememberMeParameter("remember-me");
			        remember.tokenValiditySeconds(14 * 24 * 60 * 60);
			        remember.userDetailsService(userDetailsService);
			        remember.tokenRepository(persistentTokenRepository());
			    
				// ▼ Cookie/DB不一致対策（重要）
	            // tokenRepositoryがnullを返す場合やUserが見つからない場合は
	            // 認証をクリアして再ログインを要求
	            remember.useSecureCookie(true); // HTTPS利用時に推奨
	            remember.alwaysRemember(false); // "remember-me"チェック時のみ
				})

	        	// セッション切れ時の対応
				.sessionManagement(session ->
	                session.invalidSessionUrl("/login?sessionExpired")
				)

				// ログアウト設定
				.logout(logout -> logout
	                .logoutSuccessUrl("/?loggedOut")
	                .deleteCookies("JSESSIONID", "remember-me")
	                .permitAll()
				);

		return http.build();
	}
	
	// Remenber-Me用のトークンをDBで管理
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl() {
        @Override
        public org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken getTokenForSeries(String seriesId) {
            try {
                return super.getTokenForSeries(seriesId);
            } catch (Exception e) {
                // ▼ CookieとDBの不一致を検知して安全に無効化
                System.err.println("Remember-Me token mismatch detected. Clearing token...");
                return null; // nullを返すと再ログインが求められる
            }
        }
    };
    tokenRepository.setDataSource(dataSource);
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
