package com.ringstory.album.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.album.entity.PhotoTagEntity;
import com.ringstory.album.entity.TagEntity;
import com.ringstory.album.mapper.PhotoTagMapper;
import com.ringstory.album.mapper.TagMapper;
import com.ringstory.album.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl extends ServiceImpl<TagMapper, TagEntity> implements TagService {

    private final PhotoTagMapper photoTagMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TagEntity createTag(Long familyId, String name, Long createdBy) {
        // 检查是否已存在同名标签
        TagEntity existing = lambdaQuery()
                .eq(TagEntity::getFamilyId, familyId)
                .eq(TagEntity::getName, name)
                .one();
        if (existing != null) {
            return existing;
        }

        TagEntity tag = new TagEntity();
        tag.setFamilyId(familyId);
        tag.setName(name);
        tag.setCreatedBy(createdBy);
        save(tag);
        return tag;
    }

    @Override
    public List<TagEntity> listByFamilyId(Long familyId) {
        return lambdaQuery()
                .eq(TagEntity::getFamilyId, familyId)
                .orderByDesc(TagEntity::getCreateTime)
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Long tagId) {
        removeById(tagId);
        // 同时删除关联关系
        photoTagMapper.delete(new LambdaQueryWrapper<PhotoTagEntity>()
                .eq(PhotoTagEntity::getTagId, tagId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTagsToPhotos(Long photoId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            // 检查是否已存在
            Long count = photoTagMapper.selectCount(new LambdaQueryWrapper<PhotoTagEntity>()
                    .eq(PhotoTagEntity::getPhotoId, photoId)
                    .eq(PhotoTagEntity::getTagId, tagId));
            if (count == 0) {
                PhotoTagEntity pt = new PhotoTagEntity();
                pt.setPhotoId(photoId);
                pt.setTagId(tagId);
                photoTagMapper.insert(pt);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeTagFromPhoto(Long photoId, Long tagId) {
        photoTagMapper.delete(new LambdaQueryWrapper<PhotoTagEntity>()
                .eq(PhotoTagEntity::getPhotoId, photoId)
                .eq(PhotoTagEntity::getTagId, tagId));
    }

    @Override
    public List<TagEntity> getPhotoTags(Long photoId) {
        List<PhotoTagEntity> photoTags = photoTagMapper.selectList(
                new LambdaQueryWrapper<PhotoTagEntity>()
                        .eq(PhotoTagEntity::getPhotoId, photoId));
        if (photoTags.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> tagIds = photoTags.stream()
                .map(PhotoTagEntity::getTagId)
                .collect(Collectors.toList());
        return listByIds(tagIds);
    }

    @Override
    public List<Long> getPhotoIdsByTag(Long tagId) {
        List<PhotoTagEntity> photoTags = photoTagMapper.selectList(
                new LambdaQueryWrapper<PhotoTagEntity>()
                        .eq(PhotoTagEntity::getTagId, tagId));
        return photoTags.stream()
                .map(PhotoTagEntity::getPhotoId)
                .collect(Collectors.toList());
    }

    @Override
    public List<TagEntity> getHotTags(Long familyId, int limit) {
        // 简化实现：返回家庭下最新的标签作为"高频"推荐
        // TODO: 生产环境应按 photo_tag 关联数量排序
        return lambdaQuery()
                .eq(TagEntity::getFamilyId, familyId)
                .orderByDesc(TagEntity::getCreateTime)
                .last("LIMIT " + limit)
                .list();
    }
}
