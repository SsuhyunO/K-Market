package org.example.k_market.controller.product;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.cart.CartAddRequest;
import org.example.k_market.service.cart.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart/api")
@RequiredArgsConstructor
public class CartApiController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<?> addCart(@RequestBody CartAddRequest request, HttpSession session) {
        String memberUid = getLoginMemberUid(session);

        cartService.add(memberUid, request);
        return ResponseEntity.ok(Map.of("message", "장바구니에 담았습니다."));
    }

    @DeleteMapping
    public ResponseEntity<?> removeCart(@RequestParam("cartNo") List<Integer> cartNos, HttpSession session) {
        String memberUid = getLoginMemberUid(session);

        int removedCount = cartService.remove(memberUid, cartNos);
        return ResponseEntity.ok(Map.of("removedCount", removedCount));
    }

    private String getLoginMemberUid(HttpSession session) {
        String memberUid = (String) session.getAttribute("loginMember");
        if (memberUid == null || memberUid.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return memberUid;
    }
}
