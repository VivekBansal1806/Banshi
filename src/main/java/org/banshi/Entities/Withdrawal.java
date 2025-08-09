package org.banshi.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banshi.Entities.Enums.WithdrawalStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "withdrawals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Withdrawal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private String upiId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WithdrawalStatus status; // PENDING, APPROVED, REJECTED

    @Column(length = 255)
    private String rejectionReason;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    private LocalDateTime processedAt;
}
