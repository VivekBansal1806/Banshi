package org.banshi.Dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GameResponse {
    private Long id;
    private String name;
    private String gameType;
    private LocalDateTime openingTime;
    private LocalDateTime closingTime;
    private String result;
}
