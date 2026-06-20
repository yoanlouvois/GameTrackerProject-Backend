package com.et4.gametrackerproject.controller;

import com.et4.gametrackerproject.controller.api.GameTagApi;
import com.et4.gametrackerproject.dto.GameTagDto;
import com.et4.gametrackerproject.model.Tag;
import com.et4.gametrackerproject.services.GameTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class GameTagController implements GameTagApi {
    @Autowired
    private GameTagService gameTagService;

    public GameTagController(GameTagService gameTagService) {
        this.gameTagService = gameTagService;
    }

    @Override
    public GameTagDto addTagToGame(Integer gameId, Integer tagId) {
        return gameTagService.addTagToGame(gameId,tagId);
    }

    @Override
    public GameTagDto updateTagAssociation(Integer associationId, Integer newTagId) {
        return gameTagService.updateTagAssociation(associationId,newTagId);
    }

    @Override
    public Page<GameTagDto> getTagsForGame(Integer gameId, Pageable pageable) {
        return gameTagService.getTagsForGame(gameId,pageable);
    }

    @Override
    public Page<GameTagDto> getGamesForTag(Integer tagId, Pageable pageable) {
        return gameTagService.getGamesForTag(tagId,pageable);
    }

    @Override
    public Set<GameTagDto> addMultipleTagsToGame(Integer gameId, Set<Tag> tags) {
        return gameTagService.addMultipleTagsToGame(gameId,tags);
    }

    @Override
    public void deleteGameTagById(Integer gameTagId) {
        gameTagService.deleteGameTagById(gameTagId);
    }

    @Override
    public Long countTagsByGame(Integer gameId) {
        return gameTagService.countTagsByGame(gameId);
    }

    @Override
    public Long countGamesByTag(Integer tagId) {
        return gameTagService.countGamesByTag(tagId);
    }

    @Override
    public Set<GameTagDto> getMostPopularTags(Pageable pageable) {
        return gameTagService.getMostPopularTags(pageable);
    }
}
