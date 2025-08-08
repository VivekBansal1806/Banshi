package org.banshi.Controllers;

import org.banshi.Dtos.ApiResponse;
import org.banshi.Dtos.PaymentRequest;
import org.banshi.Dtos.PaymentResponse;
import org.banshi.Dtos.VerifyPaymentRequest;
import org.banshi.Services.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse<PaymentResponse>> createOrder(@RequestBody PaymentRequest request) {
        try {
            PaymentResponse order = paymentService.createOrder(request);
            return ResponseEntity.ok(new ApiResponse<>("success", "Order created successfully", order));
        } catch (Exception e) {
            logger.error("Error creating order for userId={}", request.getUserId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("error", e.getMessage(), null));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Boolean>> verifyPayment(@RequestBody VerifyPaymentRequest request) {
        try {
            boolean verified = paymentService.verifyPayment(request);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse<>("success", "Payment verified", verified));
        } catch (Exception e) {
            logger.error("Error verifying payment", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("error", e.getMessage(), false));
        }
    }
}
