package com.ringstory.story.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.common.exception.BusinessException;
import com.ringstory.story.entity.PhotoNoteEntity;
import com.ringstory.story.entity.PhotoNoteHistoryEntity;
import com.ringstory.story.mapper.PhotoNoteHistoryMapper;
import com.ringstory.story.mapper.PhotoNoteMapper;
import com.ringstory.story.service.StoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 故事服务实现类
 * 支持：乐观锁、版本历史（最多5个）、回滚、@提及解析
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StoryServiceImpl extends ServiceImpl<PhotoNoteMapper, PhotoNoteEntity> implements StoryService {

    private final PhotoNoteHistoryMapper photoNoteHistoryMapper;
    private static final int MAX_HISTORY_VERSIONS = 5;
    private static final Pattern MENTION_PATTERN = Pattern.compile("@(\\w+)");

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
            // 更新现有笔记 —— 乐观锁机制
            // 保存旧版本到历史表
            saveToHistory(existing);

            // 使用 version 进行乐观锁更新
            int newVersion = existing.getVersion() + 1;
            existing.setVersion(newVersion);
            existing.setContent(content);
            existing.setLocationName(locationName);

            // 自动解析 @提及
            if (mentionedUsers == null || mentionedUsers.isBlank()) {
                existing.setMentionedUsers(parseMentions(content));
            } else {
                existing.setMentionedUsers(mentionedUsers);
            }

            // 乐观锁更新（MyBatis-Plus @Version 自动处理）
            boolean updated = updateById(existing);
            if (!updated) {
                throw new BusinessException("备注已被他人修改，请刷新后重试");
            }

            // 清理超出限制的历史版本（保留最新 MAX_HISTORY_VERSIONS 个）
            cleanupHistory(existing.getId());

            log.info("笔记更新成功: noteId={}, version={}, photoId={}", existing.getId(), newVersion, photoId);
            return existing;
        }

        // 创建新笔记
        PhotoNoteEntity note = new PhotoNoteEntity();
        note.setPhotoId(photoId);
        note.setAuthorId(authorId);
        note.setContent(content);
        note.setLocationName(locationName);

        // 自动解析 @提及
        if (mentionedUsers == null || mentionedUsers.isBlank()) {
            note.setMentionedUsers(parseMentions(content));
        } else {
            note.setMentionedUsers(mentionedUsers);
        }

        save(note);
        log.info("笔记创建成功: noteId={}, photoId={}", note.getId(), photoId);
        return note;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PhotoNoteEntity rollbackNote(Long noteId, Integer targetVersion, Long operatorId) {
        // 查找目标历史版本
        PhotoNoteHistoryEntity history = photoNoteHistoryMapper.selectOne(
                new LambdaQueryWrapper<PhotoNoteHistoryEntity>()
                        .eq(PhotoNoteHistoryEntity::getNoteId, noteId)
                        .eq(PhotoNoteHistoryEntity::getVersion, targetVersion));

        if (history == null) {
            throw new BusinessException("未找到版本 " + targetVersion + " 的历史记录");
        }

        PhotoNoteEntity note = getById(noteId);
        if (note == null) {
            throw new BusinessException("笔记不存在");
        }

        // 保存当前版本到历史
        saveToHistory(note);

        // 回滚到目标版本的内容
        note.setContent(history.getContent());
        note.setVersion(note.getVersion() + 1);
        updateById(note);

        log.info("笔记回滚成功: noteId={}, fromVersion={}, toVersion={}, operator={}",
                noteId, note.getVersion() - 1, targetVersion, operatorId);
        return note;
    }

    @Override
    public List<PhotoNoteHistoryEntity> getNoteHistory(Long noteId) {
        return photoNoteHistoryMapper.selectList(
                new LambdaQueryWrapper<PhotoNoteHistoryEntity>()
                        .eq(PhotoNoteHistoryEntity::getNoteId, noteId)
                        .orderByDesc(PhotoNoteHistoryEntity::getVersion));
    }

    @Override
    public String parseMentions(String content) {
        if (content == null || content.isBlank()) {
            return "[]";
        }
        Matcher matcher = MENTION_PATTERN.matcher(content);
        List<String> mentions = matcher.results()
                .map(m -> m.group(1))
                .distinct()
                .collect(Collectors.toList());

        if (mentions.isEmpty()) {
            return "[]";
        }
        // 返回 JSON 数组格式
        return mentions.stream()
                .map(m -> "\"" + m + "\"")
                .collect(Collectors.joining(",", "[", "]"));
    }

    /**
     * 保存旧版本到历史表
     */
    private void saveToHistory(PhotoNoteEntity note) {
        PhotoNoteHistoryEntity history = new PhotoNoteHistoryEntity();
        history.setNoteId(note.getId());
        history.setContent(note.getContent());
        history.setVersion(note.getVersion());
        history.setEditorId(note.getAuthorId());
        history.setCreateTime(LocalDateTime.now());
        photoNoteHistoryMapper.insert(history);
    }

    /**
     * 清理超出限制的历史版本（保留最新 MAX_HISTORY_VERSIONS 个）
     */
    private void cleanupHistory(Long noteId) {
        List<PhotoNoteHistoryEntity> allHistory = photoNoteHistoryMapper.selectList(
                new LambdaQueryWrapper<PhotoNoteHistoryEntity>()
                        .eq(PhotoNoteHistoryEntity::getNoteId, noteId)
                        .orderByDesc(PhotoNoteHistoryEntity::getVersion));

        if (allHistory.size() > MAX_HISTORY_VERSIONS) {
            // 删除最旧的版本
            List<PhotoNoteHistoryEntity> toDelete = allHistory.subList(MAX_HISTORY_VERSIONS, allHistory.size());
            for (PhotoNoteHistoryEntity h : toDelete) {
                photoNoteHistoryMapper.deleteById(h.getId());
            }
            log.debug("清理历史版本: noteId={}, deleted={}", noteId, toDelete.size());
        }
    }
}
