package org.banshi.Repositories;

import org.banshi.Entities.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    @Query("SELECT g FROM Game g WHERE DATE(g.openingTime) = CURRENT_DATE")
    List<Game> findAllTodayGames();

}
