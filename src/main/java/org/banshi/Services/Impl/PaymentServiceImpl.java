package org.banshi.Services.Impl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.banshi.Dtos.PaymentRequest;
import org.banshi.Dtos.PaymentResponse;
import org.banshi.Dtos.VerifyPaymentRequest;
import org.banshi.Entities.Enums.TransactionType;
import org.banshi.Entities.FundHistory;
import org.banshi.Entities.User;
import org.banshi.Repositories.FundHistoryRepository;
import org.banshi.Repositories.UserRepository;
import org.banshi.Services.PaymentService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Value("${razorpay.key_secret}")
    private String keySecret;
    private final RazorpayClient razorpayClient;
    private final UserRepository userRepo;
    private final FundHistoryRepository fundHistoryRepository;

    @Override
    public PaymentResponse createOrder(PaymentRequest request) throws RazorpayException {
        logger.info("Creating Razorpay order for userId={}, amount={}", request.getUserId(), request.getAmount());

        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));

        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        JSONObject options = new JSONObject();
        options.put("amount", request.getAmount() * 100); // in paise
        options.put("currency", "INR");
        options.put("payment_capture", 1);

        Order order = razorpayClient.orders.create(options);

        logger.info("Order created successfully: orderId={}, userId={}", order.get("id"), request.getUserId());
        return new PaymentResponse(order.get("id"), request.getAmount(), "INR");
    }

    @Override
    public boolean verifyPayment(VerifyPaymentRequest request) {
        logger.info("Verifying Razorpay payment: {}", request);

        // Check for duplicate payment
        boolean alreadyExists = fundHistoryRepository.existsByRazorpayPaymentId(request.getRazorpayPaymentId());
        if (alreadyExists) {
            logger.warn("Payment already verified: {}", request.getRazorpayPaymentId());
            return true;
        }

        try {
            // Validate Razorpay signature
            String data = request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId();
            String generatedSignature = hmacSHA256(data, keySecret);

            if (!generatedSignature.equals(request.getRazorpaySignature())) {
                logger.warn("Invalid Razorpay signature for paymentId={}", request.getRazorpayPaymentId());
                return false;
            }

            // Fetch user
            User user = userRepo.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));

            // Save FundHistory
            FundHistory history = FundHistory.builder()
                    .user(user)
                    .amount(request.getAmount())
                    .transactionType(TransactionType.RECHARGE)
                    .reference("Razorpay")
                    .razorpayOrderId(request.getRazorpayOrderId())
                    .razorpayPaymentId(request.getRazorpayPaymentId())
                    .razorpaySignature(request.getRazorpaySignature())
                    .transactionTime(LocalDateTime.now())
                    .build();
            fundHistoryRepository.save(history);

            // Update wallet
            user.setBalance(user.getBalance() + request.getAmount());
            userRepo.save(user);

            logger.info("Payment verified and wallet credited successfully for userId={}", user.getUserId());
            return true;

        } catch (Exception ex) {
            logger.error("Error while verifying payment", ex);
            return false;
        }
    }

    private String hmacSHA256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }
}
