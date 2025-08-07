package org.banshi.Controllers;

import org.banshi.Dtos.ApiResponse;
import org.banshi.Dtos.FundHistoryDto;
import org.banshi.Services.FundHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/fund-history")
public class FundHistoryController {

    private static final Logger logger = LoggerFactory.getLogger(FundHistoryController.class);

    @Autowired
    private FundHistoryService fundHistoryService;

    // 1. Get fund history by userId
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<FundHistoryDto>>> getFundHistoryByUser(@PathVariable Long userId) {
        logger.info("Fetching fund history for userId={}", userId);
        try {
            List<FundHistoryDto> history = fundHistoryService.getFundHistoryByUser(userId);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Fund history fetched successfully", history));
        } catch (Exception e) {
            logger.warn("Error fetching fund history for userId={}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    // 2. Get fund history by reference
    @GetMapping("/reference/{reference}")
    public ResponseEntity<ApiResponse<List<FundHistoryDto>>> getFundHistoryByReference(@PathVariable String reference) {
        logger.info("Fetching fund history for reference={}", reference);
        try {
            List<FundHistoryDto> history = fundHistoryService.getFundHistoryByReference(reference);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Fund history fetched successfully", history));
        } catch (Exception e) {
            logger.warn("Error fetching fund history for reference={}: {}", reference, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }
}
