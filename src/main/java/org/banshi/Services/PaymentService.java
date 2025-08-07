package org.banshi.Services;

import com.razorpay.RazorpayException;
import org.banshi.Dtos.PaymentRequest;
import org.banshi.Dtos.PaymentResponse;
import org.banshi.Dtos.VerifyPaymentRequest;

public interface PaymentService {

    PaymentResponse createOrder(PaymentRequest request) throws RazorpayException;

    boolean verifyPayment(VerifyPaymentRequest request) throws Exception;

}
