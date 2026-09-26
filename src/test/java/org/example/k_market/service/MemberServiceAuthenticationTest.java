package org.example.k_market.service;

import org.example.k_market.entity.Member;
import org.example.k_market.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MemberServiceAuthenticationTest {

    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final MemberService memberService = new MemberService(memberRepository, passwordEncoder);

    @Test
    void rejectsIncorrectPassword() {
        Member member = mock(Member.class);
        when(memberRepository.findById("user1")).thenReturn(Optional.of(member));
        when(member.getPassword()).thenReturn("encoded-password");
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () -> memberService.authenticate("user1", "wrong-password")
        );

        verify(passwordEncoder).matches("wrong-password", "encoded-password");
    }

    @Test
    void authenticatesOnlyWhenPasswordMatches() {
        Member member = mock(Member.class);
        when(memberRepository.findById("user1")).thenReturn(Optional.of(member));
        when(member.getPassword()).thenReturn("encoded-password");
        when(passwordEncoder.matches("correct-password", "encoded-password")).thenReturn(true);
        when(member.isWithdrawn()).thenReturn(false);

        Member authenticated = memberService.authenticate("user1", "correct-password");

        assertSame(member, authenticated);
    }
}
