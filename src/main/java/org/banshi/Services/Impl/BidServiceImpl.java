package org.banshi.Services.Impl;

import org.banshi.Dtos.BidRequest;
import org.banshi.Dtos.BidResponse;
import org.banshi.Entities.Bid;
import org.banshi.Entities.Enums.BidResultStatus;
import org.banshi.Entities.Enums.BidTiming;
import org.banshi.Entities.Enums.BidType;
import org.banshi.Entities.Enums.TransactionType;
import org.banshi.Entities.FundHistory;
import org.banshi.Entities.Game;
import org.banshi.Entities.User;
import org.banshi.Exceptions.InsufficientBalanceException;
import org.banshi.Exceptions.ResourceNotFoundException;
import org.banshi.Repositories.BidRepository;
import org.banshi.Repositories.FundHistoryRepository;
import org.banshi.Repositories.GameRepository;
import org.banshi.Repositories.UserRepository;
import org.banshi.Services.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BidServiceImpl implements BidService {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private FundHistoryRepository fundHistoryRepository;

    @Override
    public List<BidResponse> getBidsByUser(Long userId) {

        List<Bid> history = bidRepository.findByUserUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No Bid history found for userId=" + userId));

        List<BidResponse> dtos = history
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        Collections.reverse(dtos);
        return dtos;
    }

    @Override
    public List<BidResponse> getBidsByGame(Long gameId) {
        return bidRepository.findByGameGameId(gameId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BidResponse getBidById(Long bidId) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found with ID: " + bidId));
        return mapToResponse(bid);
    }

    private BidResponse mapToResponse(Bid bid) {
        return BidResponse.builder()
                .bidId(bid.getBidId())
                .userId(bid.getUser().getUserId())
                .gameId(bid.getGame().getGameId())
                .bidType(bid.getBidType().toString())
                .bidTiming(bid.getBidTiming() != null ? bid.getBidTiming().toString() : null)
                .number(bid.getNumber())
                .amount(bid.getAmount())
                .resultStatus(bid.getResultStatus())
                .payout(bid.getPayout())
                .placedAt(bid.getPlacedAt())
                .build();
    }

    @Override
    public BidResponse placeBid(BidRequest request) {


        if (requiresTiming(request.getBidType()) && request.getBidTiming() == null) {
            throw new IllegalArgumentException("BidTiming is required for " + request.getBidType());
        }

        validateBidNumber(request.getBidType(), request.getNumber());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new ResourceNotFoundException("Game not found"));

        // 🔒 Block if result already declared
        if (game.getOpenResult() != null || game.getCloseResult() != null) {
            throw new IllegalStateException("Bidding is closed. Game result has already been declared.");
        }

        // ✅ Check balance
        if (user.getBalance() < request.getAmount()) {
            throw new InsufficientBalanceException("Insufficient balance. Your wallet has ₹" + user.getBalance());
        }

        // ✅ Bidding time check
        LocalDateTime now = LocalDateTime.now();

        if (requiresTiming(request.getBidType())) {
            if (request.getBidTiming() == BidTiming.OPEN) {
                if (now.isAfter(game.getOpeningTime())) {
                    throw new IllegalStateException("Bidding for OPEN is closed. Game opened at: " + game.getOpeningTime());
                }
            } else if (request.getBidTiming() == BidTiming.CLOSE) {
                if (now.isAfter(game.getClosingTime())) {
                    throw new IllegalStateException("Bidding for CLOSE is closed. Game closed at: " + game.getClosingTime());
                }
            }
        } else {
            if (now.isAfter(game.getClosingTime())) {
                throw new IllegalStateException("Bidding is closed. Game closed at: " + game.getClosingTime());
            }
        }

        // ✅ Deduct balance
        user.setBalance(user.getBalance() - request.getAmount());
        userRepository.save(user);

        // ✅ Save bid (acts as BidHistory)
        Bid bid = Bid.builder()
                .user(user)
                .game(game)
                .bidType(request.getBidType())
                .bidTiming(request.getBidTiming())
                .number(request.getNumber())
                .amount(request.getAmount())
                .resultStatus(BidResultStatus.PENDING)
                .build();
        Bid savedBid = bidRepository.save(bid);

        // ✅ Create FundHistory record
        FundHistory fundHistory = FundHistory.builder()
                .user(user)
                .amount(-request.getAmount()) // negative for debit
                .transactionType(TransactionType.BET_PLACED)
                .reference("BID-" + savedBid.getBidId())
                .razorpaySignature(null)
                .razorpayOrderId(null)
                .razorpayPaymentId(null)
                .transactionTime(LocalDateTime.now())
                .build();
        fundHistoryRepository.save(fundHistory);

        return mapToResponse(savedBid);
    }

    private boolean requiresTiming(BidType type) {
        return type == BidType.SINGLE_DIGIT ||
                type == BidType.SINGLE_PANNA ||
                type == BidType.DOUBLE_PANNA ||
                type == BidType.TRIPLE_PANNA ||
                type == BidType.HALF_SANGAM;
    }

    private void validateBidNumber(BidType bidType, String number) {
        switch (bidType) {
            case SINGLE_DIGIT:
                if (!number.matches("\\d") || Integer.parseInt(number) > 9)
                    throw new IllegalArgumentException("Invalid SINGLE_DIGIT bid");
                break;
            case JODI_DIGIT:
                if (!number.matches("\\d{2}"))
                    throw new IllegalArgumentException("Invalid JODI_DIGIT bid");
                break;
            case SINGLE_PANNA:
                if (!number.matches("\\d{3}"))
                    throw new IllegalArgumentException("Invalid SINGLE_PANNA bid");
                break;
            case DOUBLE_PANNA:
                if (!number.matches("\\d{3}"))
                    throw new IllegalArgumentException("Invalid DOUBLE_PANNA bid");
                break;
            case TRIPLE_PANNA:
                if (!number.matches("(\\d{3})"))
                    throw new IllegalArgumentException("Invalid TRIPLE_PANNA bid");
                break;
            case HALF_SANGAM:
                if (!number.matches("\\d{1}-\\d{3}") && !number.matches("\\d{3}-\\d{1}"))
                    throw new IllegalArgumentException("Invalid HALF_SANGAM format. Use '4-123' or '123-4'");
                break;
            case FULL_SANGAM:
                if (!number.matches("\\d{3}-\\d{3}"))
                    throw new IllegalArgumentException("Invalid FULL_SANGAM format. Expected format: '123-456'");
                break;
            default:
                throw new IllegalArgumentException("Unsupported BidType");
        }
    }

//    private boolean hasRepeatedDigits(String number) {
//        return number.chars().distinct().count() < number.length();
//    }

//    private boolean hasExactlyOnePair(String number) {
//        Map<Character, Long> freq = number.chars()
//                .mapToObj(c -> (char) c)
//                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
//        return freq.containsValue(2L) && freq.size() == 2;
//    }
}
