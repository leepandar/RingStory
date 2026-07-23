package com.ringstory.search.service;

import com.ringstory.search.entity.PhotoDocument;

import java.util.List;

/**
 * 搜索服务接口
 */
public interface SearchService {

    /**
     * 搜索照片（基于 ES）
     */
    List<PhotoDocument> searchPhotos(Long familyId, String keyword,
                                     Long personId, String location,
                                     String dateFrom, String dateTo);

    /**
     * 同步照片备注到 ES
     */
    void syncPhotoNote(Long photoId, String noteContent);

    /**
     * 同步照片标签到 ES
     */
    void syncPhotoTags(Long photoId, List<String> tags);
}
