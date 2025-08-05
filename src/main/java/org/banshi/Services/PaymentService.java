package org.banshi.Services;

import com.razorpay.RazorpayException;
import org.banshi.Dtos.OrderResponse;
import org.banshi.Dtos.VerifyPaymentRequest;

public interface PaymentService {

    OrderResponse createOrder(Long userId, Double amount) throws RazorpayException;
    boolean verifyPayment(VerifyPaymentRequest request) throws Exception;

}
