package org.banshi.Services.Impl;

import org.banshi.Dtos.DashboardResponse;
import org.banshi.Repositories.BidRepository;
import org.banshi.Repositories.FundHistoryRepository;
import org.banshi.Repositories.UserRepository;
import org.banshi.Repositories.WithdrawalRepository;
import org.banshi.Services.DashboardService;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final FundHistoryRepository fundHistoryRepo;
    private final WithdrawalRepository withdrawalRepo;
    private final UserRepository userRepo;
    private final BidRepository bidRepo;

    public DashboardServiceImpl(FundHistoryRepository fundHistoryRepo, WithdrawalRepository withdrawalRepo, UserRepository userRepo, BidRepository bidRepo) {
        this.fundHistoryRepo = fundHistoryRepo;
        this.withdrawalRepo = withdrawalRepo;
        this.userRepo = userRepo;
        this.bidRepo = bidRepo;
    }

    @Override
    public DashboardResponse getDashboardDetails() {
        Double totalDeposits = fundHistoryRepo.getTotalDeposits();
        Double totalWithdrawals = withdrawalRepo.getTotalWithdrawals();
        Double pendingWithdrawalAmount = withdrawalRepo.getPendingWithdrawalAmount();
        Double totalPlacedBid = fundHistoryRepo.getTotalPlacedBidAmount();

        Long totalUsers = userRepo.count();
        Long totalBidsCount = bidRepo.count();
        Long activeBidsCount = bidRepo.countActiveBids();

        Double netRevenue = (totalDeposits != null ? totalDeposits : 0) -
                (totalWithdrawals != null ? totalWithdrawals : 0);

        Long pendingWithdrawalRequests = withdrawalRepo.countPendingRequests();

        return DashboardResponse.builder()
                .totalDeposits(totalDeposits)
                .totalWithdrawals(totalWithdrawals)
                .pendingWithdrawalAmount(pendingWithdrawalAmount)
                .totalPlacedBid(totalPlacedBid == 0 ? totalPlacedBid : -1 * totalPlacedBid)
                .totalUsers(totalUsers)
                .totalBidsCount(totalBidsCount)
                .activeBidsCount(activeBidsCount)
                .netRevenue(netRevenue)
                .pendingWithdrawalRequests(pendingWithdrawalRequests)
                .build();
    }
}
