package org.example.k_market.dto.admin;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MemberStatDTO {
    private long totalJoinCount;
    private int todayJoinCount;
    private int yesterdayJoinCount;
}
