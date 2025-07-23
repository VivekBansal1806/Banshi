package org.banshi.Dtos;

import lombok.Data;
import org.banshi.Entities.GameType;

import java.time.LocalDateTime;

@Data
public class GameRequest {
    private Long id;
    private String name;
    private GameType gameType;
    private LocalDateTime openingTime;
    private LocalDateTime closingTime;
}
