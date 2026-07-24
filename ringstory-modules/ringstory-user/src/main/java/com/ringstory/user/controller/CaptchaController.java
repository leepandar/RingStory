package com.ringstory.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.ringstory.common.captcha.SliderCaptchaService;
import com.ringstory.common.captcha.SliderCaptchaService.CaptchaResult;
import com.ringstory.common.log.AccessLog;
import com.ringstory.common.redis.RedisUtils;
import com.ringstory.common.response.R;
import com.ringstory.user.entity.UserEntity;
import com.ringstory.user.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * 验证码与管理后台登录控制器
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class CaptchaController {

    private final RedisUtils redisUtils;

    private final UserService userService;

    /** Redis Key 前缀 */
    private static final String CAPTCHA_KEY_PREFIX = "captcha:slider:";

    /** 验证码有效期（秒） */
    private static final long CAPTCHA_EXPIRE_SECONDS = 120;

    /** 滑块校验容差（像素） */
    private static final int SLIDE_TOLERANCE = 5;

    /**
     * 生成滑块验证码
     * <p>
     * 返回背景图（带镂空）和拼图块图（base64），
     * 同时将拼图 X 坐标存入 Redis。
     * </p>
     */
    @GetMapping("/captcha/generate")
    public R<CaptchaVO> generateCaptcha() {
        CaptchaResult result = SliderCaptchaService.generate();

        // 将 X 坐标存入 Redis，设置过期时间
        String redisKey = CAPTCHA_KEY_PREFIX + result.getCaptchaId();
        redisUtils.set(redisKey, String.valueOf(result.getX()), CAPTCHA_EXPIRE_SECONDS, TimeUnit.SECONDS);

        CaptchaVO vo = new CaptchaVO();
        vo.setCaptchaId(result.getCaptchaId());
        vo.setBackgroundImage(result.getBackgroundImage());
        vo.setPieceImage(result.getPieceImage());
        vo.setY(result.getY());

        return R.success(vo);
    }

    /**
     * 校验滑块位置（前端拖拽完成后调用）
     *
     * @param request captchaId + 用户滑动到的 X 坐标
     * @return 校验结果
     */
    @PostMapping("/captcha/verify")
    public R<CaptchaVerifyVO> verifyCaptcha(@RequestBody CaptchaVerifyRequest request) {
        String redisKey = CAPTCHA_KEY_PREFIX + request.getCaptchaId();
        String storedX = redisUtils.get(redisKey);

        if (storedX == null) {
            return R.error("验证码已过期，请刷新");
        }

        // 校验 X 坐标（容差范围内）
        int expectedX = Integer.parseInt(storedX);
        int deltaX = Math.abs(request.getX() - expectedX);

        CaptchaVerifyVO verifyVO = new CaptchaVerifyVO();
        if (deltaX <= SLIDE_TOLERANCE) {
            // 验证通过，删除 Redis 中的记录（一次性使用）
            redisUtils.delete(redisKey);
            // 生成一个验证通过的 token，存入 Redis 供登录使用
            String verifyToken = java.util.UUID.randomUUID().toString().replace("-", "");
            redisUtils.set("captcha:passed:" + verifyToken, "1", 60, TimeUnit.SECONDS);
            verifyVO.setSuccess(true);
            verifyVO.setVerifyToken(verifyToken);
        } else {
            verifyVO.setSuccess(false);
            verifyVO.setMessage("验证失败，请重试");
        }

        return R.success(verifyVO);
    }

    /**
     * 管理员登录（账号密码 + 滑块验证）
     */
    @AccessLog("管理员登录")
    @PostMapping("/admin-login")
    public R<AdminLoginVO> adminLogin(@RequestBody AdminLoginRequest request) {
        // 1. 校验滑块验证码 token
        String verifyKey = "captcha:passed:" + request.getCaptchaToken();
        String verifyValue = redisUtils.get(verifyKey);
        if (verifyValue == null) {
            return R.error("请先完成滑块验证");
        }

        // 2. 验证账号密码（此处简化，实际应对接数据库查询管理员表）
        UserEntity user = userService.findByUsername(request.getUsername());
        if (user == null) {
            return R.error("账号不存在");
        }

        // 3. 校验密码（实际应使用 BCrypt 等加密）
        if (!request.getPassword().equals(user.getPassword())) {
            return R.error("密码错误");
        }

        // 4. 登录成功，删除验证码 token
        redisUtils.delete(verifyKey);

        // 5. Sa-Token 登录
        StpUtil.login(user.getId());

        AdminLoginVO vo = new AdminLoginVO();
        vo.setToken(StpUtil.getTokenValue());
        vo.setUserId(user.getId());
        vo.setNickName(user.getNickName());

        return R.success(vo);
    }

    /**
     * 验证码生成响应 VO
     */
    @Data
    public static class CaptchaVO {
        private String captchaId;
        private String backgroundImage;
        private String pieceImage;
        private int y;
    }

    /**
     * 滑块校验请求
     */
    @Data
    public static class CaptchaVerifyRequest {
        private String captchaId;
        private int x;
    }

    /**
     * 滑块校验响应
     */
    @Data
    public static class CaptchaVerifyVO {
        private boolean success;
        private String verifyToken;
        private String message;
    }

    /**
     * 管理员登录请求
     */
    @Data
    public static class AdminLoginRequest {
        private String username;
        private String password;
        private String captchaToken;
    }

    /**
     * 管理员登录响应
     */
    @Data
    public static class AdminLoginVO {
        private String token;
        private Long userId;
        private String nickName;
    }
}
