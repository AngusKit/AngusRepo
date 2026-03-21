package cloud.xcan.angus.core.gm.infra.utils;

import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRequestLog;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/**
 * 请求日志脱敏工具 对认证头、访问令牌、密钥、密码、手机号、邮箱等敏感信息进行脱敏
 */
public final class RequestLogDesensitizer {

  private static final String MASK_VALUE = "****";
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  /**
   * 需完全脱敏的 Header 名称（忽略大小写）
   */
  private static final Set<String> SENSITIVE_HEADERS = new HashSet<>(Arrays.asList(
      "authorization",
      "proxy-authorization",
      "x-auth-token",
      "x-access-token",
      "x-api-key",
      "x-client-secret",
      "cookie",
      "set-cookie",
      "api-key",
      "bearer"
  ));

  /**
   * 需完全脱敏的 Query/Body 参数名（忽略大小写）
   */
  private static final Set<String> SENSITIVE_PARAM_KEYS = new HashSet<>(Arrays.asList(
      "password", "passwd", "pwd", "pass",
      "token", "access_token", "refresh_token", "id_token", "accessToken", "refreshToken",
      "secret", "client_secret", "api_secret", "clientSecret", "apiSecret",
      "api_key", "apikey", "apiKey",
      "authorization", "auth",
      "credit_card", "card_number", "cardNumber",
      "cvv", "cvc",
      "private_key", "privateKey"
  ));

  /**
   * 需部分脱敏的参数名（保留前后几位）
   */
  private static final Set<String> PARTIAL_MASK_PARAM_KEYS = new HashSet<>(Arrays.asList(
      "email", "mail",
      "phone", "mobile", "telephone", "tel",
      "id_card", "idCard", "identity_card", "identityCard"
  ));

  /**
   * 手机号正则：11位数字
   */
  private static final Pattern PHONE_PATTERN = Pattern.compile("1[3-9]\\d{9}");

  /**
   * 邮箱正则
   */
  private static final Pattern EMAIL_PATTERN = Pattern.compile(
      "[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+");

  private RequestLogDesensitizer() {
  }

  /**
   * 对 InterfaceRequestLog 的敏感字段进行脱敏（原地修改）
   */
  public static void desensitize(InterfaceRequestLog log) {
    if (log == null) {
      return;
    }
    log.setApiKey(maskApiKey(log.getApiKey()));
    log.setRequestHeaders(desensitizeHeaders(log.getRequestHeaders()));
    log.setResponseHeaders(desensitizeHeaders(log.getResponseHeaders()));
    log.setQueryParams(desensitizeQueryParam(log.getQueryParams()));
    log.setRequestBody(desensitizeJsonOrText(log.getRequestBody()));
    log.setResponseBody(desensitizeJsonOrText(log.getResponseBody()));
  }

  /**
   * 脱敏 API Key：保留前4后4位
   */
  public static String maskApiKey(String value) {
    if (!StringUtils.hasText(value) || value.length() <= 8) {
      return value != null ? MASK_VALUE : null;
    }
    return value.substring(0, 4) + "****" + value.substring(value.length() - 4);
  }

  /**
   * 脱敏 Headers
   */
  public static LinkedMultiValueMap<String, String> desensitizeHeaders(
      MultiValueMap<String, String> headers) {
    if (headers == null || headers.isEmpty()) {
      return headers != null ? new LinkedMultiValueMap<>(headers) : null;
    }
    LinkedMultiValueMap<String, String> result = new LinkedMultiValueMap<>();
    headers.forEach((key, values) -> {
      String lowerKey = key != null ? key.toLowerCase() : "";
      if (SENSITIVE_HEADERS.contains(lowerKey)) {
        result.put(key, Arrays.asList(MASK_VALUE));
      } else {
        result.put(key, values);
      }
    });
    return result;
  }

  /**
   * 脱敏 Query 参数字符串（格式：key1=value1&key2=value2）
   */
  public static String desensitizeQueryParam(String queryParam) {
    if (!StringUtils.hasText(queryParam)) {
      return queryParam;
    }
    try {
      Map<String, String> params = parseQueryString(queryParam);
      if (params.isEmpty()) {
        return queryParam;
      }
      StringBuilder sb = new StringBuilder();
      for (Map.Entry<String, String> e : params.entrySet()) {
        if (sb.length() > 0) {
          sb.append('&');
        }
        String key = e.getKey();
        String value = maskParamValue(key, e.getValue());
        sb.append(key).append('=').append(value);
      }
      return sb.toString();
    } catch (Exception e) {
      return queryParam;
    }
  }

  /**
   * 脱敏 JSON 或纯文本 Body
   */
  public static String desensitizeJsonOrText(String body) {
    if (!StringUtils.hasText(body)) {
      return body;
    }
    String trimmed = body.trim();
    if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
      return desensitizeJson(body);
    }
    return desensitizePlainText(body);
  }

  /**
   * 脱敏 JSON 中的敏感字段
   */
  public static String desensitizeJson(String json) {
    if (!StringUtils.hasText(json)) {
      return json;
    }
    try {
      JsonNode root = OBJECT_MAPPER.readTree(json);
      if (root.isObject()) {
        desensitizeJsonObject((ObjectNode) root);
      } else if (root.isArray()) {
        desensitizeJsonArray((ArrayNode) root);
      }
      return OBJECT_MAPPER.writeValueAsString(root);
    } catch (Exception e) {
      return json;
    }
  }

  private static void desensitizeJsonObject(ObjectNode node) {
    node.fields().forEachRemaining(entry -> {
      String key = entry.getKey();
      JsonNode value = entry.getValue();
      String lowerKey = key.toLowerCase();
      if (SENSITIVE_PARAM_KEYS.contains(lowerKey)) {
        node.put(key, MASK_VALUE);
      } else if (PARTIAL_MASK_PARAM_KEYS.contains(lowerKey) && value.isTextual()) {
        node.put(key, maskPartialValue(value.asText(), lowerKey));
      } else if (value.isObject()) {
        desensitizeJsonObject((ObjectNode) value);
      } else if (value.isArray()) {
        desensitizeJsonArray((ArrayNode) value);
      }
    });
  }

  private static void desensitizeJsonArray(ArrayNode array) {
    for (int i = 0; i < array.size(); i++) {
      JsonNode elem = array.get(i);
      if (elem.isObject()) {
        desensitizeJsonObject((ObjectNode) elem);
      } else if (elem.isArray()) {
        desensitizeJsonArray((ArrayNode) elem);
      }
    }
  }

  /**
   * 脱敏纯文本中的手机号、邮箱
   */
  public static String desensitizePlainText(String text) {
    if (!StringUtils.hasText(text)) {
      return text;
    }
    String result = PHONE_PATTERN.matcher(text).replaceAll(mr -> CommonUtils.maskPhone(mr.group()));
    result = EMAIL_PATTERN.matcher(result).replaceAll(mr -> maskEmail(mr.group()));
    return result;
  }

  private static Map<String, String> parseQueryString(String query)
      throws UnsupportedEncodingException {
    Map<String, String> params = new LinkedHashMap<>();
    for (String pair : query.split("&")) {
      int eq = pair.indexOf('=');
      if (eq > 0) {
        String key = URLDecoder.decode(pair.substring(0, eq).trim(), StandardCharsets.UTF_8.name());
        String value = URLDecoder.decode(pair.substring(eq + 1).trim(),
            StandardCharsets.UTF_8.name());
        params.put(key, value);
      }
    }
    return params;
  }

  private static String maskParamValue(String key, String value) {
    if (!StringUtils.hasText(value)) {
      return value;
    }
    String lowerKey = key != null ? key.toLowerCase() : "";
    if (SENSITIVE_PARAM_KEYS.contains(lowerKey)) {
      return MASK_VALUE;
    }
    if (PARTIAL_MASK_PARAM_KEYS.contains(lowerKey)) {
      return maskPartialValue(value, lowerKey);
    }
    return value;
  }

  private static String maskPartialValue(String value, String keyHint) {
    if (!StringUtils.hasText(value)) {
      return value;
    }
    if (keyHint.contains("phone") || keyHint.contains("mobile") || keyHint.contains("tel")) {
      return CommonUtils.maskPhone(value);
    }
    if (keyHint.contains("email") || keyHint.contains("mail")) {
      return maskEmail(value);
    }
    if (keyHint.contains("id") && keyHint.contains("card")) {
      return maskIdCard(value);
    }
    return value;
  }

  private static String maskEmail(String email) {
    if (email == null || !email.contains("@")) {
      return email != null ? MASK_VALUE : null;
    }
    int at = email.indexOf('@');
    String local = email.substring(0, at);
    String domain = email.substring(at);
    if (local.length() <= 2) {
      return "**" + domain;
    }
    return local.substring(0, 2) + "****" + domain;
  }

  private static String maskIdCard(String idCard) {
    if (idCard == null || idCard.length() < 8) {
      return idCard != null ? MASK_VALUE : null;
    }
    return idCard.substring(0, 4) + "**********" + idCard.substring(idCard.length() - 4);
  }
}
