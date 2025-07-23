package org.banshi.Controllers;

import org.banshi.Dtos.GameRequest;
import org.banshi.Dtos.GameResponse;
import org.banshi.Services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {

    @Autowired
    private GameService gameService;

    // 1. Create Game (Admin)
    @PostMapping("/add")
    public ResponseEntity<GameResponse> createGame(@RequestBody GameRequest game) {
        GameResponse savedGame = gameService.createGame(game);
        return ResponseEntity.ok(savedGame);
    }

    // 2. Get all games
    @GetMapping("/all")
    public ResponseEntity<List<GameResponse>> getAllGames() {
        return ResponseEntity.ok(gameService.getAllGames());
    }

    // 3. Get game by ID
    @GetMapping("/{id}")
    public ResponseEntity<GameResponse> getGameById(@PathVariable Long id) {
        return ResponseEntity.ok(gameService.getGameById(id));
    }
    

    // 5. Delete game
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return ResponseEntity.ok("Game deleted successfully");
    }
}
