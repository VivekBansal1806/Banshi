package org.banshi.Services;

import org.banshi.Dtos.GameRequest;
import org.banshi.Dtos.GameResponse;
import org.banshi.Entities.Game;
import org.banshi.Repository.GameRepo;
import org.banshi.Services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private GameRepo gameRepository;

    @Override
    public GameResponse createGame(GameRequest request) {
        if (gameRepository.existsById(request.getId())) {
            throw new IllegalArgumentException("Game with this ID already exists");
        }

        Game game = Game.builder()
                .id(request.getId())
                .name(request.getName())
                .gameType(request.getGameType())
                .openingTime(request.getOpeningTime())
                .closingTime(request.getClosingTime())
                .build();

        return mapToResponse(gameRepository.save(game));
    }

    @Override
    public List<GameResponse> getAllGames() {
        return gameRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public GameResponse getGameById(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found with ID: " + id));
        return mapToResponse(game);
    }

    @Override
    public GameResponse updateGameResult(Long id, String result) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found with ID: " + id));
        game.setResult(result);
        return mapToResponse(gameRepository.save(game));
    }

    @Override
    public void deleteGame(Long id) {
        if (!gameRepository.existsById(id)) {
            throw new RuntimeException("Game not found");
        }
        gameRepository.deleteById(id);
    }

    private GameResponse mapToResponse(Game game) {
        return GameResponse.builder()
                .id(game.getId())
                .name(game.getName())
                .gameType(game.getGameType().toString())
                .openingTime(game.getOpeningTime())
                .closingTime(game.getClosingTime())
                .result(game.getResult())
                .build();
    }
}
