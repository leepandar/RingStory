package com.ringstory.search.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexOperations;
import org.springframework.stereotype.Service;

/**
 * ES 索引管理服务
 */
@Slf4j
@Service
public class EsIndexService {

    private final ElasticsearchOperations elasticsearchOperations;

    public EsIndexService(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    /**
     * 创建索引（如果不存在）
     */
    public void createIndexIfNotExists() {
        try {
            IndexOperations indexOps = elasticsearchOperations.indexOps(
                    com.ringstory.search.entity.PhotoDocument.class);
            if (!indexOps.exists()) {
                indexOps.create();
                indexOps.putMapping(indexOps.createMapping());
                log.info("ES 索引 ringstory_photos 创建成功");
            } else {
                log.info("ES 索引 ringstory_photos 已存在");
            }
        } catch (Exception e) {
            log.error("创建 ES 索引失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 删除索引（慎用）
     */
    public void deleteIndex() {
        try {
            IndexOperations indexOps = elasticsearchOperations.indexOps(
                    com.ringstory.search.entity.PhotoDocument.class);
            if (indexOps.exists()) {
                indexOps.delete();
                log.info("ES 索引 ringstory_photos 已删除");
            }
        } catch (Exception e) {
            log.error("删除 ES 索引失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 重建索引
     */
    public void recreateIndex() {
        deleteIndex();
        createIndexIfNotExists();
    }
}
