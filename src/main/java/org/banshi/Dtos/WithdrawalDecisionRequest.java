package org.banshi.Dtos;

import lombok.Data;

@Data
public class WithdrawalDecisionRequest {
    private Long withdrawalId;
    private Boolean approve; // true = approve, false = reject
    private String rejectionMessage; // optional, required if approve=false
}
