package com.ringstory.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ringstory.notification.entity.NotificationEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知 Mapper
 */
@Mapper
public interface NotificationMapper extends BaseMapper<NotificationEntity> {
}
