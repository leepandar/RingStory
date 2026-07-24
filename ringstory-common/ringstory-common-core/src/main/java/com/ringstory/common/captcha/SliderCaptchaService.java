package com.ringstory.common.captcha;

import lombok.Data;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * 滑块拼图验证码生成服务
 * <p>
 * 使用 Java AWT 动态生成背景图 + 拼图块，
 * 每次生成随机位置和随机背景，安全性高。
 * </p>
 */
public class SliderCaptchaService {

    /** 背景图宽度 */
    private static final int BG_WIDTH = 320;

    /** 背景图高度 */
    private static final int BG_HEIGHT = 160;

    /** 拼图块尺寸 */
    private static final int PIECE_SIZE = 44;

    /** 拼图凸起半径 */
    private static final int KNOB_RADIUS = 6;

    private static final Random RANDOM = new Random();

    private SliderCaptchaService() {
    }

    /**
     * 生成滑块验证码
     *
     * @return CaptchaResult 包含 base64 图片、Y 坐标、captchaId
     */
    public static CaptchaResult generate() {
        // 随机拼图块位置（保证不超出边界）
        int x = RANDOM.nextInt(BG_WIDTH - PIECE_SIZE * 2 - KNOB_RADIUS * 2) + PIECE_SIZE + KNOB_RADIUS;
        int y = RANDOM.nextInt(BG_HEIGHT - PIECE_SIZE - KNOB_RADIUS * 2) + KNOB_RADIUS + 5;

        // 生成随机彩色背景
        BufferedImage background = generateRandomBackground();

        // 构建拼图块形状
        Path2D pieceShape = buildPiecePath(0, 0, PIECE_SIZE, KNOB_RADIUS);

        // 1. 生成带镂空的背景图
        BufferedImage bgWithHole = new BufferedImage(BG_WIDTH, BG_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2dHole = bgWithHole.createGraphics();
        enableAntiAliasing(g2dHole);
        g2dHole.drawImage(background, 0, 0, null);

        // 在拼图位置挖洞：先填半透明灰色
        g2dHole.translate(x, y);
        Area holeArea = new Area(pieceShape);
        g2dHole.setComposite(AlphaComposite.SrcAtop);
        g2dHole.setColor(new Color(0, 0, 0, 120));
        g2dHole.fill(holeArea);
        // 描边
        g2dHole.setComposite(AlphaComposite.SrcOver);
        g2dHole.setColor(new Color(255, 255, 255, 80));
        g2dHole.setStroke(new BasicStroke(2f));
        g2dHole.draw(holeArea);
        g2dHole.dispose();

        // 2. 生成拼图块图片（从原始背景中抠出）
        int pieceImgW = PIECE_SIZE + KNOB_RADIUS * 2;
        int pieceImgH = PIECE_SIZE + KNOB_RADIUS * 2;
        BufferedImage pieceImg = new BufferedImage(pieceImgW, pieceImgH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2dPiece = pieceImg.createGraphics();
        enableAntiAliasing(g2dPiece);
        // 从原始背景中截取拼图区域
        g2dPiece.drawImage(background,
                -x + KNOB_RADIUS, -y + KNOB_RADIUS,
                BG_WIDTH - x + KNOB_RADIUS, BG_HEIGHT - y + KNOB_RADIUS,
                0, 0, BG_WIDTH, BG_HEIGHT,
                null);
        // 裁剪为拼图形状
        g2dPiece.setComposite(AlphaComposite.DstIn);
        g2dPiece.fill(new Area(pieceShape));
        // 描边
        g2dPiece.setComposite(AlphaComposite.SrcOver);
        g2dPiece.setColor(new Color(255, 255, 255, 160));
        g2dPiece.setStroke(new BasicStroke(2f));
        g2dPiece.draw(pieceShape);
        g2dPiece.dispose();

        // 转为 base64
        String bgBase64 = toBase64(bgWithHole);
        String pieceBase64 = toBase64(pieceImg);

        CaptchaResult result = new CaptchaResult();
        result.setBackgroundImage(bgBase64);
        result.setPieceImage(pieceBase64);
        result.setX(x);
        result.setY(y);
        result.setCaptchaId(IdUtil.fastUUID());

        return result;
    }

    /**
     * 构建拼图块路径（正方形 + 上下左右凸起/凹陷）
     */
    private static Path2D buildPiecePath(double offsetX, double offsetY, int size, int knobR) {
        Path2D path = new Path2D.Double();
        double s = size;
        double r = knobR;

        // 起点：左上角
        path.moveTo(offsetX, offsetY);

        // 上边：凸起
        path.lineTo(offsetX + s * 0.36, offsetY);
        path.quadTo(offsetX + s * 0.36, offsetY - r, offsetX + s * 0.5, offsetY - r);
        path.quadTo(offsetX + s * 0.64, offsetY - r, offsetX + s * 0.64, offsetY);
        path.lineTo(offsetX + s, offsetY);

        // 右边：凸起
        path.lineTo(offsetX + s, offsetY + s * 0.36);
        path.quadTo(offsetX + s + r, offsetY + s * 0.36, offsetX + s + r, offsetY + s * 0.5);
        path.quadTo(offsetX + s + r, offsetY + s * 0.64, offsetX + s, offsetY + s * 0.64);
        path.lineTo(offsetX + s, offsetY + s);

        // 下边：平直
        path.lineTo(offsetX, offsetY + s);

        // 左边：凹陷
        path.lineTo(offsetX, offsetY + s * 0.64);
        path.quadTo(offsetX - r * 0.6, offsetY + s * 0.64, offsetX - r * 0.6, offsetY + s * 0.5);
        path.quadTo(offsetX - r * 0.6, offsetY + s * 0.36, offsetX, offsetY + s * 0.36);
        path.lineTo(offsetX, offsetY);

        path.closePath();
        return path;
    }

    /**
     * 生成随机彩色背景（渐变色块 + 噪点）
     */
    private static BufferedImage generateRandomBackground() {
        BufferedImage img = new BufferedImage(BG_WIDTH, BG_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        enableAntiAliasing(g);

        // 随机渐变背景
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(60 + RANDOM.nextInt(120), 100 + RANDOM.nextInt(100), 150 + RANDOM.nextInt(100)),
                BG_WIDTH, BG_HEIGHT, new Color(150 + RANDOM.nextInt(100), 80 + RANDOM.nextInt(100), 60 + RANDOM.nextInt(120))
        );
        g.setPaint(gradient);
        g.fillRect(0, 0, BG_WIDTH, BG_HEIGHT);

        // 随机色块
        for (int i = 0; i < 6; i++) {
            g.setColor(new Color(
                    RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256),
                    40 + RANDOM.nextInt(60)
            ));
            int rx = RANDOM.nextInt(BG_WIDTH);
            int ry = RANDOM.nextInt(BG_HEIGHT);
            int rw = 40 + RANDOM.nextInt(80);
            int rh = 40 + RANDOM.nextInt(80);
            g.fillRoundRect(rx, ry, rw, rh, 10, 10);
        }

        // 随机圆形
        for (int i = 0; i < 4; i++) {
            g.setColor(new Color(
                    RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256),
                    30 + RANDOM.nextInt(50)
            ));
            int cx = RANDOM.nextInt(BG_WIDTH);
            int cy = RANDOM.nextInt(BG_HEIGHT);
            int cr = 20 + RANDOM.nextInt(50);
            g.fillOval(cx, cy, cr, cr);
        }

        // 噪点
        for (int i = 0; i < 200; i++) {
            g.setColor(new Color(RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256), 80));
            g.fillRect(RANDOM.nextInt(BG_WIDTH), RANDOM.nextInt(BG_HEIGHT), 2, 2);
        }

        g.dispose();
        return img;
    }

    private static void enableAntiAliasing(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    private static String toBase64(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("验证码图片编码失败", e);
        }
    }

    /**
     * 验证码生成结果
     */
    @Data
    public static class CaptchaResult {
        /** 验证码唯一标识 */
        private String captchaId;
        /** 带镂空的背景图（base64） */
        private String backgroundImage;
        /** 拼图块图（base64） */
        private String pieceImage;
        /** 拼图块 X 坐标（用于验证） */
        private int x;
        /** 拼图块 Y 坐标（前端用于定位） */
        private int y;
    }

    /**
     * 简易 UUID 生成器（内部使用）
     */
    private static class IdUtil {
        static String fastUUID() {
            return java.util.UUID.randomUUID().toString().replace("-", "");
        }
    }
}
