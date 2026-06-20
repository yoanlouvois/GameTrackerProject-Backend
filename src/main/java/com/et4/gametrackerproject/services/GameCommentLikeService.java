package com.et4.gametrackerproject.services;

import com.et4.gametrackerproject.dto.GameCommentLikeDto;
import com.et4.gametrackerproject.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface GameCommentLikeService {

    //AJOUT/RETIRER
    GameCommentLikeDto addCommentLike(Integer userId, Integer commentId);
    void deleteGameCommentLikeById(Integer likeId);



    // GETTER
    GameCommentLikeDto getLikeById(Integer likeId);
    Page<GameCommentLikeDto> getLikesForComment(Integer commentId, Pageable pageable);
    Page<GameCommentLikeDto> getLikesByUser(Integer userId, Pageable pageable);
    Long getLikeCountForComment(Integer commentId);
    Map<Integer, Long> getMostLikedComments(int limit);
    Page<GameCommentLikeDto> getRecentLikes(Pageable pageable);
    Page<GameCommentLikeDto> getAllLikes(Pageable pageable);
    List<UserDto> getUsersWhoLikedComment(Integer commentId);

    Long countLikesByUser(Integer userId);


}