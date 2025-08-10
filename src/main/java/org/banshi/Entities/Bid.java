package org.banshi.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banshi.Entities.Enums.BidResultStatus;
import org.banshi.Entities.Enums.BidTiming;
import org.banshi.Entities.Enums.BidType;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bidId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @Enumerated(EnumType.STRING)
    private BidType bidType; // SINGLE_DIGIT, JODI, etc.

    @Enumerated(EnumType.STRING)
    private BidTiming bidTiming; // OPEN or CLOSE

    private String number;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private BidResultStatus resultStatus; // WIN, LOSE, PENDING

    private Double payout = 0.0; // amount won, default 0

    @CreationTimestamp
    private LocalDateTime placedAt;
}
