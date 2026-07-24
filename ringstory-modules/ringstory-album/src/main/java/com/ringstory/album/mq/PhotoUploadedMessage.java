package com.ringstory.album.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 照片上传完成事件消息
 * <p>
 * Topic: photo_uploaded
 * 当照片上传到 OSS 并写入数据库后发送。
 * 下游消费者：缩略图生成、内容安全检测、人脸检测、年轮树更新、ES 同步
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoUploadedMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 照片ID */
    private Long photoId;

    /** 家庭ID */
    private Long familyId;

    /** 上传者ID */
    private Long uploaderId;

    /** OSS 存储键 */
    private String ossKey;

    /** 文件 MD5 */
    private String md5;

    /** 文件格式 */
    private String format;
}
