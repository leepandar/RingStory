package com.ringstory.rocketmq.producer;

import com.ringstory.rocketmq.config.RocketMQProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

/**
 * RocketMQ 生产者封装服务
 * <p>
 * 基于 Spring Cloud Stream 的 StreamBridge 发送消息，
 * 业务模块直接注入本类即可发送消息，无需关心底层 MQ 细节。
 *
 * <pre>
 * // 使用示例：
 * &#64;Autowired
 * private RocketMQProducerService producer;
 *
 * // 发送普通消息到指定 binding
 * producer.send("topic-binding-out", "Hello RingStory");
 *
 * // 发送带 Key 的消息
 * producer.sendWithKey("topic-binding-out", "order-123", payload);
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
public class RocketMQProducerService {

    private final StreamBridge streamBridge;
    private final RocketMQProperties properties;

    /**
     * 发送消息到指定 binding（对应 Spring Cloud Stream 的 output binding 名称）
     *
     * @param bindingName 输出 binding 名称，如 "topic-binding-out"
     * @param payload     消息体
     * @param <T>         消息体类型
     * @return 是否发送成功
     */
    public <T> boolean send(String bindingName, T payload) {
        return send(bindingName, null, payload);
    }

    /**
     * 发送带 Key 的消息到指定 binding
     *
     * @param bindingName 输出 binding 名称
     * @param key         消息 Key（用于 RocketMQ 查询和去重）
     * @param payload     消息体
     * @param <T>         消息体类型
     * @return 是否发送成功
     */
    public <T> boolean sendWithKey(String bindingName, String key, T payload) {
        return send(bindingName, key, payload);
    }

    /**
     * 发送消息（内部统一方法）
     *
     * @param bindingName 输出 binding 名称
     * @param key         消息 Key（可为 null）
     * @param payload     消息体
     * @param <T>         消息体类型
     * @return 是否发送成功
     */
    private <T> boolean send(String bindingName, String key, T payload) {
        try {
            MessageBuilder<T> builder = MessageBuilder.withPayload(payload);
            if (key != null && !key.isBlank()) {
                builder.setHeader("KEYS", key);
            }
            Message<T> message = builder.build();

            boolean result = streamBridge.send(bindingName, message);

            if (result) {
                log.debug("消息发送成功 | binding={} | key={} | payload={}",
                        bindingName, key, payload);
            } else {
                log.warn("消息发送失败 | binding={} | key={}", bindingName, key);
                if (properties.getProducer().isFailFast()) {
                    throw new RuntimeException("RocketMQ 消息发送失败: binding=" + bindingName);
                }
            }
            return result;
        } catch (Exception e) {
            log.error("消息发送异常 | binding={} | key={} | error={}",
                    bindingName, key, e.getMessage(), e);
            if (properties.getProducer().isFailFast()) {
                throw new RuntimeException("RocketMQ 消息发送异常", e);
            }
            return false;
        }
    }
}
