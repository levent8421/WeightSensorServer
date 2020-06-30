package com.berrontech.dsensor.dataserver.common.util;

import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Create by 郭文梁 2019/5/11 0011 14:39
 * QrCodeUtil
 * 二维码工具类
 *
 * @author 郭文梁
 * @data 2019/5/11 0011
 */
@Slf4j
public class QrCodeUtil {
    private static final int DEFAULT_MARGIN = 2;
    /**
     * 二维码图片格式
     */
    private static final String IMAGE_FORMAT = "png";
    private static final Writer QR_CODE_WRITER = new MultiFormatWriter();
    private static final int BYTE_BITS = 8;

    /**
     * 创建二维码
     *
     * @param contents 内容
     * @param width    宽度
     * @param height   高度
     * @return bytes
     */
    public static byte[] createQrCode(String contents, int width, int height) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            createQrCode(contents, width, height, out);
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }

    /**
     * 生成二维码并写入到流中
     *
     * @param content 二维码内容
     * @param width   宽度
     * @param height  高度
     * @param out     流
     * @throws IOException IO异常
     */
    public static void createQrCode(String content, int width, int height, OutputStream out) throws IOException {
        try {
            BufferedImage image = toBufferedImage(content, width, height);
            //转换成png格式的IO流
            ImageIO.write(image, IMAGE_FORMAT, out);
        } catch (WriterException e) {
            throw new InternalServerErrorException(e);
        }
    }

    /**
     * 生成二维码的BufferedImage
     *
     * @param content 内容
     * @param width   宽度
     * @param height  高度
     * @return BufferImage
     * @throws WriterException 异常
     */
    private static BufferedImage toBufferedImage(String content, int width, int height) throws WriterException {
        return toBufferedImage(content, width, height, DEFAULT_MARGIN);
    }

    /**
     * 生成二维码的BufferedImage
     *
     * @param content 内容
     * @param width   宽度
     * @param height  高度
     * @param margin  边距
     * @return BufferImage
     * @throws WriterException 异常
     */
    private static BufferedImage toBufferedImage(String content, int width, int height, int margin) throws WriterException {
        final BitMatrix bitMatrix = encode(content, width, height, margin);
        // 1、读取文件转换为字节数组
        return toBufferedImage(bitMatrix);
    }

    /**
     * 編碼二維碼
     *
     * @param content 二維碼内容
     * @param width   寬度
     * @param height  高度
     * @param margin  　邊距
     * @return BM
     * @throws WriterException ex
     */
    private static BitMatrix encode(String content, int width, int height, int margin) throws WriterException {
        HashMap<EncodeHintType, Object> hints = new HashMap<>(2);
        hints.put(EncodeHintType.MARGIN, margin);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        return new MultiFormatWriter().encode(
                content, BarcodeFormat.QR_CODE, width, height, hints);
    }

    /**
     * image流数据处理
     *
     * @author ianly
     */
    private static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
    }

    /**
     * 创建带背景的二维码
     *
     * @param background    背景
     * @param qrCodeContent 二维码内容
     * @param qrCodeWidth   二维码宽度
     * @param qrCodeHeight  二维码高度
     * @param top           margin top
     * @param left          margin left
     * @return bytes
     */
    private static byte[] createQrCodeImage(BufferedImage background, String qrCodeContent,
                                            int qrCodeWidth, int qrCodeHeight, int left, int top) {
        try {
            val qrCode = toBufferedImage(qrCodeContent, qrCodeWidth, qrCodeHeight, 0);
            final Graphics g = background.getGraphics();
            if (top < 0) {
                top = background.getHeight() + top - qrCodeHeight;
            }
            if (left < 0) {
                left = background.getWidth() + left - qrCodeWidth;
            }
            boolean result = g.drawImage(qrCode, left, top, null);
            log.debug("QrCode Draw result {}", result);
            g.dispose();
            return image2Bytes(background);
        } catch (WriterException | IOException e) {
            throw new InternalServerErrorException("Create group purchase commodity share qrcode error!", e);
        }
    }

    /**
     * 转换Image为二进制数组
     *
     * @param image 图片文件
     * @return 二进制数组
     * @throws IOException IO异常
     */
    private static byte[] image2Bytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, IMAGE_FORMAT, out);
        out.close();
        return out.toByteArray();
    }

    /**
     * 編碼二維碼
     *
     * @param content 二維碼内容
     * @param width   寬度
     * @param height  高度
     * @param margin  邊距
     * @return int array
     * @throws WriterException exception
     */
    public static int[] encodeToBits(String content, int width, int height, int margin) throws WriterException {
        final BitMatrix bitMatrix = encode(content, width, height, margin);
        final int bmWidth = bitMatrix.getWidth();
        final int bmHeight = bitMatrix.getHeight();
        final int[] res = new int[bmHeight * bmWidth];
        int pos = 0;
        for (int y = 0; y < bmHeight; y += BYTE_BITS) {
            for (int x = 0; x < bmWidth; x++) {
                int b = 0;
                for (int i = 0; i < BYTE_BITS; i++) {
                    final int ry = y + i;
                    if (bitMatrix.get(x, ry)) {
                        b |= 0x01 << i;
                    }
                }
                res[pos++] = b;
            }
        }
        return res;
    }
}
