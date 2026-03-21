package cloud.xcan.angus.core.gm.infra.utils;

import cloud.xcan.angus.api.commonlink.tenant.TenantRepo;
import cloud.xcan.angus.core.spring.SpringContextHolder;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.util.StringUtils;

/**
 * <p>用户注册信息生成工具类</p>
 * <p>用于生成用户名、用户姓名、租户编码、租户名称等信息</p>
 */
public class RegistrationInfoGenerator {

  private static final SecureRandom RANDOM = new SecureRandom();
  private static final String USERNAME_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
  private static final int USERNAME_RANDOM_LENGTH = 4;
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

  /**
   * <p>生成唯一的用户名</p>
   * <p>优先使用邮箱前缀或手机号，如果已存在则添加随机后缀</p>
   *
   * @param email 邮箱地址（可选）
   * @param phone 手机号（可选）
   * @return 唯一的用户名
   * @example <pre>
   * // 示例1：使用邮箱生成用户名
   * generateUsername("zhangsan@example.com", null)
   * // 返回: "zhangsan" 或 "zhangsan_1"（如果已存在）
   *
   * // 示例2：使用手机号生成用户名
   * generateUsername(null, "13800138000")
   * // 返回: "phone_13800138000" 或 "phone_13800138000_1"（如果已存在）
   *
   * // 示例3：都没有提供，使用默认前缀
   * generateUsername(null, null)
   * // 返回: "user" 或 "user_1"（如果已存在）
   * </pre>
   */
  public static String generateUsername(String email, String phone) {
    String baseUsername = null;

    // 优先使用邮箱前缀
    if (StringUtils.hasText(email)) {
      baseUsername = extractEmailPrefix(email);
    }
    // 如果没有邮箱，使用手机号
    else if (StringUtils.hasText(phone)) {
      baseUsername = sanitizePhoneForUsername(phone);
    }

    // 如果都没有，使用默认前缀
    if (baseUsername == null) {
      baseUsername = "user";
    }

    // 确保用户名符合规范（只包含小写字母、数字、下划线）
    baseUsername = sanitizeUsername(baseUsername);

    // 检查用户名是否存在，如果存在则添加后缀
    return baseUsername + "_" + generateRandomString(USERNAME_RANDOM_LENGTH);
  }

  /**
   * <p>生成用户姓名</p>
   * <p>如果没有提供姓名，则基于邮箱或手机号生成默认姓名</p>
   *
   * @param email 邮箱地址（可选）
   * @param phone 手机号（可选）
   * @return 用户姓名
   * @example <pre>
   *
   * // 示例1：基于邮箱生成姓名
   * generateUserName("zhangsan@example.com", null)
   * // 返回: "user_zhangsan"
   *
   * // 示例2：基于手机号生成姓名
   * generateUserName(null, "13800138000")
   * // 返回: "user_8000"
   *
   * // 示例3：都没有提供，使用随机字符串
   * generateUserName(null, null)
   * // 返回: "user_abc123"（随机6位字符）
   * </pre>
   */
  public static String generateUserName(String email, String phone) {
    // 如果有邮箱，使用邮箱前缀作为姓名
    if (StringUtils.hasText(email)) {
      String emailPrefix = extractEmailPrefix(email);
      return "user_" + emailPrefix;
    }

    // 如果有手机号，使用手机号后4位
    if (StringUtils.hasText(phone)) {
      String phoneSuffix = phone.length() > 4 ? phone.substring(phone.length() - 4) : phone;
      return "user_" + phoneSuffix;
    }

    // 默认姓名
    return "user_" + generateRandomString(USERNAME_RANDOM_LENGTH);
  }

  /**
   * <p>生成唯一的租户编码</p>
   * <p>基于租户名称或管理员信息生成，如果已存在则添加后缀</p>
   *
   * @param tenantName 租户名称（可选）
   * @param adminEmail 管理员邮箱（可选）
   * @param adminName  管理员姓名（可选）
   * @return 唯一的租户编码
   * @example <pre>
   * // 示例1：基于租户名称生成编码
   * generateTenantCode("示例公司", null, null)
   * // 返回: "示例公司_20241201" 或 "示例公司_20241201_001"（如果已存在）
   *
   * // 示例2：基于管理员邮箱生成编码
   * generateTenantCode(null, "admin@example.com", null)
   * // 返回: "ADMIN_20241201" 或 "ADMIN_20241201_001"（如果已存在）
   *
   * // 示例3：基于管理员姓名生成编码
   * generateTenantCode(null, null, "张三")
   * // 返回: "张三_20241201" 或 "张三_20241201_001"（如果已存在）
   *
   * // 示例4：都没有提供，使用默认前缀
   * generateTenantCode(null, null, null)
   * // 返回: "tenant_20241201" 或 "tenant_20241201_001"（如果已存在）
   * </pre>
   */
  public static String generateTenantCode(String tenantName, String adminEmail, String adminName) {
    String baseCode = null;

    // 优先使用租户名称生成编码
    if (StringUtils.hasText(tenantName)) {
      baseCode = sanitizeTenantCode(tenantName);
    }
    // 如果没有租户名称，使用管理员邮箱前缀
    else if (StringUtils.hasText(adminEmail)) {
      baseCode = extractEmailPrefix(adminEmail);
    }
    // 如果没有邮箱，使用管理员姓名
    else if (StringUtils.hasText(adminName)) {
      baseCode = sanitizeTenantCode(adminName);
    }

    // 如果都没有，使用默认前缀 + 日期
    if (baseCode == null) {
      String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
      baseCode = "tenant_" + dateStr;
    } else {
      // 添加日期后缀确保唯一性
      String dateStr = LocalDateTime.now().format(DATE_FORMATTER);
      baseCode = baseCode + "_" + dateStr;
    }

    // 确保编码符合规范（只包含大写字母、数字、下划线，最大长度50）
    baseCode = sanitizeTenantCode(baseCode);
    if (baseCode.length() > 50) {
      baseCode = baseCode.substring(0, 50);
    }

    // 检查租户编码是否存在，如果存在则添加后缀
    String code = baseCode;
    int suffix = 1;
    TenantRepo tenantRepo = SpringContextHolder.getBean("tenantRepo", TenantRepo.class);
    assert tenantRepo != null;
    while (tenantRepo.existsByCode(code)) {
      String suffixStr = String.format("%03d", suffix);
      // 计算总长度：baseCode + "_" + suffixStr
      int totalLength = baseCode.length() + 1 + suffixStr.length();
      if (totalLength <= 50) {
        code = baseCode + "_" + suffixStr;
      } else {
        // 如果总长度超过50，截取baseCode
        int maxBaseLength = 50 - 1 - suffixStr.length();
        code = baseCode.substring(0, Math.max(0, maxBaseLength)) + "_" + suffixStr;
      }
      suffix++;
      // 防止无限循环
      if (suffix > 999) {
        code = baseCode.substring(0, Math.min(40, baseCode.length())) + "_"
            + generateRandomString(6).toUpperCase();
        break;
      }
    }

    return code;
  }

  /**
   * <p>从邮箱地址提取前缀</p>
   *
   * @param email 邮箱地址
   * @return 邮箱前缀（@符号前的部分）
   */
  private static String extractEmailPrefix(String email) {
    if (!StringUtils.hasText(email)) {
      return null;
    }
    int atIndex = email.indexOf('@');
    if (atIndex > 0) {
      return email.substring(0, atIndex);
    }
    return email;
  }

  /**
   * <p>将手机号转换为适合用户名的格式</p>
   *
   * @param phone 手机号
   * @return 用户名格式的手机号
   */
  private static String sanitizePhoneForUsername(String phone) {
    if (!StringUtils.hasText(phone)) {
      return null;
    }
    // 移除所有非数字字符
    String cleaned = phone.replaceAll("[^0-9]", "");
    // 如果手机号太长，只取后11位
    if (cleaned.length() > 11) {
      cleaned = cleaned.substring(cleaned.length() - 11);
    }
    return "phone_" + cleaned;
  }

  /**
   * <p>清理用户名，确保只包含小写字母、数字和下划线</p>
   *
   * @param username 原始用户名
   * @return 清理后的用户名
   */
  private static String sanitizeUsername(String username) {
    if (!StringUtils.hasText(username)) {
      return "user";
    }
    // 转换为小写，只保留字母、数字和下划线
    String cleaned = username.toLowerCase().replaceAll("[^a-z0-9_]", "_");
    // 移除连续的下划线
    cleaned = cleaned.replaceAll("_{2,}", "_");
    // 移除开头和结尾的下划线
    cleaned = cleaned.replaceAll("^_+|_+$", "");
    // 确保不为空
    if (cleaned.isEmpty()) {
      cleaned = "user";
    }
    return cleaned;
  }

  /**
   * <p>清理租户编码，确保只包含大写字母、数字和下划线</p>
   *
   * @param code 原始编码
   * @return 清理后的编码
   */
  private static String sanitizeTenantCode(String code) {
    if (!StringUtils.hasText(code)) {
      return "TENANT";
    }
    // 转换为大写，只保留字母、数字和下划线
    String cleaned = code.toUpperCase().replaceAll("[^A-Z0-9_]", "_");
    // 移除连续的下划线
    cleaned = cleaned.replaceAll("_{2,}", "_");
    // 移除开头和结尾的下划线
    cleaned = cleaned.replaceAll("^_+|_+$", "");
    // 确保不为空
    if (cleaned.isEmpty()) {
      cleaned = "TENANT";
    }
    return cleaned;
  }

  /**
   * <p>生成随机字符串</p>
   *
   * @param length 字符串长度
   * @return 随机字符串
   */
  private static String generateRandomString(int length) {
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append(USERNAME_CHARS.charAt(RANDOM.nextInt(USERNAME_CHARS.length())));
    }
    return sb.toString();
  }
}
