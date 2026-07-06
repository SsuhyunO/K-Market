package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.k_market.entity.Member;

@Mapper
public interface MemberDAO {

    // 회원가입 (INSERT)
    int insertMember(Member member);

    // uid로 회원 조회 (로그인 시 사용)
    Member findByUid(String uid);

    // 아이디 중복확인 (해당 uid가 몇 명 있는지 -> 0이면 사용가능)
    int countByUid(String uid);

    // 이름 + 이메일로 아이디 찾기
    String findUidByNameAndEmail(String name, String email);

    // uid + 이메일 일치 확인 (비밀번호 찾기 시 본인 확인용)
    int countByUidAndEmail(String uid, String email);

    // 비밀번호 변경
    int updatePassword(String uid, String password);

    // 회원정보 수정
    int updateMember(Member member);
}