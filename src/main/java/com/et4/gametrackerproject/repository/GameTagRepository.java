package com.et4.gametrackerproject.repository;

import com.et4.gametrackerproject.model.Game;
import com.et4.gametrackerproject.model.GameTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GameTagRepository extends JpaRepository<GameTag,Integer> {

    // Pagination des tags pour un jeu
    @Query("SELECT gt FROM GameTag gt WHERE gt.game.id = :gameId")
    Page<GameTag> findByGame(Integer gameId, Pageable pageable);

    // Pagination des jeux pour un tag
    @Query("SELECT gt FROM GameTag gt WHERE gt.tag.id = :tagId")
    Page<GameTag> findByTag(Integer tagId, Pageable pageable);

    // Recherches de base
    List<GameTag> findByGame(Game game);


    @Query("SELECT gt FROM GameTag gt WHERE gt.tag.id = :tagId")
    Optional<GameTag> findByGameAndTag(Integer game, Integer tagId);

    // Compter le nombre de tags pour un jeu
    @Query("SELECT COUNT(gt) FROM GameTag gt WHERE gt.game.id = :gameId")
    Long countTagsByGame(Integer gameId);

    // Compter le nombre de jeux pour un tag
    @Query("SELECT COUNT(gt) FROM GameTag gt WHERE gt.tag.id = :tagId")
    Long countGamesByTag(Integer tagId);

    // Trouver les tags les plus populaires
    @Query("SELECT gt.tag, COUNT(gt) as tagCount FROM GameTag gt GROUP BY gt.tag ORDER BY tagCount DESC")
    List<Object[]> findMostPopularTags(Pageable pageable);

    // Trouver les tags par id de jeu
    @Query("SELECT gt FROM GameTag gt WHERE gt.game.id = :id")
    Optional<GameTag> findByGameId(Integer id);

    //trouver les gametag par id de tag
    Optional<GameTag> findByTagId(Integer tagId);
}
