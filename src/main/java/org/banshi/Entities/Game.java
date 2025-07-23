package org.banshi.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game {

    @Id
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private GameType gameType;

    private LocalDateTime openingTime;

    private LocalDateTime closingTime;

    private String result; // nullable — to be set after game is resolved
}
