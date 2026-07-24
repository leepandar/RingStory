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
     * 创建或更新照片笔记（带乐观锁 + 版本历史 + @用户解析）
     * @param mentionedUserIds @提及的用户ID列表（验证是否为家庭成员）
     */
    PhotoNoteEntity createOrUpdateNote(Long photoId, Long authorId, String content,
                                       String locationName, List<Long> mentionedUserIds);

    /**
     * 回滚到指定版本
     */
    PhotoNoteEntity rollbackNote(Long noteId, Integer targetVersion, Long operatorId);

    /**
     * 获取笔记的版本历史（分页）
     */
    List<PhotoNoteHistoryEntity> getNoteHistory(Long noteId, int page, int size);

    /**
     * 根据照片ID删除笔记及关联的历史版本和@提及记录
     */
    void deleteNoteByPhotoId(Long photoId);
}
