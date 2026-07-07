package org.example.k_market.service.admin;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.member.MemberDto;
import org.example.k_market.entity.Member;
import org.example.k_market.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AdminMemberService {

    private final MemberRepository memberRepository;

    public Page<MemberDto.AdminListItem> getMemberList(Pageable pageable) {
        Page<Member> memberPage = memberRepository.findAll(pageable);
        return toDtoPage(memberPage, pageable);
    }

    public Page<MemberDto.AdminListItem> searchMemberList(String searchType, String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return getMemberList(pageable);
        }

        Page<Member> memberPage = switch (searchType) {
            case "이름" -> memberRepository.findByNameContaining(keyword, pageable);
            case "이메일" -> memberRepository.findByEmailContaining(keyword, pageable);
            case "휴대폰" -> memberRepository.findByPhoneContaining(keyword, pageable);
            default -> memberRepository.findByUidContaining(keyword, pageable);
        };

        return toDtoPage(memberPage, pageable);
    }

    private Page<MemberDto.AdminListItem> toDtoPage(Page<Member> memberPage, Pageable pageable) {
        long total = memberPage.getTotalElements();
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();

        java.util.List<MemberDto.AdminListItem> content = new java.util.ArrayList<>();
        java.util.List<Member> members = memberPage.getContent();

        for (int i = 0; i < members.size(); i++) {
            long no = total - (long) pageNumber * pageSize - i;
            content.add(MemberDto.AdminListItem.from(members.get(i), (int) no));
        }

        return new org.springframework.data.domain.PageImpl<>(content, pageable, total);
    }

    // ===== 회원정보 수정 (즉시 DB 반영) =====
    public void updateMember(MemberDto.AdminUpdateRequest request) {
        Member member = memberRepository.findById(request.getUid())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다. uid=" + request.getUid()));

        member.adminUpdate(
                request.getName(),
                request.getGender(),
                request.getEmail(),
                request.getPhone(),
                request.getZipCode(),
                request.getAddress(),
                request.getDetailAddress(),
                request.getNote()
        );
        memberRepository.save(member); // JPA 영속성 컨텍스트 상 사실 없어도 반영되지만 명시적으로 저장
    }

    // ===== 등급 즉시변경 =====
    public void updateGrade(String uid, Integer memberLevel) {
        Member member = memberRepository.findById(uid)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다. uid=" + uid));

        member.changeMemberLevel(memberLevel);
        memberRepository.save(member);
    }
}