package com.ringstory.common.log;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class AccessLogAspect {

    @Around("@annotation(com.ringstory.common.log.AccessLog)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        AccessLog accessLog = method.getAnnotation(AccessLog.class);
        String description = accessLog.value();

        String requestUrl = "unknown";
        String httpMethod = "unknown";
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            requestUrl = request.getRequestURI();
            httpMethod = request.getMethod();
        }

        String className = point.getTarget().getClass().getSimpleName();
        String methodName = method.getName();

        log.info("[{}] 开始调用: {} {} | {}.{} | 参数: {}",
                description.isEmpty() ? "API" : description,
                httpMethod, requestUrl, className, methodName,
                getArgsString(point.getArgs()));

        Object result;
        try {
            result = point.proceed();
            long costTime = System.currentTimeMillis() - startTime;
            log.info("[{}] 调用成功: {} {} | 耗时: {}ms",
                    description.isEmpty() ? "API" : description,
                    httpMethod, requestUrl, costTime);
        } catch (Exception e) {
            long costTime = System.currentTimeMillis() - startTime;
            log.error("[{}] 调用异常: {} {} | 耗时: {}ms | 异常: {}",
                    description.isEmpty() ? "API" : description,
                    httpMethod, requestUrl, costTime, e.getMessage());
            throw e;
        }

        return result;
    }

    private String getArgsString(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(", ");
            Object arg = args[i];
            if (arg == null) {
                sb.append("null");
            } else if (arg instanceof HttpServletRequest) {
                sb.append("HttpServletRequest");
            } else if (arg instanceof String) {
                sb.append("\"").append(arg).append("\"");
            } else {
                sb.append(arg.getClass().getSimpleName()).append("@").append(System.identityHashCode(arg));
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
