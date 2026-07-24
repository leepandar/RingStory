package com.ringstory.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ringstory.common.annotation.AuditLog;
import com.ringstory.common.entity.AuditLogEntity;
import com.ringstory.common.mapper.AuditLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 审计日志 AOP 切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogMapper auditLogMapper;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Around("@annotation(com.ringstory.common.annotation.AuditLog)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object result = point.proceed();

        try {
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            AuditLog annotation = method.getAnnotation(AuditLog.class);

            AuditLogEntity entity = new AuditLogEntity();
            entity.setAction(annotation.action());
            entity.setTargetTable(annotation.targetTable());
            entity.setCreateTime(LocalDateTime.now());

            // 尝试从参数中提取 ID
            Object[] args = point.getArgs();
            String[] paramNames = signature.getParameterNames();
            if (paramNames != null) {
                for (int i = 0; i < paramNames.length; i++) {
                    if ("id".equalsIgnoreCase(paramNames[i]) || "photoId".equalsIgnoreCase(paramNames[i])
                            || "memberId".equalsIgnoreCase(paramNames[i])) {
                        entity.setTargetId(String.valueOf(args[i]));
                    }
                    if ("familyId".equalsIgnoreCase(paramNames[i])) {
                        entity.setFamilyId((Long) args[i]);
                    }
                }
            }

            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes)
                    RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                entity.setIpAddress(request.getRemoteAddr());
                entity.setUserAgent(request.getHeader("User-Agent"));
            }

            // 记录参数（简化）
            if (args.length > 0) {
                entity.setNewValue(objectMapper.writeValueAsString(args));
            }

            auditLogMapper.insert(entity);
        } catch (Exception e) {
            log.warn("审计日志记录失败: {}", e.getMessage());
        }

        return result;
    }
}
