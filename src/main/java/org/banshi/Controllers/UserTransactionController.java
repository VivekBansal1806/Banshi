package org.banshi.Controllers;

import jakarta.persistence.EntityNotFoundException;
import org.banshi.Dtos.ApiResponse;
import org.banshi.Dtos.UserTransactionResponse;
import org.banshi.Services.UserTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class UserTransactionController {

    @Autowired
    private UserTransactionService transactionService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<UserTransactionResponse>>> getUserTransactions(@PathVariable Long userId) {
        try {
            List<UserTransactionResponse> transactions = transactionService.getTransactionsByUser(userId);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Transactions fetched successfully", transactions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @PostMapping("/add-balance/{userId}")
    public ResponseEntity<ApiResponse<String>> addBalance(
            @PathVariable Long userId,
            @RequestParam Long amount) {

        logger.info("Received request to add balance. UserId={}, Amount={}", userId, amount);

        try {
            if (amount == null || amount <= 0) {
                logger.warn("Invalid amount provided for add balance. UserId={}, Amount={}", userId, amount);
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>("ERROR", "Amount must be greater than zero", null)
                );
            }

            // call service method
            transactionService.addBalance(userId, amount);

            logger.info("Balance successfully added for UserId={}, Amount={}", userId, amount);
            return ResponseEntity.ok(
                    new ApiResponse<>("SUCCESS", "Balance added successfully", "Balance updated for userId " + userId)
            );

        } catch (EntityNotFoundException ex) {
            logger.error("User not found while adding balance. UserId={}, Error={}", userId, ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>("ERROR", "User not found", null)
            );
        } catch (Exception ex) {
            logger.error("Unexpected error while adding balance. UserId={}, Error={}", userId, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>("ERROR", "Failed to add balance due to server error", null)
            );
        }
    }

}
