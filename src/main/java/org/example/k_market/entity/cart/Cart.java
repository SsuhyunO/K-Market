package org.example.k_market.entity.cart;


import jakarta.persistence.*;
import lombok.*;
import org.example.k_market.dto.cart.CartDTO;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cartNo;
    private String memberUid;
    private int prodNo;
    private int count;
    private int price;
    private int total;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public CartDTO toDTO() {
        return CartDTO.builder()
                .cartNo(cartNo)
                .memberUid(memberUid)
                .prodNo(prodNo)
                .count(count)
                .price(price)
                .total(total)
                .createdAt(createdAt.toString())
                .build();
    }
}
