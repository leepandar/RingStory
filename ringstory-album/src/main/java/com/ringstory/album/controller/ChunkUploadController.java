package com.ringstory.album.controller;

import com.ringstory.common.response.R;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分片上传控制器
 * <p>
 * 支持大文件分片上传（每片 1MB），断点续传。
 * 流程：init → uploadPart（多次）→ complete
 * 当前为 Mock 实现，实际生产环境应对接 OSS Multipart Upload API。
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/api/album")
@RequiredArgsConstructor
public class ChunkUploadController {

    // 内存中暂存上传状态（生产环境应使用 Redis）
    private static final Map<String, ChunkUploadState> UPLOAD_STATES = new ConcurrentHashMap<>();

    /**
     * 初始化分片上传
     *
     * @param request 包含 familyId、fileName、totalChunks、fileSize
     * @return uploadId 用于后续分片上传
     */
    @PostMapping("/chunk/init")
    public R<ChunkInitVO> initUpload(@RequestBody ChunkInitRequest request) {
        String uploadId = UUID.randomUUID().toString().replace("-", "");

        ChunkUploadState state = new ChunkUploadState();
        state.uploadId = uploadId;
        state.familyId = request.getFamilyId();
        state.fileName = request.getFileName();
        state.totalChunks = request.getTotalChunks();
        state.fileSize = request.getFileSize();
        state.uploadedChunks = new boolean[request.getTotalChunks()];

        UPLOAD_STATES.put(uploadId, state);

        log.info("分片上传初始化: uploadId={}, fileName={}, totalChunks={}, fileSize={}",
                uploadId, request.getFileName(), request.getTotalChunks(), request.getFileSize());

        ChunkInitVO vo = new ChunkInitVO();
        vo.setUploadId(uploadId);
        vo.setTotalChunks(request.getTotalChunks());
        return R.success(vo);
    }

    /**
     * 上传单个分片
     *
     * @param uploadId   上传任务ID
     * @param chunkIndex 分片序号（从 0 开始）
     * @return 上传结果
     */
    @PostMapping("/chunk/upload-part")
    public R<Void> uploadPart(@RequestParam String uploadId,
                              @RequestParam int chunkIndex) {
        ChunkUploadState state = UPLOAD_STATES.get(uploadId);
        if (state == null) {
            return R.error("上传任务不存在或已过期");
        }
        if (chunkIndex < 0 || chunkIndex >= state.totalChunks) {
            return R.error("分片序号无效");
        }

        // TODO: 生产环境接收 MultipartFile 并上传到 OSS Multipart
        state.uploadedChunks[chunkIndex] = true;

        log.debug("分片上传: uploadId={}, chunkIndex={}/{}", uploadId, chunkIndex + 1, state.totalChunks);
        return R.success();
    }

    /**
     * 完成分片上传（合并分片）
     *
     * @param uploadId 上传任务ID
     * @return 最终 OSS 路径
     */
    @PostMapping("/chunk/complete")
    public R<ChunkCompleteVO> completeUpload(@RequestParam String uploadId) {
        ChunkUploadState state = UPLOAD_STATES.get(uploadId);
        if (state == null) {
            return R.error("上传任务不存在或已过期");
        }

        // 检查所有分片是否已上传
        for (int i = 0; i < state.uploadedChunks.length; i++) {
            if (!state.uploadedChunks[i]) {
                return R.error("分片 " + i + " 尚未上传");
            }
        }

        // TODO: 生产环境调用 OSS completeMultipartUpload

        UPLOAD_STATES.remove(uploadId);

        ChunkCompleteVO vo = new ChunkCompleteVO();
        vo.setOssKey(String.format("%d/%s/%s", state.familyId,
                java.time.LocalDate.now(), state.fileName));
        log.info("分片上传完成: uploadId={}, ossKey={}", uploadId, vo.getOssKey());

        return R.success(vo);
    }

    /**
     * 查询分片上传进度（断点续传用）
     */
    @GetMapping("/chunk/progress")
    public R<ChunkProgressVO> getProgress(@RequestParam String uploadId) {
        ChunkUploadState state = UPLOAD_STATES.get(uploadId);
        if (state == null) {
            return R.error("上传任务不存在或已过期");
        }

        ChunkProgressVO vo = new ChunkProgressVO();
        vo.setUploadId(uploadId);
        vo.setTotalChunks(state.totalChunks);
        int uploaded = 0;
        for (boolean b : state.uploadedChunks) {
            if (b) uploaded++;
        }
        vo.setUploadedChunks(uploaded);
        return R.success(vo);
    }

    // ==================== 内部类 ====================

    private static class ChunkUploadState {
        String uploadId;
        Long familyId;
        String fileName;
        int totalChunks;
        long fileSize;
        boolean[] uploadedChunks;
    }

    @Data
    public static class ChunkInitRequest {
        private Long familyId;
        private String fileName;
        private int totalChunks;
        private long fileSize;
    }

    @Data
    public static class ChunkInitVO {
        private String uploadId;
        private int totalChunks;
    }

    @Data
    public static class ChunkCompleteVO {
        private String ossKey;
    }

    @Data
    public static class ChunkProgressVO {
        private String uploadId;
        private int totalChunks;
        private int uploadedChunks;
    }
}
