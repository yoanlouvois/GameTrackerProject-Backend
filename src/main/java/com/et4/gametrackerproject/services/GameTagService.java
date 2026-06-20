package com.et4.gametrackerproject.services;

import com.et4.gametrackerproject.dto.GameTagDto;
import com.et4.gametrackerproject.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface GameTagService {

    Set<GameTagDto> addMultipleTagsToGame(Integer gameId, Set<Tag> tags);

    void deleteGameTagById(Integer gameTagId);

    GameTagDto updateTagAssociation(Integer associationId, Integer newTagId);

    Page<GameTagDto> getTagsForGame(Integer gameId, Pageable pageable);

    Page<GameTagDto> getGamesForTag(Integer tagId, Pageable pageable);

    GameTagDto addTagToGame(Integer gameId, Integer tagId);

    Long countTagsByGame(Integer gameId);

    Long countGamesByTag(Integer tagId);

    Set<GameTagDto> getMostPopularTags(Pageable pageable);
}