package org.banshi.Controllers;

import org.banshi.Dtos.ApiResponse;
import org.banshi.Dtos.GameResponse;
import org.banshi.Services.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private GameService gameService;

    // 2. Get all games for users
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<GameResponse>>> getAllGamesUser() {
        logger.info("Fetching all games");
        try {
            List<GameResponse> games = gameService.getAllGamesUser();
            logger.info("Fetched {} games", games.size());
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "All games fetched successfully", games));
        } catch (Exception e) {
            logger.error("Error fetching all games: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    // 3. Get game by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GameResponse>> getGameById(@PathVariable Long id) {
        logger.info("Fetching game with ID: {}", id);
        try {
            GameResponse game = gameService.getGameById(id);
            logger.info("Game fetched successfully: {}", id);
            return ResponseEntity.ok(new ApiResponse<>("SUCCESS", "Game fetched successfully", game));
        } catch (Exception e) {
            logger.warn("Game not found with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

}
