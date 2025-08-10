package org.banshi.Services.Impl;

import org.banshi.Dtos.FundHistoryDto;
import org.banshi.Entities.FundHistory;
import org.banshi.Exceptions.ResourceNotFoundException;
import org.banshi.Repositories.FundHistoryRepository;
import org.banshi.Services.FundHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FundHistoryServiceImpl implements FundHistoryService {

    @Autowired
    private FundHistoryRepository fundHistoryRepository;

    @Override
    public List<FundHistoryDto> getFundHistoryByUser(Long userId) {
        List<FundHistory> history = fundHistoryRepository.findByUserUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No fund history found for userId=" + userId));

        return history.stream()
                .map(this::mapToFhd)
                .collect(Collectors.toList()).reversed();
    }

    @Override
    public List<FundHistoryDto> getFundHistoryByReference(String reference) {
        List<FundHistory> history = fundHistoryRepository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("No fund history found for reference=" + reference));

        return history.stream()
                .map(this::mapToFhd)
                .collect(Collectors.toList());
    }

    private FundHistoryDto mapToFhd(FundHistory fh) {
        return FundHistoryDto.builder()
                .historyId(fh.getHistoryId())
                .userId(fh.getUser().getUserId())
                .amount(fh.getAmount())
                .transactionType(fh.getTransactionType().name())
                .reference(fh.getReference())
                .razorpayOrderId(fh.getRazorpayOrderId())
                .razorpayPaymentId(fh.getRazorpayPaymentId())
                .razorpaySignature(fh.getRazorpaySignature())
                .transactionTime(LocalDateTime.now())
                .build();

    }
}
