package com.ringstory.search.service;

import java.util.List;
import java.util.Map;

/**
 * 搜索服务接口
 */
public interface SearchService {

    /**
     * 搜索照片
     */
    List<Map<String, Object>> searchPhotos(Long familyId, String keyword,
                                            Long personId, String location,
                                            String dateFrom, String dateTo);
}
