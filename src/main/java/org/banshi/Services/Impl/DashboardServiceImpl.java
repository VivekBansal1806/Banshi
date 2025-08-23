package org.banshi.Services.Impl;

import org.banshi.Dtos.DashboardResponse;
import org.banshi.Dtos.GameResponse;
import org.banshi.Repositories.*;
import org.banshi.Services.DashboardService;
import org.banshi.Services.GameService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final FundHistoryRepository fundHistoryRepo;
    private final WithdrawalRepository withdrawalRepo;
    private final UserRepository userRepo;
    private final BidRepository bidRepo;
    private final GameRepository gameRepository;
    private final GameService gameService;

    public DashboardServiceImpl(FundHistoryRepository fundHistoryRepo, WithdrawalRepository withdrawalRepo, UserRepository userRepo, BidRepository bidRepo, GameRepository gameRepository, GameService gameService) {
        this.fundHistoryRepo = fundHistoryRepo;
        this.withdrawalRepo = withdrawalRepo;

        this.userRepo = userRepo;
        this.bidRepo = bidRepo;
        this.gameRepository = gameRepository;
        this.gameService = gameService;
    }

    @Override
    public DashboardResponse getDashboardDetails() {
        Double totalDeposits = fundHistoryRepo.getTotalDeposits();
        Double totalWithdrawals = withdrawalRepo.getTotalWithdrawals();
        Double pendingWithdrawalAmount = withdrawalRepo.getPendingWithdrawalAmount();
        Double totalPlacedBid = fundHistoryRepo.getTotalPlacedBidAmount();

        Long totalUsers = userRepo.count();
        List<GameResponse> totalGamesList=gameService.getAllGamesUser();
        Long totalGames = (long) totalGamesList.size();
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
                .totalGames(totalGames)
                .totalBidsCount(totalBidsCount)
                .activeBidsCount(activeBidsCount)
                .netRevenue(netRevenue)
                .pendingWithdrawalRequests(pendingWithdrawalRequests)
                .build();
    }
}
