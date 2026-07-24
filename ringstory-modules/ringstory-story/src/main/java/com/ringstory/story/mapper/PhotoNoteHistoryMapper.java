package com.ringstory.story.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ringstory.story.entity.PhotoNoteHistoryEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 备注版本历史 Mapper
 */
@Mapper
public interface PhotoNoteHistoryMapper extends BaseMapper<PhotoNoteHistoryEntity> {
}
