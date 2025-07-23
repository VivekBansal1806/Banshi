package org.banshi.Repository;

import org.banshi.Entities.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepo extends JpaRepository<Game, Long> {
}
