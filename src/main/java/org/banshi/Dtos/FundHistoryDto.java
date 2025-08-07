package org.banshi.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banshi.Entities.Enums.TransactionType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundHistoryDto {
    private Long historyId;
    private Long userId;
    private Double amount;
    private String transactionType;
    private String reference;
    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String razorpaySignature;
    private LocalDateTime transactionTime;
}
