package cloud.xcan.angus.core.gm.infra.user;

import java.util.List;

/**
 * TOTP双因素认证服务
 */
public interface TotpService {

  /**
   * 生成TOTP密钥
   */
  String generateSecret();

  /**
   * 生成QR码（Base64格式）
   */
  String generateQRCode(String secret, String accountName, String issuer);

  /**
   * 验证TOTP验证码
   */
  boolean verifyCode(String secret, String code);

  /**
   * 生成备用恢复码
   */
  List<String> generateBackupCodes(int count);
}
