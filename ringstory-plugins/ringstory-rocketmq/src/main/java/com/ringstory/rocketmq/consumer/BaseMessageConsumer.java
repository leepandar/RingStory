package com.ringstory.rocketmq.consumer;

import lombok.extern.slf4j.Slf4j;

/**
 * RocketMQ 消费者基础抽象类
 * <p>
 * 业务模块的消费者可继承本类，获得统一的日志、异常处理和重试逻辑。
 * <p>
 * 使用示例：
 * <pre>
 * &#64;Component
 * public class OrderMessageConsumer extends BaseMessageConsumer&lt;OrderMessage&gt; {
 *
 *     &#64;Override
 *     protected void handleMessage(OrderMessage message) {
 *         // 业务处理逻辑
 *     }
 *
 *     &#64;Override
 *     protected String getConsumerName() {
 *         return "OrderMessageConsumer";
 *     }
 * }
 * </pre>
 *
 * @param <T> 消息体类型
 */
@Slf4j
public abstract class BaseMessageConsumer<T> {

    /**
     * 消息处理入口（由子类实现具体业务逻辑）
     *
     * @param message 反序列化后的消息对象
     */
    protected abstract void handleMessage(T message);

    /**
     * 消费者名称（用于日志标识）
     */
    protected abstract String getConsumerName();

    /**
     * 统一消费入口，封装异常处理和日志
     *
     * @param message 原始消息
     * @return true=消费成功，false=消费失败（触发重试）
     */
    public boolean onMessage(T message) {
        String name = getConsumerName();
        try {
            log.debug("[{}] 收到消息: {}", name, message);
            handleMessage(message);
            log.debug("[{}] 消息处理成功", name);
            return true;
        } catch (Exception e) {
            log.error("[{}] 消息处理失败 | error={} | message={}",
                    name, e.getMessage(), message, e);
            return false;
        }
    }
}
