package com.ringstory.story.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ringstory.story.entity.PhotoNoteEntity;

/**
 * 故事服务接口
 */
public interface StoryService extends IService<PhotoNoteEntity> {

    /**
     * 根据照片ID获取笔记
     */
    PhotoNoteEntity getNoteByPhotoId(Long photoId);

    /**
     * 创建或更新照片笔记
     */
    PhotoNoteEntity createOrUpdateNote(Long photoId, Long authorId, String content,
                                        String locationName, String mentionedUsers);
}
