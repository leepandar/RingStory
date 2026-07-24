package com.ringstory.album.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 人脸检测 Feign 客户端
 * 调用 Rust face-svc 的人脸检测 API
 */
@FeignClient(name = "face-svc", url = "${face-svc.url:http://localhost:9090}")
public interface FaceFeignClient {

    /**
     * 检测照片中的人脸
     *
     * @param imageUrl 照片 URL
     * @return 检测结果：包含人脸坐标、特征向量等
     */
    @PostMapping("/api/face/detect")
    Map<String, Object> detectFace(@RequestParam("imageUrl") String imageUrl);

    /**
     * 批量检测人脸
     *
     * @param imageUrls 照片 URL 列表
     * @return 批量检测结果
     */
    @PostMapping("/api/face/detect-batch")
    List<Map<String, Object>> detectFaceBatch(@RequestParam("imageUrls") List<String> imageUrls);
}
