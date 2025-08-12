package org.banshi.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banshi.Entities.Enums.BidResultStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BidResponse {
    private Long bidId;
    private Long userId;
    private Long gameId;
    private String gameName;
    private String bidType;
    private String bidTiming;
    private String number;
    private BidResultStatus resultStatus;
    private Double amount;
    private Double payout;
    private LocalDateTime placedAt;
}
