package com.ringstory.story.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ringstory.story.entity.PhotoNoteEntity;
import com.ringstory.story.entity.PhotoNoteHistoryEntity;

import java.util.List;

/**
 * 故事服务接口
 */
public interface StoryService extends IService<PhotoNoteEntity> {

    /**
     * 根据照片ID获取笔记
     */
    PhotoNoteEntity getNoteByPhotoId(Long photoId);

    /**
     * 创建或更新照片笔记（带乐观锁 + 版本历史）
     */
    PhotoNoteEntity createOrUpdateNote(Long photoId, Long authorId, String content,
                                       String locationName, String mentionedUsers);

    /**
     * 回滚到指定版本
     */
    PhotoNoteEntity rollbackNote(Long noteId, Integer targetVersion, Long operatorId);

    /**
     * 获取笔记的版本历史
     */
    List<PhotoNoteHistoryEntity> getNoteHistory(Long noteId);

    /**
     * 解析内容中的 @提及，提取 mentionedUsers JSON
     */
    String parseMentions(String content);
}
