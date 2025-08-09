package org.banshi.Controllers.Admin;

import org.banshi.Dtos.ApiResponse;
import org.banshi.Dtos.WithdrawalDecisionRequest;
import org.banshi.Dtos.WithdrawalResponse;
import org.banshi.Services.WithdrawalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/withdrawals")
@PreAuthorize("hasRole('ADMIN')")
public class AdminWithdrawalController {

    private static final Logger logger = LoggerFactory.getLogger(AdminWithdrawalController.class); // ✅ Fixed class name
    private final WithdrawalService withdrawalService;

    public AdminWithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    @PostMapping("/decision")
    public ResponseEntity<ApiResponse<String>> decideWithdrawal(@RequestBody WithdrawalDecisionRequest request) {
        logger.info("Admin decision on withdrawal | WithdrawalId: {} | Approve: {}",
                request.getWithdrawalId(), request.getApprove());

        try {
            String message = withdrawalService.decideWithdrawal(
                    request.getWithdrawalId(),
                    request.getApprove(),
                    request.getRejectionMessage()
            );
            logger.info("Withdrawal decision processed | WithdrawalId: {} | Result: {}",
                    request.getWithdrawalId(), message);
            return ResponseEntity.ok(new ApiResponse<>("success", message, null));
        } catch (RuntimeException e) {
            logger.warn("Withdrawal decision failed | WithdrawalId: {} | Reason: {}",
                    request.getWithdrawalId(), e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>("error", e.getMessage(), null));
        } catch (Exception e) {
            logger.error("Unexpected error while deciding withdrawal | WithdrawalId: {}",
                    request.getWithdrawalId(), e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>("error", "Unexpected error occurred", null));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<WithdrawalResponse>>> getAllWithdrawals() {
        logger.info("Admin fetching all withdrawals");
        try {
            List<WithdrawalResponse> all = withdrawalService.getAllWithdrawals();
            return ResponseEntity.ok(new ApiResponse<>("success", "All withdrawals fetched", all));
        } catch (Exception e) {
            logger.error("Error fetching all withdrawals | Reason: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>("error", "Could not fetch all withdrawals", null));
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<WithdrawalResponse>>> getPendingWithdrawals() {
        logger.info("Admin fetching pending withdrawals");
        try {
            List<WithdrawalResponse> pending = withdrawalService.getPendingWithdrawals();
            return ResponseEntity.ok(new ApiResponse<>("success", "Pending withdrawals fetched", pending));
        } catch (Exception e) {
            logger.error("Error fetching pending withdrawals | Reason: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>("error", "Could not fetch pending withdrawals", null));
        }
    }

    @GetMapping("/approved")
    public ResponseEntity<ApiResponse<List<WithdrawalResponse>>> getApprovedWithdrawals() {
        logger.info("Admin fetching approved withdrawals");
        try {
            List<WithdrawalResponse> approved = withdrawalService.getApprovedWithdrawals();
            return ResponseEntity.ok(new ApiResponse<>("success", "Approved withdrawals fetched", approved));
        } catch (Exception e) {
            logger.error("Error fetching approved withdrawals | Reason: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>("error", "Could not fetch approved withdrawals", null));
        }
    }

    @GetMapping("/rejected")
    public ResponseEntity<ApiResponse<List<WithdrawalResponse>>> getRejectedWithdrawals() {
        logger.info("Admin fetching rejected withdrawals");
        try {
            List<WithdrawalResponse> rejected = withdrawalService.getRejectedWithdrawals();
            return ResponseEntity.ok(new ApiResponse<>("success", "Rejected withdrawals fetched", rejected));
        } catch (Exception e) {
            logger.error("Error fetching rejected withdrawals | Reason: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>("error", "Could not fetch rejected withdrawals", null));
        }
    }

}
