package com.ringstory.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ringstory.album.entity.CommentEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论 Mapper
 */
@Mapper
public interface CommentMapper extends BaseMapper<CommentEntity> {
}
