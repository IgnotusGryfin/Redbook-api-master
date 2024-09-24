package com.itcast.service;

import com.itcast.result.Result;

public interface LikeService {
    Result<Void> like(Integer noteId);

    Result<Boolean> isLike(Integer noteId);
}
