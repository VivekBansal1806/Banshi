package org.banshi.Services.Impl;

import org.banshi.Dtos.DeclareResultRequest;
import org.banshi.Dtos.GameRequest;
import org.banshi.Dtos.GameResponse;
import org.banshi.Entities.Bid;
import org.banshi.Entities.Enums.BidResultStatus;
import org.banshi.Entities.Enums.BidTiming;
import org.banshi.Entities.Enums.BidType;
import org.banshi.Entities.Enums.TransactionType;
import org.banshi.Entities.FundHistory;
import org.banshi.Entities.Game;
import org.banshi.Entities.User;
import org.banshi.Exceptions.ResourceNotFoundException;
import org.banshi.Repositories.BidRepository;
import org.banshi.Repositories.FundHistoryRepository;
import org.banshi.Repositories.GameRepository;
import org.banshi.Repositories.UserRepository;
import org.banshi.Services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private BidRepository bidRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FundHistoryRepository fundHistoryRepository;

    @Override
    public GameResponse createGame(GameRequest request) {
        Game game = Game.builder()
                .name(request.getName())
                .openingTime(request.getOpeningTime())
                .closingTime(request.getClosingTime())
                .gameResult(null)
                .openResult(null)
                .closeResult(null)
                .build();
        return mapToResponse(gameRepository.save(game));
    }

    @Override
    public List<GameResponse> getAllGames() {
        List<Game> games = gameRepository.findAll();
        if (games.isEmpty()) {
            throw new ResourceNotFoundException("No games found");
        }
        return games.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public GameResponse getGameById(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with ID: " + id));
        return mapToResponse(game);
    }

    @Override
    public void deleteGame(Long id) {
        if (!gameRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete — game not found with ID: " + id);
        }
        gameRepository.deleteById(id);
    }

    @Override
    public GameResponse declareGameResult(DeclareResultRequest request) {
        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with ID: " + request.getGameId()));

        if (game.getGameResult() != null) {
            throw new IllegalStateException("Game result already declared");
        }

        String openResult = request.getOpenResult();
        String closeResult = request.getCloseResult();

        String jodi = "" +
                openResult.chars().map(Character::getNumericValue).sum() % 10 +
                closeResult.chars().map(Character::getNumericValue).sum() % 10;

        game.setOpenResult(openResult);
        game.setCloseResult(closeResult);
        game.setGameResult(openResult + "-" + jodi + "-" + closeResult);

        return mapToResponse(gameRepository.save(game));
    }

    @Override
    public String evaluateGameResult(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found"));

        if (game.getOpenResult() == null || game.getCloseResult() == null) {
            throw new IllegalArgumentException("Game results are not declared yet.");
        }

        String result = game.getGameResult();
        String openDigit = String.valueOf(result.charAt(4));
        String closeDigit = String.valueOf(result.charAt(5));
        String jodi = openDigit + closeDigit;

        List<Bid> bids = bidRepository.findByGameGameId(gameId);

        for (Bid bid : bids) {
            try {
                // Skip already evaluated bids
                if (bid.getResultStatus() != BidResultStatus.PENDING) continue;

                boolean won = checkIfWon(bid, game, openDigit, closeDigit, jodi);

                if (won) {
                    bid.setResultStatus(BidResultStatus.WON);
                    double reward = calculateReward(bid.getBidType(), bid.getAmount());
                    bid.setPayout(reward);

                    User user = bid.getUser();
                    user.setBalance(user.getBalance() + reward);

                    // ✅ Create FundHistory record for winning
                    FundHistory fundHistory = FundHistory.builder()
                            .user(user)
                            .amount(reward) // credit
                            .transactionType(TransactionType.WINNING)
                            .reference("BID-" + bid.getBidId()) // reference: bid ID
                            .transactionTime(LocalDateTime.now())
                            .build();

                    fundHistoryRepository.save(fundHistory);
                    userRepository.save(user);
                } else {
                    bid.setResultStatus(BidResultStatus.LOST);
                    bid.setPayout(0.0); // Optional
                }

                bidRepository.save(bid);

            } catch (Exception ex) {
                System.err.println("Failed to evaluate bid ID: " + bid.getBidId() + ". Reason: " + ex.getMessage());
            }
        }

        return "Result evaluated successfully for game ID: " + gameId;
    }


    private boolean checkIfWon(Bid bid, Game game, String openDigit, String closeDigit, String jodi) {
        String bidNum = bid.getNumber();
        return switch (bid.getBidType()) {
            case SINGLE_DIGIT -> (bid.getBidTiming() == BidTiming.OPEN && bidNum.equals(openDigit)) ||
                    (bid.getBidTiming() == BidTiming.CLOSE && bidNum.equals(closeDigit));
            case JODI_DIGIT -> bidNum.equals(jodi);
            case SINGLE_PANNA, DOUBLE_PANNA, TRIPLE_PANNA ->
                    (bid.getBidTiming() == BidTiming.OPEN && bidNum.equals(game.getOpenResult())) ||
                            (bid.getBidTiming() == BidTiming.CLOSE && bidNum.equals(game.getCloseResult()));
            case HALF_SANGAM -> {
                String[] half = bidNum.split("-");
                yield half.length == 2 &&
                        ((half[0].equals(openDigit) && half[1].equals(game.getCloseResult())) ||
                                (half[0].equals(closeDigit) && half[1].equals(game.getOpenResult())));
            }
            case FULL_SANGAM -> {
                String[] full = bidNum.split("-");
                yield full.length == 2 &&
                        full[0].equals(game.getOpenResult()) &&
                        full[1].equals(game.getCloseResult());
            }
        };
    }

    private double calculateReward(BidType bidType, double amount) {
        return switch (bidType) {
            case SINGLE_DIGIT -> amount * 10.0;
            case JODI_DIGIT -> amount * 100.0;
            case SINGLE_PANNA -> amount * 160.0;
            case DOUBLE_PANNA -> amount * 320.0;
            case TRIPLE_PANNA -> amount * 700.0;
            case HALF_SANGAM -> amount * 1000.0;
            case FULL_SANGAM -> amount * 10000.0;
        };
    }

    private GameResponse mapToResponse(Game game) {
        return GameResponse.builder()
                .gameId(game.getGameId())
                .name(game.getName())
                .openingTime(game.getOpeningTime())
                .closingTime(game.getClosingTime())
                .openResult(game.getOpenResult())
                .closeResult(game.getCloseResult())
                .build();
    }
}
