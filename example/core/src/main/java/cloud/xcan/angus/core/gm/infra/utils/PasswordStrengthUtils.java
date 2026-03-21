package cloud.xcan.angus.core.gm.infra.utils;

import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.core.gm.domain.security.model.PasswordPolicyConfig;
import cloud.xcan.angus.core.gm.domain.user.enums.PasswordStrength;

/**
 * 密码强度计算工具类 提供根据密码策略计算密码强度的静态方法
 */
public class PasswordStrengthUtils {

  /**
   * 根据密码策略计算密码强度
   *
   * @param password 密码字符串
   * @param config   密码策略配置，如果为null则使用默认策略
   * @return 密码强度枚举值：WEAK | MEDIUM | STRONG
   */
  public static PasswordStrength calculatePasswordStrength(String password,
      PasswordPolicyConfig config) {
    if (password == null || password.isEmpty()) {
      return PasswordStrength.WEAK;
    }

    // 如果没有提供策略配置，使用默认配置
    PasswordPolicyConfig policy = config != null ? config : new PasswordPolicyConfig();

    // 获取密码策略配置
    int minLength = nullSafe(policy.getMinLength(), 6);
    int maxLength = nullSafe(policy.getMaxLength(), 20);
    boolean requireUppercase = Boolean.TRUE.equals(policy.getRequireUppercase());
    boolean requireLowercase = Boolean.TRUE.equals(policy.getRequireLowercase());
    boolean requireNumbers = Boolean.TRUE.equals(policy.getRequireNumbers());
    boolean requireSpecialChars = Boolean.TRUE.equals(policy.getRequireSpecialChars());

    int passwordLength = password.length();

    // 检查密码长度是否符合要求
    if (passwordLength < minLength || passwordLength > maxLength) {
      return PasswordStrength.WEAK;
    }

    // 统计满足的字符类型要求数量
    int metRequirements = 0;
    int totalRequirements = 0;

    // 检查大写字母
    if (requireUppercase) {
      totalRequirements++;
      if (password.matches(".*[A-Z].*")) {
        metRequirements++;
      }
    }

    // 检查小写字母
    if (requireLowercase) {
      totalRequirements++;
      if (password.matches(".*[a-z].*")) {
        metRequirements++;
      }
    }

    // 检查数字
    if (requireNumbers) {
      totalRequirements++;
      if (password.matches(".*\\d.*")) {
        metRequirements++;
      }
    }

    // 检查特殊字符（使用与验证逻辑相同的正则表达式）
    if (requireSpecialChars) {
      totalRequirements++;
      if (password.matches(".*[@$!%*?&#_\\-+=().,;:<>\\[\\]{}|~`\"'/\\\\].*")) {
        metRequirements++;
      }
    }

    // 如果没有配置任何字符类型要求，只根据长度判断
    if (totalRequirements == 0) {
      if (passwordLength >= 12) {
        return PasswordStrength.STRONG;
      } else if (passwordLength >= 8) {
        return PasswordStrength.MEDIUM;
      } else {
        return PasswordStrength.WEAK;
      }
    }

    // 计算满足要求的比例
    double requirementRatio = (double) metRequirements / totalRequirements;

    // 根据满足要求的比例和密码长度判断强度
    // STRONG: 满足所有要求且密码长度 >= 10，或满足所有要求且密码长度 >= 8
    // MEDIUM: 满足 >= 75% 的要求，或满足 >= 50% 的要求且密码长度 >= 8
    // WEAK: 其他情况

    if (requirementRatio == 1.0) {
      // 满足所有要求
      if (passwordLength >= 10) {
        return PasswordStrength.STRONG;
      } else if (passwordLength >= 8) {
        return PasswordStrength.MEDIUM;
      } else {
        return PasswordStrength.WEAK;
      }
    } else if (requirementRatio >= 0.75) {
      // 满足 >= 75% 的要求
      if (passwordLength >= 10) {
        return PasswordStrength.MEDIUM;
      } else {
        return PasswordStrength.WEAK;
      }
    } else if (requirementRatio >= 0.5) {
      // 满足 >= 50% 的要求
      if (passwordLength >= 8) {
        return PasswordStrength.MEDIUM;
      } else {
        return PasswordStrength.WEAK;
      }
    } else {
      // 满足 < 50% 的要求
      return PasswordStrength.WEAK;
    }
  }

  /**
   * 根据密码策略计算密码强度（使用默认策略）
   *
   * @param password 密码字符串
   * @return 密码强度枚举值：WEAK | MEDIUM | STRONG
   */
  public static PasswordStrength calculatePasswordStrength(String password) {
    return calculatePasswordStrength(password, null);
  }
}
