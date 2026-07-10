package org.example.k_market.security;

import lombok.RequiredArgsConstructor;
import org.example.k_market.entity.Member;
import org.example.k_market.repository.MemberRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 구글로부터 사용자 정보(email, name, sub 등)를 받아옴
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String providerId = (String) attributes.get("sub"); // 구글 고유 식별자

        if (email == null) {
            throw new OAuth2AuthenticationException("구글 계정에서 이메일 정보를 가져올 수 없습니다.");
        }

        // 이메일로 기존 member 조회
        Optional<Member> existingMember = memberRepository.findByEmail(email);

        Member member;
        if (existingMember.isPresent()) {
            // 이미 가입된 이메일 -> GOOGLE로 연동 (기존에 LOCAL로 가입했어도 그대로 로그인 처리)
            member = existingMember.get();
            if (!"GOOGLE".equals(member.getProvider())) {
                member.linkGoogleProvider(providerId);
                memberRepository.save(member);
            }
        } else {
            // 신규 유저 -> member row 새로 생성
            member = Member.createGoogleMember(email, name, providerId);
            memberRepository.save(member);
        }

        // Spring Security가 세션에 들고 있을 OAuth2User 객체 리턴
        // attributes는 그대로 넘기고, nameAttributeKey는 "email"로 지정
        return oAuth2User;
    }
}