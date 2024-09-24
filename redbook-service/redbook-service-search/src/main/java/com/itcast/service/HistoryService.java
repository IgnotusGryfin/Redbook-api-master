package com.itcast.service;

import com.itcast.result.Result;
import com.itcast.search.pojo.History;

import java.util.List;

public interface HistoryService {
    Result<List<History>> getHistoryList();

    Result<Void> deleteHistory();
}
