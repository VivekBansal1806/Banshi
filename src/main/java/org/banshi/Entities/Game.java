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
    private String gameId;

    private String name;

    private LocalDateTime openingTime;

    private LocalDateTime closingTime;

}
