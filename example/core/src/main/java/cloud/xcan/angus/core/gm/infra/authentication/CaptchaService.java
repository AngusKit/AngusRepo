package cloud.xcan.angus.core.gm.infra.authentication;

import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.CaptchaVo;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CaptchaService {

  private static final String CAPTCHA_PREFIX = "captcha:";
  private static final int CAPTCHA_EXPIRE_SECONDS = 300; // 5分钟
  private static final int CAPTCHA_WIDTH = 120;
  private static final int CAPTCHA_HEIGHT = 40;
  private static final int CAPTCHA_LENGTH = 4;
  private static final String CAPTCHA_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

  private final Random random = new Random();
  private final RedisTemplate<String, String> redisTemplate;

  public CaptchaService(RedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public CaptchaVo generate() {
    // 生成验证码key
    String captchaKey = UUID.randomUUID().toString();

    // 生成验证码文本
    String captchaText = generateCaptchaText();

    // 存储验证码到Redis
    redisTemplate.opsForValue().set(
        CAPTCHA_PREFIX + captchaKey,
        captchaText,
        CAPTCHA_EXPIRE_SECONDS,
        TimeUnit.SECONDS
    );

    // 生成验证码图片
    String captchaImage = generateCaptchaImage(captchaText);

    CaptchaVo vo = new CaptchaVo();
    vo.setCaptchaKey(captchaKey);
    vo.setCaptchaImage(captchaImage);
    vo.setExpireTime(CAPTCHA_EXPIRE_SECONDS);
    return vo;
  }

  public boolean verify(String captchaKey, String captcha) {
    if (captchaKey == null || captcha == null) {
      return false;
    }

    String storedCaptcha = redisTemplate.opsForValue().get(CAPTCHA_PREFIX + captchaKey);
    if (storedCaptcha == null) {
      return false;
    }

    // 验证后删除
    redisTemplate.delete(CAPTCHA_PREFIX + captchaKey);

    return storedCaptcha.equalsIgnoreCase(captcha);
  }

  private String generateCaptchaText() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < CAPTCHA_LENGTH; i++) {
      sb.append(CAPTCHA_CHARS.charAt(random.nextInt(CAPTCHA_CHARS.length())));
    }
    return sb.toString();
  }

  private String generateCaptchaImage(String text) {
    BufferedImage image = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HEIGHT,
        BufferedImage.TYPE_INT_RGB);
    Graphics2D g = image.createGraphics();

    // 设置背景色
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);

    // 设置字体
    Font font = new Font("Arial", Font.BOLD, 28);
    g.setFont(font);

    // 绘制验证码文本
    for (int i = 0; i < text.length(); i++) {
      g.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
      int x = 20 + i * 25;
      int y = 25 + random.nextInt(10);
      g.drawString(String.valueOf(text.charAt(i)), x, y);
    }

    // 绘制干扰线
    for (int i = 0; i < 5; i++) {
      g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
      int x1 = random.nextInt(CAPTCHA_WIDTH);
      int y1 = random.nextInt(CAPTCHA_HEIGHT);
      int x2 = random.nextInt(CAPTCHA_WIDTH);
      int y2 = random.nextInt(CAPTCHA_HEIGHT);
      g.drawLine(x1, y1, x2, y2);
    }

    g.dispose();

    // 转换为Base64
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(image, "png", baos);
      byte[] imageBytes = baos.toByteArray();
      return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
    } catch (IOException e) {
      throw new RuntimeException("生成验证码图片失败", e);
    }
  }
}
