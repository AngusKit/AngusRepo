package cloud.xcan.angus.api.commonlink;

import cloud.xcan.angus.spec.experimental.Value;
import cloud.xcan.angus.spec.locale.SupportedLanguage;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Locale;

/**
 * 支持的语言枚举
 */
public enum Language implements Value<String> {
  /**
   * 简体中文
   */
  ZH_CN("zh-CN"),

  /**
   * 英语（美国）
   */
  EN_US("en-US");

  private final String value;

  public static final Language DEFAULT = Language.EN_US;

  Language(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String getValue() {
    return this.value;
  }

  public Locale toLocale() {
    if (this == Language.EN_US) {
      return Locale.ENGLISH;
    }
    return Locale.CHINA;
  }

  /**
   * 根据字符串值获取对应的枚举
   *
   * @param value 语言值（如 "zh-CN", "en-US"）
   * @return 对应的 Language 枚举，如果未找到则返回 null
   */
  @JsonCreator
  public static Language fromValue(String value) {
    if (value == null) {
      return DEFAULT;
    }
    String normalized = value.replace("_", "-");
    for (Language language : Language.values()) {
      if (language.value.equals(value) || language.value.equals(normalized)) {
        return language;
      }
    }
    // 兼容 Locale.getLanguage() 返回的 "zh"、"en" 等简短形式
    if ("zh".equalsIgnoreCase(value) || "zh".equalsIgnoreCase(normalized)) {
      return ZH_CN;
    }
    if ("en".equalsIgnoreCase(value) || "en".equalsIgnoreCase(normalized)) {
      return EN_US;
    }
    return DEFAULT;
  }

  public static Language fromDefaultValue(SupportedLanguage value) {
    if (value == null) {
      return DEFAULT;
    }
    if (SupportedLanguage.zh_CN.equals(value)) {
      return ZH_CN;
    }
    return DEFAULT;
  }
}
