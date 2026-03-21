package cloud.xcan.angus.core.gm.infra.eureka.impl;

import cloud.xcan.angus.api.commonlink.setting.eureka.EurekaConfig;
import cloud.xcan.angus.core.gm.infra.eureka.EurekaClientService;
import cloud.xcan.angus.core.gm.infra.eureka.dto.EurekaApplication;
import cloud.xcan.angus.core.gm.infra.eureka.dto.EurekaApplications;
import cloud.xcan.angus.core.gm.infra.eureka.dto.EurekaInstanceInfo;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class EurekaClientServiceImpl implements EurekaClientService {

  private RestTemplate createRestTemplate(EurekaConfig config) {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    Integer connectTimeout = config.getConnectTimeout();
    Integer readTimeout = config.getReadTimeout();
    if (connectTimeout != null && connectTimeout > 0) {
      factory.setConnectTimeout(connectTimeout);
    } else {
      factory.setConnectTimeout(5000);
    }
    if (readTimeout != null && readTimeout > 0) {
      factory.setReadTimeout(readTimeout);
    } else {
      factory.setReadTimeout(10000);
    }
    return new RestTemplate(factory);
  }

  @Override
  public EurekaApplications getApplications(EurekaConfig config) {
    try {
      RestTemplate restTemplate = createRestTemplate(config);
      String url = normalizeUrl(config) + "/apps";
      HttpHeaders headers = createHeaders(config);
      headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));

      HttpEntity<String> entity = new HttpEntity<>(headers);
      ResponseEntity<EurekaApplications> response = restTemplate.exchange(url, HttpMethod.GET,
          entity, EurekaApplications.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        return response.getBody();
      }
      return new EurekaApplications();
    } catch (Exception e) {
      throw new RuntimeException("获取Eureka应用列表失败: " + e.getMessage(), e);
    }
  }

  @Override
  public EurekaApplication getApplication(EurekaConfig config, String appName) {
    try {
      RestTemplate restTemplate = createRestTemplate(config);
      String url = normalizeUrl(config) + "/apps/" + appName.toUpperCase();
      HttpHeaders headers = createHeaders(config);
      headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));

      HttpEntity<String> entity = new HttpEntity<>(headers);
      ResponseEntity<EurekaApplication> response = restTemplate.exchange(url, HttpMethod.GET,
          entity, EurekaApplication.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        return response.getBody();
      }
      return null;
    } catch (Exception e) {
      throw new RuntimeException("获取Eureka应用信息失败: " + e.getMessage(), e);
    }
  }

  @Override
  public EurekaInstanceInfo getInstance(EurekaConfig config, String appName,
      String instanceId) {
    try {
      RestTemplate restTemplate = createRestTemplate(config);
      String url = normalizeUrl(config) + "/apps/" + appName.toUpperCase() + "/" + instanceId;
      HttpHeaders headers = createHeaders(config);
      headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));

      HttpEntity<String> entity = new HttpEntity<>(headers);
      ResponseEntity<EurekaInstanceInfo> response = restTemplate.exchange(url, HttpMethod.GET,
          entity, EurekaInstanceInfo.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        return response.getBody();
      }
      return null;
    } catch (Exception e) {
      throw new RuntimeException("获取Eureka实例信息失败: " + e.getMessage(), e);
    }
  }

  @Override
  public void updateInstanceStatus(EurekaConfig config, String appName, String instanceId,
      String status) {
    try {
      RestTemplate restTemplate = createRestTemplate(config);
      String url = normalizeUrl(config) + "/apps/" + appName.toUpperCase() + "/" + instanceId
          + "/status?value=" + status;
      HttpHeaders headers = createHeaders(config);

      HttpEntity<String> entity = new HttpEntity<>(headers);
      restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
    } catch (RestClientException e) {
      throw new RuntimeException("更新Eureka实例状态失败: " + e.getMessage(), e);
    }
  }

  @Override
  public Map<String, Object> getInstanceHealth(EurekaConfig config, String appName,
      String instanceId) {
    try {
      RestTemplate restTemplate = createRestTemplate(config);
      String url = normalizeUrl(config) + "/apps/" + appName.toUpperCase() + "/" + instanceId
          + "/health";
      HttpHeaders headers = createHeaders(config);
      headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));

      HttpEntity<String> entity = new HttpEntity<>(headers);
      ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.GET,
          entity, new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {
          });

      if (response.getStatusCode().is2xxSuccessful()) {
        return response.getBody();
      }
      return new HashMap<>();
    } catch (Exception e) {
      throw new RuntimeException("获取Eureka实例健康状态失败: " + e.getMessage(), e);
    }
  }

  @Override
  public boolean testConnection(EurekaConfig config) {
    try {
      RestTemplate restTemplate = createRestTemplate(config);
      String url = normalizeUrl(config) + "/apps";
      HttpHeaders headers = createHeaders(config);
      headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));

      HttpEntity<String> entity = new HttpEntity<>(headers);
      ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity,
          String.class);
      return response.getStatusCode().is2xxSuccessful();
    } catch (Exception e) {
      return false;
    }
  }

  private HttpHeaders createHeaders(EurekaConfig config) {
    HttpHeaders headers = new HttpHeaders();
    if (Boolean.TRUE.equals(config.getEnableAuth()) && config.getUsername() != null
        && config.getPassword() != null && !config.getUsername().isEmpty()) {
      String auth = config.getUsername() + ":" + config.getPassword();
      byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
      String authHeader = "Basic " + new String(encodedAuth);
      headers.set("Authorization", authHeader);
    }
    return headers;
  }

  private String normalizeUrl(EurekaConfig config) {
    String url = config.getServiceUrl();
    if (url == null || url.isEmpty()) {
      url = Boolean.TRUE.equals(config.getEnableSsl()) ? "https://localhost:1806/eureka"
          : "http://localhost:1806/eureka";
      return url;
    }
    url = url.trim();
    if (Boolean.TRUE.equals(config.getEnableSsl()) && url.startsWith("http://")) {
      url = url.replaceFirst("http://", "https://");
    } else if (!Boolean.TRUE.equals(config.getEnableSsl()) && url.startsWith("https://")) {
      url = url.replaceFirst("https://", "http://");
    }
    if (url.endsWith("/")) {
      url = url.substring(0, url.length() - 1);
    }
    if (!url.endsWith("/eureka")) {
      if (!url.endsWith("/")) {
        url += "/";
      }
      url += "eureka";
    }
    return url;
  }
}
