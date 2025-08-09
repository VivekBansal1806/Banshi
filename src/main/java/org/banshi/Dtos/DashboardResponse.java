package org.banshi.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardResponse {
    private Double totalDeposits;
    private Double totalWithdrawals;
    private Double pendingWithdrawalAmount;
    private Double totalPlacedBid;

    private Long totalUsers;

    private Long totalBidsCount;
    private Long activeBidsCount;

    private Double netRevenue;

    private Long pendingWithdrawalRequests;

}
