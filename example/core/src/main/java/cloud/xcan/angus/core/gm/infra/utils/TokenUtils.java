package cloud.xcan.angus.core.gm.infra.utils;

import java.security.MessageDigest;

public class TokenUtils {

  /**
   * 对令牌进行SHA-256哈希处理
   */
  public static String hashToken(String token) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(token.getBytes());
      StringBuilder hexString = new StringBuilder();
      for (byte b : hash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (Exception e) {
      throw new RuntimeException("令牌哈希处理失败", e);
    }
  }

}
