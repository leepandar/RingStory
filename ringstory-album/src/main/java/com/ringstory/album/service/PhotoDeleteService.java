package com.ringstory.album.service;

import java.util.List;
import java.util.Map;

/**
 * 照片删除服务接口（含级联规则）
 */
public interface PhotoDeleteService {

    /**
     * 批量删除照片（含级联清理）
     * 删除顺序：
     * 1. 检查照片是否在已生成回顾中（提示用户）
     * 2. 删除 t_photo_note + t_photo_note_history + t_photo_note_mention
     * 3. 删除 t_face_photo
     * 4. 删除 t_comment
     * 5. 删除 t_like
     * 6. 删除 t_photo_tag
     * 7. 删除 t_photo_album
     * 8. 软删除 t_photo 本身
     * 9. 发送事件（缓存失效、计数更新）
     *
     * @param photoIds 照片ID列表
     * @param userId   操作者ID（可为null，表示系统操作）
     * @return 删除结果摘要
     */
    Map<String, Object> batchDeletePhotos(List<Long> photoIds, Long userId);
}
