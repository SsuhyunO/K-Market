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
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
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

    // ===== 추가된 부분: 구글 인증요청 URL에 파라미터 커스텀 추가용 =====
    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

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

    // ===== 추가된 부분: 구글 로그인 요청 시 prompt=login 파라미터를 강제로 추가 =====
    // select_account는 로그인된 구글 계정이 1개뿐이면 "선택할 게 없다"고 판단해 그냥 스킵되어버림.
    // login으로 지정하면 계정 개수와 상관없이 매번 재인증(비밀번호 재입력) 화면을 거치게 강제됨.
    @Bean
    public OAuth2AuthorizationRequestResolver authorizationRequestResolver() {
        DefaultOAuth2AuthorizationRequestResolver resolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository, "/oauth2/authorization");

        resolver.setAuthorizationRequestCustomizer(customizer ->
                customizer.additionalParameters(params -> params.put("prompt", "login"))
        );

        return resolver;
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
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestResolver(authorizationRequestResolver())
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2LoginSuccessHandler)
                );

        return http.build();
    }
}