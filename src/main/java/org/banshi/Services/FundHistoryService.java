package org.banshi.Services;

import org.banshi.Dtos.FundHistoryDto;

import java.util.List;

public interface FundHistoryService {

    List<FundHistoryDto> getFundHistoryByUser(Long userId);

    List<FundHistoryDto> getFundHistoryByReference(String reference);
}
