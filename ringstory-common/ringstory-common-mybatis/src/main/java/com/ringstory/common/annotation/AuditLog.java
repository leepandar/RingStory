package com.ringstory.common.annotation;

import java.lang.annotation.*;

/**
 * 审计日志注解
 * 标注在需要记录审计日志的方法上
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {

    /** 操作动作描述 */
    String action() default "";

    /** 目标表名 */
    String targetTable() default "";
}
