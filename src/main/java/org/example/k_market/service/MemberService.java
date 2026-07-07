package org.example.k_market.service;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.member.MemberDto;
import org.example.k_market.entity.Member;
import org.example.k_market.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

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
    public MemberDto.FindUidResult findUid(MemberDto.FindUidRequest request) {
        Member member = memberRepository.findByNameAndEmail(request.getName(), request.getEmail());
        if (member == null) {
            throw new IllegalArgumentException("일치하는 회원 정보가 없습니다.");
        }
        return MemberDto.FindUidResult.from(member);
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

    // ===== 추가된 부분: 현재 비밀번호 확인 (마이페이지 비밀번호 확인 팝업용) =====
    public boolean verifyCurrentPassword(String uid, String rawPassword) {
        Member member = findByUid(uid);
        return passwordEncoder.matches(rawPassword, member.getPassword());
    }

    // ===== 추가된 부분: 마이페이지에서 비밀번호 변경 (uid + 새 비밀번호) =====
    public void changePasswordByUid(String uid, String newRawPassword) {
        Member member = findByUid(uid);
        member.changePassword(passwordEncoder.encode(newRawPassword));
        memberRepository.save(member);
    }

    // ===== 추가된 부분: 탈퇴 처리 (soft delete - 상태값만 변경) =====
    public void withdraw(String uid) {
        Member member = findByUid(uid);
        member.withdraw();
        memberRepository.save(member);
    }

    // ===== 추가된 부분: 마이페이지 정보수정(휴대폰/주소) =====
    // 이메일은 정책상 수정 금지이므로 request.getEmail()은 의도적으로 사용하지 않음
    public void updateProfile(String uid, MemberDto.UpdateRequest request) {
        Member member = findByUid(uid);
        member.updateProfile(request.getPhone(), request.getZipCode(), request.getAddr1(), request.getAddr2());
        memberRepository.save(member);
    }

    // ===== 추가된 부분: 자동로그인 토큰 발급 (7일 유지) =====
    public String issueAutoLoginToken(String uid) {
        Member member = findByUid(uid);
        String token = UUID.randomUUID().toString();
        member.issueAutoLoginToken(token, LocalDateTime.now().plusDays(7));
        memberRepository.save(member);
        return token;
    }

    // ===== 추가된 부분: 토큰으로 회원 조회 (만료 체크 포함, 유효하지 않으면 null) =====
    public Member findByValidAutoLoginToken(String token) {
        return memberRepository.findByAutoLoginToken(token)
                .filter(m -> m.getAutoLoginExpireAt() != null
                        && m.getAutoLoginExpireAt().isAfter(LocalDateTime.now()))
                .orElse(null);
    }

    // ===== 추가된 부분: 자동로그인 토큰 삭제 (로그아웃/탈퇴 시) =====
    public void clearAutoLoginToken(String uid) {
        Member member = findByUid(uid);
        member.clearAutoLoginToken();
        memberRepository.save(member);
    }
}