package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.api.commonlink.GMConstant.EMAIL_CACHE_REPEAT_CHECK_PREFIX;
import static cloud.xcan.angus.api.commonlink.GMConstant.EMAIL_VERIFICATION_CODE_CACHE_PREFIX;

import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.email.EmailTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class EmailConverter {

  public static @NotNull Email toTemplateEmail(EmailTemplate template, String to, String cc,
      String bcc, Map<String, String> params) {
    // 替换模板主题中的参数
    String subject = replaceTemplateParams(template.getSubject(), params);

    // 替换模板内容中的参数
    String htmlContent = replaceTemplateParams(template.getContent(), params);

    // 创建邮件实体
    Email email = new Email();
    email.setToRecipients(List.of(to));

    // 设置抄送和密送（如果提供）
    if (cc != null && !cc.isEmpty()) {
      email.setCcRecipients(List.of(cc));
    }
    if (bcc != null && !bcc.isEmpty()) {
      email.setBccRecipients(List.of(bcc));
    }

    email.setSubject(subject);
    email.setHtmlContent(htmlContent);
    email.setTemplateId(template.getId());

    // 保存模板参数
    if (params != null && !params.isEmpty()) {
      Map<String, Object> templateParams = new HashMap<>(params);
      email.setTemplateParams(templateParams);
    }
    return email;
  }

  /**
   * 替换模板参数，支持 {变量名} 和 {{变量名}} 格式
   */
  public static String replaceTemplateParams(String template, Map<String, String> params) {
    if (template == null || template.isEmpty() || params == null || params.isEmpty()) {
      return template;
    }

    String result = template;
    for (Map.Entry<String, String> entry : params.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue() != null ? entry.getValue() : "";
      // 替换 {key} 格式
      result = result.replace("{" + key + "}", value);
      // 替换 {{key}} 格式（邮件模板常用格式）
      result = result.replace("{{" + key + "}}", value);
    }
    return result;
  }

  /**
   * 替换模板参数，支持 {变量名} 和 {{变量名}} 格式（支持Object类型的参数值）
   */
  public static String replaceTemplateParamsWithObject(String template,
      Map<String, Object> params) {
    if (template == null || template.isEmpty() || params == null || params.isEmpty()) {
      return template;
    }

    // 将Object类型的参数值转换为String
    Map<String, String> stringParams = new HashMap<>();
    for (Map.Entry<String, Object> entry : params.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      stringParams.put(key, value != null ? value.toString() : "");
    }

    return replaceTemplateParams(template, stringParams);
  }

  public static String getEmailVerificationCodeCacheKey(String templateCode, String email) {
    return EMAIL_VERIFICATION_CODE_CACHE_PREFIX + ":" + templateCode + ":" + email;
  }

  public static String getEmailVerificationCodeRepeatCheckKey(String templateCode, String email) {
    return EMAIL_CACHE_REPEAT_CHECK_PREFIX + ":" + templateCode + ":" + email;
  }

}
