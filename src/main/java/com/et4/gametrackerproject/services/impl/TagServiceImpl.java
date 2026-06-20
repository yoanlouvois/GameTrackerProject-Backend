package com.et4.gametrackerproject.services.impl;

import com.et4.gametrackerproject.dto.TagDto;
import com.et4.gametrackerproject.exception.EntityNotFoundException;
import com.et4.gametrackerproject.exception.ErrorCodes;
import com.et4.gametrackerproject.exception.InvalidEntityException;
import com.et4.gametrackerproject.exception.InvalidOperationException;
import com.et4.gametrackerproject.model.Game;
import com.et4.gametrackerproject.model.GameTag;
import com.et4.gametrackerproject.model.User;
import com.et4.gametrackerproject.repository.GameRepository;
import com.et4.gametrackerproject.repository.GameTagRepository;
import com.et4.gametrackerproject.repository.TagRepository;
import com.et4.gametrackerproject.services.TagService;
import com.et4.gametrackerproject.validator.TagValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final GameRepository gameRepository;
    private final GameTagRepository gameTagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, GameRepository gameRepository, GameTagRepository gameTagRepository) {
        this.tagRepository = tagRepository;
        this.gameRepository = gameRepository;
        this.gameTagRepository = gameTagRepository;
    }

    @Override
    public TagDto createTag(TagDto tagDto) {
        List<String> errors = TagValidator.validate(tagDto);
        if (!errors.isEmpty()) {
            log.error("Tag is not valid {}", tagDto);
            throw new InvalidEntityException("Le tag n'est pas valide", ErrorCodes.TAG_NOT_VALID ,errors);
        }

        log.info("Create new tag {}", tagDto);

        return TagDto.fromEntity(
                tagRepository.save(
                        TagDto.toEntity(tagDto)
                )
        );
    }

    @Override
    public TagDto updateTag(Integer tagId, TagDto tagDto) {
        if(tagId == null) {
            log.error("Tag ID is null");
            throw new InvalidEntityException("ID de tag non valide");
        }

        List<String> errors = TagValidator.validate(tagDto);
        if (!errors.isEmpty()) {
            log.error("Tag is not valid {}", tagDto);
            throw new InvalidEntityException("Le tag n'est pas valide", ErrorCodes.TAG_NOT_VALID ,errors);
        }

        log.info("Update tag {}", tagDto);

        return TagDto.fromEntity(
                tagRepository.save(
                        TagDto.toEntity(tagDto)
                )
        );
    }

    @Override
    public void deleteTagById(Integer tagId) {
        if(tagId == null) {
            log.error("Tag ID is null");
            throw new InvalidEntityException("ID de tag non valide");
        }
        if(!tagRepository.existsById(tagId)) {
            log.error("Tag with ID {} not found", tagId);
            throw new EntityNotFoundException("Tag avec ID " + tagId + " n'existe pas", ErrorCodes.TAG_NOT_FOUND);
        }

        Optional<GameTag> gameTags = gameTagRepository.findByTagId(tagId);
        if (gameTags.isPresent()) {
            log.error("Impossible de supprimer le tag avec l'ID {} car il est référencé par un gameTag", tagId);
            throw new InvalidOperationException("Impossible de supprimer le tag car il est référencé par un gameTag",
                    ErrorCodes.TAG_ALREADY_USED);
        }

        tagRepository.deleteById(tagId);
    }

    @Override
    public TagDto getTagById(Integer tagId) {
        if(tagId == null) {
            log.error("Tag ID is null");
            throw new InvalidEntityException("ID de tag non valide", ErrorCodes.TAG_NOT_VALID);
        }

        log.info("Get tag with ID {}", tagId);

        return tagRepository.findById(tagId)
                .map(TagDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Tag avec ID " + tagId + " n'existe pas", ErrorCodes.TAG_NOT_FOUND));
    }

    @Override
    public TagDto getTagByName(String name) {
        if(name == null) {
            log.error("Tag name is null");
            throw new InvalidEntityException("Nom de tag non valide", ErrorCodes.TAG_NOT_VALID);
        }

        log.info("Get tag with name {}", name);

        return tagRepository.findByName(name)
                .map(TagDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Tag avec nom " + name + " n'existe pas", ErrorCodes.TAG_NOT_FOUND));
    }

    @Override
    public Page<TagDto> getAllTags(Pageable pageable) {
        log.info("Get all tags");
        return tagRepository.findAll(pageable)
                .map(TagDto::fromEntity);
    }

    @Override
    public List<TagDto> getTagsForGame(Integer gameId) {
        if(gameId == null) {
            log.error("Game ID is null");
            throw new InvalidEntityException("ID de jeu non valide", ErrorCodes.GAME_NOT_VALID);
        }

        log.info("Get tags for game with ID {}", gameId);

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Jeu avec ID " + gameId + " n'existe pas", ErrorCodes.GAME_NOT_FOUND));

        return tagRepository.findTagsByGame(game)
                .stream()
                .map(TagDto::fromEntity)
                .toList();
    }

    @Override
    public Page<TagDto> searchTags(String query, Pageable pageable) {
        if(query == null) {
            log.error("Query is null");
            throw new InvalidEntityException("Requête non valide", ErrorCodes.TAG_NOT_VALID);
        }

        log.info("Search tags with query {}", query);

        return tagRepository.findByNameContainingIgnoreCase(query, pageable)
                .map(TagDto::fromEntity);
    }
}
