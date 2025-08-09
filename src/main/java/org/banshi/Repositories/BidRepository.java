package org.banshi.Repositories;

import org.banshi.Entities.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByUserUserId(Long userId);

    List<Bid> findByGameGameId(Long gameId);

    // Count only active bids (those with resultStatus = 'PENDING')
    @Query("SELECT COUNT(b) FROM Bid b WHERE b.resultStatus = 'PENDING'")
    Long countActiveBids();
}
