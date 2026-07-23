package com.ringstory.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ringstory.notification.entity.NotificationSettingEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知偏好设置 Mapper
 */
@Mapper
public interface NotificationSettingMapper extends BaseMapper<NotificationSettingEntity> {
}
