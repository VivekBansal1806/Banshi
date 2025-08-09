package org.banshi.Repositories;

import org.banshi.Entities.Enums.WithdrawalStatus;
import org.banshi.Entities.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {

    List<Withdrawal> findByUserUserIdOrderByRequestedAtDesc(Long userId);

    List<Withdrawal> findAllByOrderByRequestedAtDesc();

    List<Withdrawal> findByStatusOrderByRequestedAtAsc(WithdrawalStatus status);

    @Query("SELECT COALESCE(SUM(w.amount), 0) FROM Withdrawal w WHERE w.status = 'APPROVED'")
    Double getTotalWithdrawals();

    @Query("SELECT COALESCE(SUM(w.amount), 0) FROM Withdrawal w WHERE w.status = 'PENDING'")
    Double getPendingWithdrawalAmount();

    @Query("SELECT COUNT(w) FROM Withdrawal w WHERE w.status = 'PENDING'")
    Long countPendingRequests();
}
