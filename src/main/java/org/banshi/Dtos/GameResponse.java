package org.banshi.Dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GameResponse {

    private String id;
    private String name;
    private LocalDateTime openingTime;
    private LocalDateTime closingTime;
}
