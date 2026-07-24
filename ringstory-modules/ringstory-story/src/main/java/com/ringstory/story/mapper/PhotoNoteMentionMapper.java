package com.ringstory.story.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ringstory.story.entity.PhotoNoteMentionEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 备注@提及用户 Mapper
 */
@Mapper
public interface PhotoNoteMentionMapper extends BaseMapper<PhotoNoteMentionEntity> {
}
