package com.et4.gametrackerproject.services;

import com.et4.gametrackerproject.dto.TagDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService {

    // Opérations de base
    TagDto createTag(TagDto tagDto);
    TagDto updateTag(Integer tagId, TagDto tagDto);
    void deleteTagById(Integer tagId);

    // Récupération
    TagDto getTagById(Integer tagId);
    TagDto getTagByName(String name);
    Page<TagDto> getAllTags(Pageable pageable);

    // Gestion des relations
    List<TagDto> getTagsForGame(Integer gameId);

    // Recherche
    Page<TagDto> searchTags(String query, Pageable pageable);
}