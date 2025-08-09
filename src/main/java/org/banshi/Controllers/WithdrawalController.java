package org.banshi.Controllers;

import org.banshi.Dtos.ApiResponse;
import org.banshi.Dtos.WithdrawalRequest;
import org.banshi.Dtos.WithdrawalResponse;
import org.banshi.Services.WithdrawalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/withdrawals")
@PreAuthorize("hasAnyRole('ADMIN','USER')")
public class WithdrawalController {

    private static final Logger logger = LoggerFactory.getLogger(WithdrawalController.class); // ✅ Fixed class name
    private final WithdrawalService withdrawalService;

    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<String>> requestWithdraw(@RequestBody WithdrawalRequest dto) {
        logger.info("Withdrawal request received | UserId: {} | Amount: {} | UPI: {}",
                dto.getUserId(), dto.getAmount(), dto.getUpiId());

        try {
            String resultMessage = withdrawalService.requestWithdrawal(dto);
            logger.info("Withdrawal request processed successfully | UserId: {}", dto.getUserId());
            return ResponseEntity.ok(new ApiResponse<>("success", resultMessage, null));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid withdrawal request | UserId: {} | Reason: {}", dto.getUserId(), e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>("error", e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Withdrawal request failed | UserId: {} | Reason: {}", dto.getUserId(), e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>("error", "Unexpected error occurred", null));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<WithdrawalResponse>>> getUserWithdrawals(@PathVariable Long userId) {
        logger.info("Fetching withdrawals for UserId: {}", userId);
        try {
            List<WithdrawalResponse> dtos = withdrawalService.getUserWithdrawals(userId);
            return ResponseEntity.ok(new ApiResponse<>("success", "User withdrawals fetched", dtos));
        } catch (Exception e) {
            logger.error("Error fetching withdrawals for UserId: {} | Reason: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>("error", "Could not fetch user withdrawals", null));
        }
    }

    @GetMapping("/{withdrawalId}")
    public ResponseEntity<ApiResponse<WithdrawalResponse>> getWithdrawalById(@PathVariable Long withdrawalId) {
        logger.info("Fetching withdrawal details | WithdrawalId: {}", withdrawalId);
        try {
            WithdrawalResponse dto = withdrawalService.getWithdrawalById(withdrawalId);
            return ResponseEntity.ok(new ApiResponse<>("success", "Withdrawal details fetched", dto));
        } catch (Exception e) {
            logger.error("Error fetching withdrawal details | WithdrawalId: {} | Reason: {}", withdrawalId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>("error", "Could not fetch withdrawal details", null));
        }
    }


}
