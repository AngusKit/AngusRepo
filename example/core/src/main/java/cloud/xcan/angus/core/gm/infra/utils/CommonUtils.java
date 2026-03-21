package cloud.xcan.angus.core.gm.infra.utils;

import cloud.xcan.angus.api.commonlink.Language;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.StringUtils;

public class CommonUtils {

  /**
   * 判断是否为邮箱格式
   */
  public static boolean isEmail(String account) {
    return account != null && account.contains("@") && account.contains(".");
  }

  /**
   * 脱敏手机号
   */
  public static String maskPhone(String phone) {
    if (phone == null || phone.length() < 7) {
      return phone;
    }
    return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
  }

  /**
   * 脱敏内容中的5位或6位连续数字 将连续包含5位或6位数字的字符串替换成对应长度的星号 长度小于5或大于6的数字不替换
   *
   * @param content 原始内容
   * @return 脱敏后的内容
   */
  public static String maskDigits(String content) {
    if (content == null || content.isEmpty()) {
      return content;
    }
    // 使用正则表达式匹配独立的5位或6位数字（前后不是数字）
    // (?<!\d) 负向后顾，确保前面不是数字
    // \d{5,6} 匹配5位或6位数字
    // (?!\d) 负向前瞻，确保后面不是数字
    Pattern pattern = Pattern.compile("(?<!\\d)\\d{5,6}(?!\\d)");
    Matcher matcher = pattern.matcher(content);
    StringBuffer result = new StringBuffer();

    while (matcher.find()) {
      String matched = matcher.group();
      // 根据匹配到的数字长度生成对应长度的星号
      StringBuilder replacement = new StringBuilder();
      for (int i = 0; i < matched.length(); i++) {
        replacement.append("*");
      }
      matcher.appendReplacement(result, replacement.toString());
    }
    matcher.appendTail(result);

    return result.toString();
  }

  public static String formatFileSize(long bytes) {
    if (bytes < 1024) {
      return bytes + " B";
    } else if (bytes < 1024 * 1024) {
      return String.format("%.2f KB", bytes / 1024.0);
    } else if (bytes < 1024 * 1024 * 1024) {
      return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    } else {
      return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
  }

  /**
   * 格式化运行时间
   */
  public static String formatUptime(long seconds) {
    long days = seconds / 86400;
    long hours = (seconds % 86400) / 3600;
    long minutes = (seconds % 3600) / 60;
    if (days > 0) {
      return String.format("%dd %dh %dm", days, hours, minutes);
    } else if (hours > 0) {
      return String.format("%dh %dm", hours, minutes);
    } else {
      return String.format("%dm", minutes);
    }
  }

  public static Long calculateDurationWithSecond(LocalDateTime startTime, LocalDateTime endTime) {
    if (startTime == null || endTime == null) {
      return 0L;
    }
    return Duration.between(startTime, endTime).toSeconds();
  }

  /**
   * 计算两个 LocalDateTime 之间的天数差
   *
   * @param start 开始时间
   * @param end   结束时间
   * @return 天数差（end - start），若任一参数为 null 则返回 0
   */
  public static long daysBetween(LocalDateTime start, LocalDateTime end) {
    if (start == null || end == null) {
      return 0L;
    }
    return ChronoUnit.DAYS.between(start, end);
  }

  /**
   * 格式化百分比为2位小数
   *
   * @param percent 百分比值（例如：85.123456）
   * @return 格式化后的百分比值，保留2位小数（例如：85.12）
   */
  public static Double formatPercent(Double percent) {
    if (percent == null) {
      return null;
    }
    return BigDecimal.valueOf(percent)
        .setScale(2, RoundingMode.HALF_UP)
        .doubleValue();
  }


  /**
   * 解析周期字符串为时间范围
   */
  public static LocalDateTime[] parsePeriod(String period) {
    LocalDateTime endDate = LocalDateTime.now();
    LocalDateTime startDate;

    if (StringUtils.hasText(period)) {
      period = period.toLowerCase();
      if (period.endsWith("h")) {
        int hours = Integer.parseInt(period.substring(0, period.length() - 1));
        startDate = endDate.minusHours(hours);
      } else if (period.endsWith("d")) {
        int days = Integer.parseInt(period.substring(0, period.length() - 1));
        startDate = endDate.minusDays(days);
      } else {
        // 默认24小时
        startDate = endDate.minusHours(24);
      }
    } else {
      // 默认24小时
      startDate = endDate.minusHours(24);
    }

    return new LocalDateTime[]{startDate, endDate};
  }

  /**
   * 解析时间周期字符串为小时数
   */
  public static int parsePeriodHour(String period) {
    if (period == null || period.isEmpty()) {
      return 1; // 默认1小时
    }

    try {
      if (period.endsWith("h")) {
        return Integer.parseInt(period.substring(0, period.length() - 1));
      } else if (period.endsWith("d")) {
        return Integer.parseInt(period.substring(0, period.length() - 1)) * 24;
      } else if (period.endsWith("m")) {
        return Integer.parseInt(period.substring(0, period.length() - 1)) / 60;
      } else {
        return Integer.parseInt(period);
      }
    } catch (NumberFormatException | DateTimeParseException e) {
      return 1; // 默认1小时
    }
  }

  /**
   * 解析日期字符串
   */
  public static LocalDateTime parseDate(String dateStr, boolean isEnd) {
    if (!StringUtils.hasText(dateStr)) {
      return null;
    }
    try {
      if (dateStr.length() == 10) {
        // yyyy-MM-dd格式
        return LocalDateTime.parse(dateStr + (isEnd ? " 23:59:59" : " 00:00:00"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
      } else {
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
      }
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * 格式化日期时间为可读字符串
   *
   * @param dateTime 日期时间
   * @param language 语言代码（zh_CN 或 en_US）
   * @return 格式化后的日期时间字符串
   */
  public static String formatDateTime(LocalDateTime dateTime, Language language) {
    if (dateTime == null) {
      return "";
    }

    DateTimeFormatter formatter;
    if (Language.ZH_CN.equals(language)) {
      // 中文格式：2026年1月31日 14:30
      formatter = DateTimeFormatter.ofPattern("yyyy年M月d日 HH:mm", Locale.SIMPLIFIED_CHINESE);
    } else {
      // 英文格式：Jan 31, 2026 14:30
      formatter = DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm", Locale.ENGLISH);
    }
    return dateTime.format(formatter);
  }

}
