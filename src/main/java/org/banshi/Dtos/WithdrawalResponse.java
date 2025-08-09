package org.banshi.Dtos;

import lombok.*;
import java.time.LocalDateTime;
import org.banshi.Entities.Enums.WithdrawalStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawalResponse {
    private Long id;
    private Long userId;
    private String userName;          // optional, helpful for admin UI
    private Double amount;
    private String upiId;
    private WithdrawalStatus status;
    private String rejectionReason;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
}
