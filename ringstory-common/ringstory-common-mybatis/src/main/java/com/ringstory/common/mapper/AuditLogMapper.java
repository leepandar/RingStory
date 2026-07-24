package com.ringstory.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ringstory.common.entity.AuditLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审计日志 Mapper
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLogEntity> {
}
