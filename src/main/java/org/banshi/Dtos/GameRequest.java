package org.banshi.Dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GameRequest {

    private String id;
    private String name;
    private LocalDateTime openingTime;
    private LocalDateTime closingTime;
}
