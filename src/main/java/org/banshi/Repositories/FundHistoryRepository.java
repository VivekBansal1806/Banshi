package org.banshi.Repositories;

import org.banshi.Entities.FundHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FundHistoryRepository extends JpaRepository<FundHistory, Long> {

    // Get all transactions for a specific user
    Optional<List<FundHistory>> findByUserUserId(Long userId);

    // Optionally: get all transactions by reference (e.g., linked to a bid)
    Optional<List<FundHistory>> findByReference(String reference);

    boolean existsByRazorpayPaymentId(String id);

    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM FundHistory f WHERE f.transactionType = 'RECHARGE'")
    Double getTotalDeposits();

    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM FundHistory f WHERE f.transactionType = 'BET_PLACED'")
    Double getTotalPlacedBidAmount();

}
