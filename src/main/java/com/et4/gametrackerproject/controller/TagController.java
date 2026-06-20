package com.et4.gametrackerproject.controller;

import com.et4.gametrackerproject.controller.api.TagApi;
import com.et4.gametrackerproject.dto.TagDto;
import com.et4.gametrackerproject.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TagController implements TagApi {

    @Autowired
    private TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @Override
    public TagDto createTag(TagDto tagDto) {
        return tagService.createTag(tagDto);
    }

    @Override
    public TagDto updateTag(Integer tagId, TagDto tagDto) {
        return tagService.updateTag(tagId, tagDto);
    }

    @Override
    public void deleteTag(Integer tagId) {
        tagService.deleteTagById(tagId);
    }

    @Override
    public TagDto getTagById(Integer tagId) {
        return tagService.getTagById(tagId);
    }

    @Override
    public TagDto getTagByName(String name) {
        return tagService.getTagByName(name);
    }

    @Override
    public Page<TagDto> getAllTags(Pageable pageable) {
        return tagService.getAllTags(pageable);
    }

    @Override
    public List<TagDto> getTagsForGame(Integer gameId) {
        return tagService.getTagsForGame(gameId);
    }

    @Override
    public Page<TagDto> searchTags(String query, Pageable pageable) {
        return tagService.searchTags(query, pageable);
    }
}
