package com.ringstory.album.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ringstory.album.entity.PhotoTagEntity;
import com.ringstory.album.entity.TagEntity;

import java.util.List;

/**
 * 标签服务接口
 */
public interface TagService extends IService<TagEntity> {

    /**
     * 创建标签（家庭级别）
     */
    TagEntity createTag(Long familyId, String name, Long createdBy);

    /**
     * 获取家庭所有标签
     */
    List<TagEntity> listByFamilyId(Long familyId);

    /**
     * 删除标签（同时删除关联）
     */
    void deleteTag(Long tagId);

    /**
     * 为照片批量添加标签
     */
    void addTagsToPhotos(Long photoId, List<Long> tagIds);

    /**
     * 移除照片的标签
     */
    void removeTagFromPhoto(Long photoId, Long tagId);

    /**
     * 获取照片的所有标签
     */
    List<TagEntity> getPhotoTags(Long photoId);

    /**
     * 获取标签下的所有照片ID
     */
    List<Long> getPhotoIdsByTag(Long tagId);

    /**
     * 获取高频标签（推荐）
     */
    List<TagEntity> getHotTags(Long familyId, int limit);
}
