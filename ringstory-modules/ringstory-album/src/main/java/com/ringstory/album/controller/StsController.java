package com.ringstory.album.controller;

import com.ringstory.common.response.R;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * OSS STS 临时凭证控制器
 * <p>
 * 前端直传 OSS 时需要先获取 STS 临时凭证。
 * 当前为 Mock 实现，返回模拟的 AccessKeyId/Secret/Token。
 * 生产环境应对接阿里云 STS AssumeRole API。
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/api/album")
public class StsController {

    @Value("${aliyun.oss.bucket:ringstory-photos}")
    private String bucket;

    @Value("${aliyun.oss.endpoint:oss-cn-hangzhou.aliyuncs.com}")
    private String endpoint;

    /**
     * 获取 OSS STS 临时凭证
     *
     * @param familyId 家庭ID（用于生成上传目录前缀）
     * @return STS 凭证信息
     */
    @GetMapping("/sts/credential")
    public R<StsCredentialVO> getStsCredential(@RequestParam Long familyId) {
        log.info("获取 STS 凭证: familyId={}", familyId);

        // TODO: 生产环境替换为阿里云 STS AssumeRole API 调用
        // 当前为 Mock 实现
        StsCredentialVO vo = new StsCredentialVO();
        vo.setAccessKeyId("MOCK_ACCESS_KEY_" + UUID.randomUUID().toString().substring(0, 8));
        vo.setAccessKeySecret("MOCK_SECRET_KEY_" + UUID.randomUUID().toString().substring(0, 8));
        vo.setSecurityToken("MOCK_STS_TOKEN_" + UUID.randomUUID());
        vo.setExpiration(LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        vo.setBucket(bucket);
        vo.setRegion(endpoint);
        // 上传目录前缀：{familyId}/{date}/
        vo.setDir(String.format("%d/%s/", familyId,
                LocalDateTime.now().toLocalDate().toString()));

        return R.success(vo);
    }

    @Data
    public static class StsCredentialVO {
        private String accessKeyId;
        private String accessKeySecret;
        private String securityToken;
        private String expiration;
        private String bucket;
        private String region;
        private String dir;
    }
}
