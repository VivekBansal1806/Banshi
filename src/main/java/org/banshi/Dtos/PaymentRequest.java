package org.banshi.Dtos;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long userId;
    private Double amount;
}
