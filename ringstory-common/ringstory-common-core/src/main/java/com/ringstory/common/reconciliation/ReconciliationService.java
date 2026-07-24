package com.ringstory.common.reconciliation;

/**
 * 数据一致性对账服务接口
 * 定时任务：每小时对账 like_count, comment_count, photo_count, member_count
 * 关键业务逻辑避免依赖冗余字段，改为实时 COUNT
 */
public interface ReconciliationService {

    /**
     * 对账单个家庭的所有冗余统计字段
     * 包括：t_photo.like_count, t_family.photo_count, t_family.member_count
     */
    void reconcileFamily(Long familyId);

    /**
     * 对账所有家庭的冗余统计字段（定时任务调用）
     */
    void reconcileAll();

    /**
     * 对账单张照片的点赞数和评论数
     */
    void reconcilePhotoCounts(Long photoId);
}
