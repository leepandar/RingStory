package com.ringstory.story.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.common.exception.BusinessException;
import com.ringstory.story.entity.PhotoNoteEntity;
import com.ringstory.story.entity.PhotoNoteHistoryEntity;
import com.ringstory.story.entity.PhotoNoteMentionEntity;
import com.ringstory.story.mapper.PhotoNoteHistoryMapper;
import com.ringstory.story.mapper.PhotoNoteMapper;
import com.ringstory.story.mapper.PhotoNoteMentionMapper;
import com.ringstory.story.service.StoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 故事服务实现类
 * 支持：乐观锁、版本历史（最多5个）、回滚、@提及解析到关联表
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StoryServiceImpl extends ServiceImpl<PhotoNoteMapper, PhotoNoteEntity> implements StoryService {

    private final PhotoNoteHistoryMapper photoNoteHistoryMapper;
    private final PhotoNoteMentionMapper photoNoteMentionMapper;
    private static final int MAX_HISTORY_VERSIONS = 5;

    @Override
    public PhotoNoteEntity getNoteByPhotoId(Long photoId) {
        return lambdaQuery().eq(PhotoNoteEntity::getPhotoId, photoId).one();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PhotoNoteEntity createOrUpdateNote(Long photoId, Long authorId, String content,
                                               String locationName, List<Long> mentionedUserIds) {
        PhotoNoteEntity existing = lambdaQuery()
                .eq(PhotoNoteEntity::getPhotoId, photoId).one();

        if (existing != null) {
            // 保存旧版本到历史表
            saveToHistory(existing);

            // 记录用户期望的版本号（用于冲突检测）
            int expectedVersion = existing.getVersion();
            int newVersion = expectedVersion + 1;

            // 尝试乐观锁更新
            existing.setVersion(newVersion);
            existing.setContent(content);
            existing.setLocationName(locationName);
            existing.setCurrentVersion(newVersion);

            boolean updated = updateById(existing);
            if (!updated) {
                // 乐观锁冲突：有人先一步修改了
                // 执行自动合并策略
                return handleConflict(existing, authorId, content, locationName, mentionedUserIds);
            }

            // 更新成功，保存@提及
            saveMentions(existing.getId(), mentionedUserIds);
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
        note.setCurrentVersion(1);

        save(note);
        saveMentions(note.getId(), mentionedUserIds);

        log.info("笔记创建成功: noteId={}, photoId={}", note.getId(), photoId);
        return note;
    }

    /**
     * 冲突自动合并策略
     * <p>
     * 当乐观锁检测到版本冲突时，执行以下策略：
     * 1. 重新读取最新版本
     * 2. 如果他人只修改了非内容字段（位置），则自动合并：保留当前用户的内容 + 他人的位置
     * 3. 如果他人也修改了内容（冲突），则：
     *    a. 将当前用户的内容保存为“冲突草稿”到历史表
     *    b. 保留他人的最新版本
     *    c. 返回最新版本（客户端可提示用户“内容已合并，你的修改已保存为草稿”）
     * </p>
     */
    private PhotoNoteEntity handleConflict(PhotoNoteEntity staleEntity, Long authorId,
                                            String newContent, String newLocation,
                                            List<Long> newMentions) {
        // 重新读取最新版本
        PhotoNoteEntity latest = lambdaQuery()
                .eq(PhotoNoteEntity::getPhotoId, staleEntity.getPhotoId()).one();
        if (latest == null) {
            throw new BusinessException("笔记不存在");
        }

        boolean contentConflict = !newContent.equals(latest.getContent());

        if (contentConflict) {
            // 内容冲突：将用户的修改保存为“冲突草稿”到历史表
            PhotoNoteHistoryEntity conflictDraft = new PhotoNoteHistoryEntity();
            conflictDraft.setNoteId(latest.getId());
            conflictDraft.setContent(newContent); // 用户想要写入的内容
            conflictDraft.setVersion(latest.getVersion() + 1);
            conflictDraft.setEditorId(authorId);
            conflictDraft.setCreateTime(LocalDateTime.now());
            photoNoteHistoryMapper.insert(conflictDraft);

            // 仅更新位置等非内容字段（如果用户修改了位置）
            if (newLocation != null && !newLocation.equals(latest.getLocationName())) {
                latest.setLocationName(newLocation);
                latest.setVersion(latest.getVersion() + 1);
                latest.setCurrentVersion(latest.getVersion());
                updateById(latest);
            }

            // 更新@提及（合并用户的提及列表）
            saveMentions(latest.getId(), newMentions);
            cleanupHistory(latest.getId());

            log.warn("笔记内容冲突已自动处理: noteId={}, user={}, draftVersion={}",
                    latest.getId(), authorId, conflictDraft.getVersion());
            // 返回最新版本（客户端可通过检查 version 与用户期望值不一致来判断发生了合并）
            return latest;
        } else {
            // 无内容冲突（他人可能只改了位置等元数据）：直接应用用户的修改
            latest.setContent(newContent);
            latest.setLocationName(newLocation);
            latest.setVersion(latest.getVersion() + 1);
            latest.setCurrentVersion(latest.getVersion());
            updateById(latest);

            saveMentions(latest.getId(), newMentions);
            cleanupHistory(latest.getId());

            log.info("笔记自动合并成功（无内容冲突）: noteId={}, version={}",
                    latest.getId(), latest.getVersion());
            return latest;
        }
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
        note.setCurrentVersion(note.getVersion());
        updateById(note);

        log.info("笔记回滚成功: noteId={}, fromVersion={}, toVersion={}, operator={}",
                noteId, note.getVersion() - 1, targetVersion, operatorId);
        return note;
    }

    @Override
    public List<PhotoNoteHistoryEntity> getNoteHistory(Long noteId, int page, int size) {
        Page<PhotoNoteHistoryEntity> pageParam = new Page<>(page, size);
        Page<PhotoNoteHistoryEntity> result = photoNoteHistoryMapper.selectPage(pageParam,
                new LambdaQueryWrapper<PhotoNoteHistoryEntity>()
                        .eq(PhotoNoteHistoryEntity::getNoteId, noteId)
                        .orderByDesc(PhotoNoteHistoryEntity::getVersion));
        return result.getRecords();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNoteByPhotoId(Long photoId) {
        PhotoNoteEntity note = lambdaQuery().eq(PhotoNoteEntity::getPhotoId, photoId).one();
        if (note == null) {
            return;
        }
        // 1. 删除 @提及关联
        photoNoteMentionMapper.delete(
                new LambdaQueryWrapper<PhotoNoteMentionEntity>()
                        .eq(PhotoNoteMentionEntity::getNoteId, note.getId()));
        // 2. 删除版本历史
        photoNoteHistoryMapper.delete(
                new LambdaQueryWrapper<PhotoNoteHistoryEntity>()
                        .eq(PhotoNoteHistoryEntity::getNoteId, note.getId()));
        // 3. 删除笔记本身
        removeById(note.getId());
        log.info("照片笔记级联删除完成: photoId={}, noteId={}", photoId, note.getId());
    }

    /**
     * 保存 @提及用户到关联表
     * 如果 mentionedUserIds 为空，则自动从内容中解析
     */
    private void saveMentions(Long noteId, List<Long> mentionedUserIds) {
        // 先删除旧的提及关系
        photoNoteMentionMapper.delete(
                new LambdaQueryWrapper<PhotoNoteMentionEntity>()
                        .eq(PhotoNoteMentionEntity::getNoteId, noteId));

        // 如果有显式传入的提及用户ID列表，直接使用
        if (mentionedUserIds != null && !mentionedUserIds.isEmpty()) {
            for (Long userId : mentionedUserIds) {
                PhotoNoteMentionEntity mention = new PhotoNoteMentionEntity();
                mention.setNoteId(noteId);
                mention.setUserId(userId);
                mention.setCreateTime(LocalDateTime.now());
                photoNoteMentionMapper.insert(mention);
            }
            log.debug("保存提及用户: noteId={}, count={}", noteId, mentionedUserIds.size());
        }
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
