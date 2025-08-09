package org.banshi.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.banshi.Dtos.WithdrawalRequest;
import org.banshi.Dtos.WithdrawalResponse;
import org.banshi.Entities.Enums.TransactionType;
import org.banshi.Entities.Enums.WithdrawalStatus;
import org.banshi.Entities.FundHistory;
import org.banshi.Entities.User;
import org.banshi.Entities.Withdrawal;
import org.banshi.Repositories.FundHistoryRepository;
import org.banshi.Repositories.UserRepository;
import org.banshi.Repositories.WithdrawalRepository;
import org.banshi.Services.WithdrawalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawalServiceImpl implements WithdrawalService {

    private static final long MIN_WITHDRAW_AMOUNT = 300;
    private final UserRepository userRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final FundHistoryRepository fundHistoryRepository;

    @Override
    @Transactional
    public String requestWithdrawal(WithdrawalRequest dto) {
        try {
            log.info("Starting withdrawal request for UserId: {}", dto.getUserId());

            // 1️⃣ Check user existence
            User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> {
                log.error("User not found: {}", dto.getUserId());
                return new RuntimeException("User not found");
            });
            // 1️⃣ Minimum amount check
            if (dto.getAmount() < MIN_WITHDRAW_AMOUNT) {
                log.warn("Withdrawal amount below minimum | UserId: {} | Requested: {}", dto.getUserId(), dto.getAmount());
                throw new RuntimeException("Minimum withdrawal amount is ₹" + MIN_WITHDRAW_AMOUNT);
            }

            // 2️⃣ Validate balance
            if (user.getBalance() < dto.getAmount()) {
                log.warn("Insufficient balance | UserId: {} | Balance: {} | Requested: {}", dto.getUserId(), user.getBalance(), dto.getAmount());
                throw new RuntimeException("Insufficient balance");
            }


            // 3️⃣ Deduct balance from user
            user.setBalance(user.getBalance() - dto.getAmount());
            userRepository.save(user);
            log.info("Balance updated for UserId: {} | New Balance: {}", dto.getUserId(), user.getBalance());

            // 4️⃣ Save withdrawal record
            Withdrawal withdrawal = Withdrawal.builder().user(user).amount(dto.getAmount()).upiId(dto.getUpiId()).status(WithdrawalStatus.PENDING).requestedAt(LocalDateTime.now()).build();

            withdrawalRepository.save(withdrawal);
            log.info("Withdrawal record created (PENDING) for UserId: {}", dto.getUserId());

            // 5️⃣ Save fund history record
            FundHistory fundHistory = FundHistory.builder().user(user).amount(-dto.getAmount()) // Negative for debit
                    .transactionType(TransactionType.WITHDRAW).reference("Withdrawal Request ID: " + withdrawal.getId()).transactionTime(LocalDateTime.now()).build();

            fundHistoryRepository.save(fundHistory);
            log.info("Fund history recorded for UserId: {} | Amount: {}", dto.getUserId(), -dto.getAmount());

            return "Withdrawal request submitted successfully.";

        } catch (RuntimeException e) {
            log.error("Error processing withdrawal | UserId: {} | Reason: {}", dto.getUserId(), e.getMessage());
            throw e; // Pass to controller to handle API response
        } catch (Exception e) {
            log.error("Unexpected error during withdrawal for UserId: {}", dto.getUserId(), e);
            throw new RuntimeException("An unexpected error occurred");
        }
    }

    @Override
    @Transactional
    public String decideWithdrawal(Long withdrawalId, boolean approve, String rejectionMessage) {
        Withdrawal withdrawal = withdrawalRepository.findById(withdrawalId).orElseThrow(() -> new RuntimeException("Withdrawal not found"));

        if (withdrawal.getStatus() != WithdrawalStatus.PENDING) {
            throw new RuntimeException("Withdrawal is not pending");
        }

        if (approve) {
            withdrawal.setStatus(WithdrawalStatus.APPROVED);
            withdrawal.setProcessedAt(LocalDateTime.now());

            fundHistoryRepository.save(FundHistory.builder().user(withdrawal.getUser()).amount(-withdrawal.getAmount()).transactionType(TransactionType.WITHDRAW).reference("Withdrawal Approved: " + withdrawal.getId()).transactionTime(LocalDateTime.now()).build());

            log.info("Withdrawal approved | WithdrawalId: {} | UserId: {}", withdrawal.getId(), withdrawal.getUser().getUserId());
            return "Withdrawal approved successfully";
        } else {
            if (rejectionMessage == null || rejectionMessage.trim().isEmpty()) {
                throw new RuntimeException("Rejection message is required when rejecting a withdrawal");
            }

            withdrawal.setStatus(WithdrawalStatus.REJECTED);
            withdrawal.setProcessedAt(LocalDateTime.now());
            withdrawal.setRejectionReason(rejectionMessage); // you'll need to add this field to entity

            log.info("Withdrawal rejected | WithdrawalId: {} | UserId: {} | Reason: {}", withdrawal.getId(), withdrawal.getUser().getUserId(), rejectionMessage);

            return "Withdrawal rejected: " + rejectionMessage;
        }
    }

    @Override
    public List<WithdrawalResponse> getUserWithdrawals(Long userId) {
        return withdrawalRepository.findByUserUserIdOrderByRequestedAtDesc(userId)
                .stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<WithdrawalResponse> getAllWithdrawals() {
        return withdrawalRepository.findAllByOrderByRequestedAtDesc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<WithdrawalResponse> getPendingWithdrawals() {
        return withdrawalRepository.findByStatusOrderByRequestedAtAsc(WithdrawalStatus.PENDING)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<WithdrawalResponse> getApprovedWithdrawals() {
        return withdrawalRepository.findByStatusOrderByRequestedAtAsc(WithdrawalStatus.APPROVED)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<WithdrawalResponse> getRejectedWithdrawals() {
        return withdrawalRepository.findByStatusOrderByRequestedAtAsc(WithdrawalStatus.REJECTED)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public WithdrawalResponse getWithdrawalById(Long id) {
        return withdrawalRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Withdrawal not found"));
    }

    private WithdrawalResponse toDto(Withdrawal w) {
        return WithdrawalResponse.builder().id(w.getId()).userId(w.getUser().getUserId()) // adjust getter name to your User entity
                .userName(w.getUser().getName()) // optional
                .amount(w.getAmount()).upiId(w.getUpiId()).status(w.getStatus()).rejectionReason(w.getRejectionReason()).requestedAt(w.getRequestedAt()).processedAt(w.getProcessedAt()).build();
    }
}
