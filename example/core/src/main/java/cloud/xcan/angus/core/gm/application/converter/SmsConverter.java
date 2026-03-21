package cloud.xcan.angus.core.gm.application.converter;

import static cloud.xcan.angus.api.commonlink.GMConstant.SMS_VC_CACHE_PREFIX;
import static cloud.xcan.angus.api.commonlink.GMConstant.SMS_VC_CACHE_REPEAT_CHECK_PREFIX;
import static cloud.xcan.angus.core.spring.SpringContextHolder.getCachedUidGenerator;
import static cloud.xcan.angus.spec.experimental.SimpleResult.SUCCESS_CODE;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.sms.SmsStatus;
import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.core.gm.domain.sms.SmsProvider;
import cloud.xcan.angus.core.gm.domain.sms.SmsTemplate;
import cloud.xcan.angus.core.gm.domain.sms.enums.SmsType;
import cloud.xcan.angus.spec.experimental.SimpleResult;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class SmsConverter {

  public static @NotNull Sms toSendSms(SmsTemplate templateDb, SmsProvider providerDb,
      String phone, String content, Map<String, String> params) {
    Sms sms = new Sms();
    sms.setId(getCachedUidGenerator().getUID());
    sms.setPhone(phone);
    sms.setTemplateId(templateDb.getId());
    sms.setContent(content);
    sms.setTemplateCode(templateDb.getTemplateCode());
    sms.setProvider(providerDb.getName());
    sms.setType(SmsType.NOTIFICATION);
    sms.setStatus(SmsStatus.PENDING);
    sms.setSendTime(LocalDateTime.now());
    sms.setTemplateParams(params);
    return sms;
  }

  public static @NotNull Sms toBatchSendSms(SmsTemplate templateDb, SmsProvider providerDb,
      String phone, String content, Map<String, String> params, SimpleResult result) {
    Sms sms = new Sms();
    sms.setId(getCachedUidGenerator().getUID());
    sms.setPhone(phone);
    sms.setTemplateId(templateDb.getId());
    sms.setContent(content);
    sms.setTemplateCode(templateDb.getTemplateCode());
    sms.setProvider(providerDb.getName());
    sms.setType(SmsType.NOTIFICATION);
    sms.setSendTime(LocalDateTime.now());
    sms.setTemplateParams(params);

    // 更新发送结果
    if (SUCCESS_CODE.equals(result.getCode())) {
      sms.setStatus(SmsStatus.SENT);
      sms.setMessageId(result.getMessage());
    } else {
      sms.setStatus(SmsStatus.FAILED);
      sms.setErrorCode(result.getCode());
      sms.setErrorMessage(result.getMessage());
    }
    return sms;
  }

  public static Sms toTestSms(SmsProvider providerDb, String phone, String content) {
    Sms sms = new Sms();
    sms.setId(getCachedUidGenerator().getUID());
    sms.setPhone(phone);
    sms.setContent(content);
    sms.setProvider(providerDb.getName());
    sms.setType(SmsType.TEST);
    sms.setStatus(SmsStatus.PENDING);
    sms.setSendTime(LocalDateTime.now());
    return sms;
  }

  public static String getSmsVerificationCodeCacheKey(String templateCode, String mobile) {
    return SMS_VC_CACHE_PREFIX + ":" + templateCode + ":" + mobile;
  }

  public static String getSmsVerificationCodeRepeatCheckKey(String templateCode, String mobile) {
    return SMS_VC_CACHE_REPEAT_CHECK_PREFIX + ":" + templateCode + ":" + mobile;
  }

  /**
   * 替换模板参数 支持 {变量名} 格式
   */
  public static String replaceTemplateParams(String template, Map<String, String> params) {
    if (isEmpty(template) || isEmpty(params)) {
      return template;
    }

    String result = template;
    for (Map.Entry<String, String> entry : params.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue() != null ? entry.getValue() : "";
      // 替换 ${key} 格式
      result = result.replace("${" + key + "}", value);
      // 替换 {{key}} 格式（兼容邮件模板格式）
      result = result.replace("{{" + key + "}}", value);
      // 替换 {key} 格式
      result = result.replace("{" + key + "}", value);
    }
    return result;
  }

}
