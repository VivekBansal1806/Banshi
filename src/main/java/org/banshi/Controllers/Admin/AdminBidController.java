package org.banshi.Controllers.Admin;

import org.banshi.Dtos.ApiResponse;
import org.banshi.Dtos.BidResponse;
import org.banshi.Services.BidService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/bids")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBidController {

    private static final Logger logger = LoggerFactory.getLogger(AdminBidController.class);
    private final BidService bidService;

    public AdminBidController(BidService bidService) {
        this.bidService = bidService;
    }

    // 3. Get bidding history by game
    @GetMapping("/history/game/{gameId}")
    public ResponseEntity<ApiResponse<List<BidResponse>>> getGameBidHistory(@PathVariable Long gameId) {
        logger.info("Fetching bid history for gameId={}", gameId);
        try {
            List<BidResponse> response = bidService.getBidsByGame(gameId);
            logger.info("Fetched {} bids (history) for gameId={}", response.size(), gameId);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Game's bid history fetched", response));
        } catch (Exception e) {
            logger.warn("No bid history found for gameId={}: {}", gameId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

}
