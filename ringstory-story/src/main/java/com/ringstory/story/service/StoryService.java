package com.ringstory.story.service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.story.entity.PhotoNoteEntity;
import com.ringstory.story.mapper.PhotoNoteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StoryService extends ServiceImpl<PhotoNoteMapper, PhotoNoteEntity> {

    @Transactional
    public PhotoNoteEntity createOrUpdateNote(Long photoId, Long authorId, String content,
                                              String locationName, String mentionedUsers) {
        PhotoNoteEntity existing = lambdaQuery().eq(PhotoNoteEntity::getPhotoId, photoId).one();
        if (existing != null) {
            existing.setVersion(existing.getVersion() + 1);
            existing.setContent(content);
            existing.setLocationName(locationName);
            existing.setMentionedUsers(mentionedUsers);
            updateById(existing);
            return existing;
        }
        PhotoNoteEntity note = new PhotoNoteEntity();
        note.setPhotoId(photoId); note.setAuthorId(authorId); note.setContent(content);
        note.setLocationName(locationName); note.setMentionedUsers(mentionedUsers);
        note.setVersion(1);
        save(note);
        return note;
    }
}
