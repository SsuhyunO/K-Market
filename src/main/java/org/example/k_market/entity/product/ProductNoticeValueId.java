package org.example.k_market.entity.product;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProductNoticeValueId  implements Serializable {
    private String noticeKey;
    private int prodNo;
}
