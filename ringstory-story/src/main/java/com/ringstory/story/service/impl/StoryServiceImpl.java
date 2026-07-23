package com.ringstory.story.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.story.entity.PhotoNoteEntity;
import com.ringstory.story.mapper.PhotoNoteMapper;
import com.ringstory.story.service.StoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 故事服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StoryServiceImpl extends ServiceImpl<PhotoNoteMapper, PhotoNoteEntity> implements StoryService {

    @Override
    public PhotoNoteEntity getNoteByPhotoId(Long photoId) {
        return lambdaQuery().eq(PhotoNoteEntity::getPhotoId, photoId).one();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PhotoNoteEntity createOrUpdateNote(Long photoId, Long authorId, String content,
                                               String locationName, String mentionedUsers) {
        PhotoNoteEntity existing = lambdaQuery()
                .eq(PhotoNoteEntity::getPhotoId, photoId).one();
        if (existing != null) {
            existing.setVersion(existing.getVersion() + 1);
            existing.setContent(content);
            existing.setLocationName(locationName);
            existing.setMentionedUsers(mentionedUsers);
            updateById(existing);
            return existing;
        }
        PhotoNoteEntity note = new PhotoNoteEntity();
        note.setPhotoId(photoId);
        note.setAuthorId(authorId);
        note.setContent(content);
        note.setLocationName(locationName);
        note.setMentionedUsers(mentionedUsers);
        save(note);
        return note;
    }
}
