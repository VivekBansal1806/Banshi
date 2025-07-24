package org.banshi.Services.Impl;

import org.banshi.Dtos.GameRequest;
import org.banshi.Dtos.GameResponse;
import org.banshi.Entities.Game;
import org.banshi.Repositories.GameRepository;
import org.banshi.Services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private GameRepository gameRepository;

    @Override
    public GameResponse createGame(GameRequest request) {
        if (gameRepository.existsById(request.getId())) {
            throw new IllegalArgumentException("Game with this ID already exists");
        }

        Game game = Game.builder()
                .gameId(request.getId())
                .name(request.getName())
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
    public GameResponse getGameById(String id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found with ID: " + id));
        return mapToResponse(game);
    }

    @Override
    public void deleteGame(String  id) {
        if (!gameRepository.existsById(id)) {
            throw new RuntimeException("Game not found");
        }
        gameRepository.deleteById(id);
    }

    private GameResponse mapToResponse(Game game) {
        return GameResponse.builder()
                .id(game.getGameId())
                .name(game.getName())
                .openingTime(game.getOpeningTime())
                .closingTime(game.getClosingTime())
                .build();
    }
}
