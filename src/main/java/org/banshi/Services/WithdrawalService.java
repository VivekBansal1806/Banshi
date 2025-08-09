package org.banshi.Services;

import org.banshi.Dtos.WithdrawalRequest;
import org.banshi.Dtos.WithdrawalResponse;

import java.util.List;

public interface WithdrawalService {
    String requestWithdrawal(WithdrawalRequest dto);

    String decideWithdrawal(Long id, boolean approve, String rejectionMessage);

    List<WithdrawalResponse> getUserWithdrawals(Long userId);

    List<WithdrawalResponse> getAllWithdrawals();

    List<WithdrawalResponse> getPendingWithdrawals();

    List<WithdrawalResponse> getRejectedWithdrawals();

    List<WithdrawalResponse> getApprovedWithdrawals();

    WithdrawalResponse getWithdrawalById(Long id);

}
