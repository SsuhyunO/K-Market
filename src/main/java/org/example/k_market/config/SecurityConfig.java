package org.example.k_market.config;

import org.example.k_market.security.CustomOAuth2UserService;
import org.example.k_market.security.OAuth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // ===== 구글 OAuth2 관련 =====
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 프론트를 다른 포트(라이브서버, npm dev서버 등)에서 띄워서 테스트하는 경우
    // credentials: 'include' 로 세션 쿠키를 주고받으려면 CORS 설정이 반드시 필요합니다.
    // 같은 서버(Thymeleaf/JSP)에서 정적 파일을 서빙한다면 이 빈은 없어도 무방하지만,
    // 있어도 해가 되지 않으니 그대로 두는 걸 권장합니다.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // TODO: 실제 프론트 주소로 변경하세요 (예: http://localhost:5500)
        config.setAllowedOrigins(List.of("http://localhost:5500", "http://127.0.0.1:5500"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // 세션 쿠키 주고받으려면 필수

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                                .anyRequest().permitAll()
                        // 나중에 보안 강화할 때 다시 좁힐 예정
                )
                // ===== 구글 OAuth2 로그인 설정 =====
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/member/login") // 커스텀 로그인 페이지
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2LoginSuccessHandler)
                );

        return http.build();
    }
}