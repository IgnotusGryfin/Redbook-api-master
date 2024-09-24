package com.itcast.service;

import com.itcast.note.pojo.Topic;
import com.itcast.result.Result;

import java.util.List;

public interface TopicService {
    Result<List<Topic>> getTopics();
}
