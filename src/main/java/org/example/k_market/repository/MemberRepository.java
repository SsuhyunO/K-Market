package org.example.k_market.repository;

import org.example.k_market.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {

    // 아이디(uid) 존재 여부 확인 (중복확인용)
    boolean existsByUid(String uid);

    // 이름 + 이메일로 회원 찾기 (아이디 찾기용)
    Member findByNameAndEmail(String name, String email);

    // uid + 이메일 일치 확인 (비밀번호 찾기 시 본인 확인용)
    boolean existsByUidAndEmail(String uid, String email);
}