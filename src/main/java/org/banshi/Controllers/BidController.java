package org.banshi.Controllers;

import org.banshi.Dtos.ApiResponse;
import org.banshi.Dtos.BidRequest;
import org.banshi.Dtos.BidResponse;
import org.banshi.Services.BidService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bids")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class BidController {

    private static final Logger logger = LoggerFactory.getLogger(BidController.class);
    @Autowired
    private BidService bidService;

    // 1. Place a bid
    @PostMapping("/place")
    public ResponseEntity<ApiResponse<BidResponse>> placeBid(@RequestBody BidRequest request) {
        logger.info("Received request to place bid:{}",
                request);
        try {
            BidResponse response = bidService.placeBid(request);
            logger.info("Bid placed successfully: bidId={}, userId={}, gameId={}",
                    response.getBidId(), response.getUserId(), response.getGameId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("SUCCESS", "Bid placed successfully", response));
        } catch (Exception e) {
            logger.error("Failed to place bid for userId={} on gameId={}: {}",
                    request.getUserId(), request.getGameId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    // 2. Get bidding history by user
    @GetMapping("/history/user/{userId}")
    public ResponseEntity<ApiResponse<List<BidResponse>>> getUserBidHistory(@PathVariable Long userId) {
        logger.info("Fetching bid history for userId={}", userId);
        try {
            List<BidResponse> response = bidService.getBidsByUser(userId);
            logger.info("Fetched {} bids (history) for userId={}", response.size(), userId);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "User's bid history fetched", response));
        } catch (Exception e) {
            logger.warn("No bid history found for userId={}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    // 4. Get a bid by ID
    @GetMapping("/{bidId}")
    public ResponseEntity<ApiResponse<BidResponse>> getBidById(@PathVariable Long bidId) {
        logger.info("Fetching bid by bidId={}", bidId);
        try {
            BidResponse response = bidService.getBidById(bidId);
            logger.info("Bid fetched successfully: bidId={}", bidId);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Bid found", response));
        } catch (Exception e) {
            logger.warn("Bid not found for bidId={}: {}", bidId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }
}
