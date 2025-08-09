package org.banshi.Repositories;

import org.banshi.Entities.Enums.WithdrawalStatus;
import org.banshi.Entities.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {

    List<Withdrawal> findByUserUserIdOrderByRequestedAtDesc(Long userId);

    List<Withdrawal> findAllByOrderByRequestedAtDesc();

    List<Withdrawal> findByStatusOrderByRequestedAtAsc(WithdrawalStatus status);

}
