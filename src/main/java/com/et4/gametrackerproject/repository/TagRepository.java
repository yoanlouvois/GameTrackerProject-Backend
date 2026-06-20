package com.et4.gametrackerproject.repository;

import com.et4.gametrackerproject.model.Game;
import com.et4.gametrackerproject.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag,Integer> {

    // Recherches de base
    Optional<Tag> findByName(String name);

    Optional<Tag> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Tag> findByNameContainingIgnoreCase(String name);

    Page<Tag> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Trouver des tags par nombre de jeux associés
    @Query("SELECT t, COUNT(gt.game) FROM Tag t JOIN t.gameTags gt GROUP BY t ORDER BY COUNT(gt.game) DESC")
    List<Object[]> findTagsByPopularity();

    @Query("SELECT t, COUNT(gt.game) FROM Tag t JOIN t.gameTags gt GROUP BY t ORDER BY COUNT(gt.game) DESC")
    Page<Object[]> findTagsByPopularity(Pageable pageable);

    // Trouver les tags d'un jeu spécifique
    @Query("SELECT t FROM Tag t JOIN t.gameTags gt WHERE gt.game = :game")
    List<Tag> findTagsByGame(@Param("game") Game game);

    @Query("SELECT t FROM Tag t JOIN t.gameTags gt WHERE gt.game.id = :gameId")
    List<Tag> findTagsByGameId(@Param("gameId") Integer gameId);

    // Trouver les jeux qui ont tous les tags spécifiés
    @Query("SELECT gt.game FROM GameTag gt WHERE gt.tag IN :tags GROUP BY gt.game HAVING COUNT(DISTINCT gt.tag) = :tagCount")
    List<Game> findGamesByAllTags(@Param("tags") Set<Tag> tags, @Param("tagCount") Long tagCount);

    // Trouver les jeux qui ont au moins un des tags spécifiés
    @Query("SELECT DISTINCT gt.game FROM GameTag gt WHERE gt.tag IN :tags")
    List<Game> findGamesByAnyTag(@Param("tags") Set<Tag> tags);

    // Trouver les tag par tagId
    @Query("SELECT t FROM Tag t JOIN t.gameTags gt WHERE gt.id = :gameTagId")
    Optional<Tag> findByGameTagId(@Param("gameTagId") Integer gameTagId);
}
