package com.et4.gametrackerproject.controller;

import com.et4.gametrackerproject.controller.api.GameCommentLikeApi;
import com.et4.gametrackerproject.dto.GameCommentLikeDto;
import com.et4.gametrackerproject.dto.UserDto;
import com.et4.gametrackerproject.services.GameCommentLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class GameCommentLikeController implements GameCommentLikeApi {

    @Autowired
    private GameCommentLikeService gameCommentLikeService;

    public GameCommentLikeController(GameCommentLikeService gameCommentLikeService) {
        this.gameCommentLikeService = gameCommentLikeService;
    }


    @Override
    public GameCommentLikeDto addCommentLike(Integer userId, Integer commentId) {
        return gameCommentLikeService.addCommentLike(userId, commentId);
    }

    @Override
    public void removeLike(Integer likeId) {
        gameCommentLikeService.deleteGameCommentLikeById(likeId);
    }

    @Override
    public GameCommentLikeDto getLikeById(Integer likeId) {
        return gameCommentLikeService.getLikeById(likeId);
    }

    @Override
    public Page<GameCommentLikeDto> getLikesForComment(Integer commentId, Pageable pageable) {
        return gameCommentLikeService.getLikesForComment(commentId, pageable);
    }

    @Override
    public Page<GameCommentLikeDto> getLikesByUser(Integer userId, Pageable pageable) {
        return gameCommentLikeService.getLikesByUser(userId, pageable);
    }

    @Override
    public Long getLikeCountForComment(Integer commentId) {
        return gameCommentLikeService.getLikeCountForComment(commentId);
    }

    @Override
    public Map<Integer, Long> getMostLikedComments(int limit) {
        return gameCommentLikeService.getMostLikedComments(limit);
    }

    @Override
    public Page<GameCommentLikeDto> getRecentLikes(Pageable pageable) {
        return gameCommentLikeService.getRecentLikes(pageable);
    }

    @Override
    public Page<GameCommentLikeDto> getAllLikes(Pageable pageable) {
        return gameCommentLikeService.getAllLikes(pageable);
    }

    @Override
    public List<UserDto> getUsersWhoLikedComment(Integer commentId) {
        return gameCommentLikeService.getUsersWhoLikedComment(commentId);
    }

    @Override
    public Long countLikesByUser(Integer userId) {
        return gameCommentLikeService.countLikesByUser(userId);
    }
}
