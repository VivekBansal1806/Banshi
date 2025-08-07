package org.banshi.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banshi.Entities.Enums.TransactionType;

import java.time.LocalDateTime;

@Entity
@Table(name = "fund_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double amount;  // Positive for credit, negative for debit

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    private String reference; // e.g., UPI Txn ID, Bid ID, Withdrawal Request ID

    // 🔽 Razorpay Specific Fields
    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String razorpaySignature; // Optional

    @Column(nullable = false)
    private LocalDateTime transactionTime;
}
