package org.example.k_market.service;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.member.MemberDto;
import org.example.k_market.entity.Member;
import org.example.k_market.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 아이디 중복확인 (true = 이미 존재함 = 사용 불가)
    public boolean isUidDuplicate(String uid) {
        return memberRepository.existsByUid(uid);
    }

    // 회원가입
    public void signUp(MemberDto.SignUpRequest request, String regIp) {

        // 1) 아이디 중복 체크
        if (memberRepository.existsByUid(request.getUid())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 2) 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3) 일반회원으로 가입 (판매자는 별도 절차로 나중에 처리)
        Member member = request.toEntity(encodedPassword, "NORMAL", regIp);

        // 4) DB 저장
        memberRepository.save(member);
    }

    // 아이디 찾기
    public String findUid(MemberDto.FindUidRequest request) {
        Member member = memberRepository.findByNameAndEmail(request.getName(), request.getEmail());
        if (member == null) {
            throw new IllegalArgumentException("일치하는 회원 정보가 없습니다.");
        }
        return member.getUid();
    }

    // 비밀번호 찾기 - 본인 확인 (uid + 이메일 일치 여부)
    public boolean verifyForPasswordReset(MemberDto.FindPasswordRequest request) {
        return memberRepository.existsByUidAndEmail(request.getUid(), request.getEmail());
    }

    // 비밀번호 재설정
    public void resetPassword(MemberDto.ResetPasswordRequest request) {
        Member member = memberRepository.findById(request.getUid())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        member.changePassword(encodedPassword);
        memberRepository.save(member);
    }

    // uid로 회원 조회 (로그인 등에서 사용)
    public Member findByUid(String uid) {
        return memberRepository.findById(uid)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }
}