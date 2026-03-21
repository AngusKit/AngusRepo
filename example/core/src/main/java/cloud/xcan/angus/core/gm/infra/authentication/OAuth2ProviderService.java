package cloud.xcan.angus.core.gm.infra.authentication;

import static cloud.xcan.angus.spec.http.MediaType.APPLICATION_FORM_URLENCODED;

import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.social.GitHubSocial;
import cloud.xcan.angus.api.commonlink.setting.social.GoogleSocial;
import cloud.xcan.angus.api.commonlink.setting.social.Social;
import cloud.xcan.angus.api.commonlink.setting.social.WeChatSocial;
import cloud.xcan.angus.api.manager.SettingManager;
import cloud.xcan.angus.core.gm.domain.user.enums.OAuthProvider;
import cloud.xcan.angus.spec.http.HttpMethod;
import cloud.xcan.angus.spec.http.HttpSender;
import cloud.xcan.angus.spec.http.HttpSender.Request;
import cloud.xcan.angus.spec.http.HttpSender.Response;
import cloud.xcan.angus.spec.http.HttpUrlConnectionSender;
import cloud.xcan.angus.spec.utils.JsonUtils;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * OAuth2第三方登录服务 负责处理微信、GitHub、Google等第三方登录
 */
@Slf4j
@Service
public class OAuth2ProviderService {

  private final SettingManager settingManager;
  private final HttpSender httpSender;

  public OAuth2ProviderService(SettingManager settingManager) {
    this.settingManager = settingManager;
    this.httpSender = new HttpUrlConnectionSender();
  }

  /**
   * 获取OAuth配置
   */
  public OAuthConfig getOAuthConfig(OAuthProvider provider) {
    var setting = settingManager.getCachedSetting(SettingKey.SOCIAL);
    Social social = setting.getSocial();
    if (social == null) {
      throw new IllegalArgumentException("未配置第三方登录");
    }

    return switch (provider) {
      case WECHAT -> {
        WeChatSocial weChatSocial = social.getWeChatSocial();
        if (weChatSocial == null || weChatSocial.check()) {
          throw new IllegalArgumentException("微信登录配置不完整");
        }
        yield new OAuthConfig(weChatSocial.getAppId(), weChatSocial.getSecret(),
            weChatSocial.getCallback(), weChatSocial.getCodeUrl(), weChatSocial.getUserInfoUrl());
      }
      case GITHUB -> {
        GitHubSocial gitHubSocial = social.getGitHubSocial();
        if (gitHubSocial == null || gitHubSocial.check()) {
          throw new IllegalArgumentException("GitHub登录配置不完整");
        }
        yield new OAuthConfig(gitHubSocial.getAppId(), gitHubSocial.getSecret(),
            gitHubSocial.getCallback(), gitHubSocial.getCodeUrl(), gitHubSocial.getUserInfoUrl());
      }
      case GOOGLE -> {
        GoogleSocial googleSocial = social.getGoogleSocial();
        if (googleSocial == null || googleSocial.check()) {
          throw new IllegalArgumentException("Google登录配置不完整");
        }
        yield new OAuthConfig(googleSocial.getAppId(), googleSocial.getSecret(),
            googleSocial.getCallback(), googleSocial.getCodeUrl(), googleSocial.getUserInfoUrl());
      }
    };
  }

  /**
   * 使用code换取access_token
   */
  public String exchangeAccessToken(OAuthProvider provider, String code, OAuthConfig config) {
    try {
      String tokenUrl = getTokenUrl(provider, config);
      MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

      switch (provider) {
        case WECHAT -> {
          params.add("appid", config.getAppId());
          params.add("secret", config.getSecret());
          params.add("code", code);
          params.add("grant_type", "authorization_code");
        }
        case GITHUB -> {
          params.add("client_id", config.getAppId());
          params.add("client_secret", config.getSecret());
          params.add("code", code);
          params.add("redirect_uri", config.getCallback());
        }
        case GOOGLE -> {
          params.add("client_id", config.getAppId());
          params.add("client_secret", config.getSecret());
          params.add("code", code);
          params.add("grant_type", "authorization_code");
          params.add("redirect_uri", config.getCallback());
        }
      }

      // 将 MultiValueMap 转换为 URL 编码的字符串
      StringBuilder formData = new StringBuilder();
      params.forEach((key, values) -> {
        for (String value : values) {
          if (!formData.isEmpty()) {
            formData.append("&");
          }
          formData.append(URLEncoder.encode(key, StandardCharsets.UTF_8))
              .append("=")
              .append(URLEncoder.encode(value, StandardCharsets.UTF_8));
        }
      });

      Response response = Request.build(tokenUrl, httpSender)
          .withMethod(HttpMethod.POST)
          .withContent(APPLICATION_FORM_URLENCODED, formData.toString())
          .send();

      if (!response.isSuccessful()) {
        throw new RuntimeException("获取access_token失败: " + response.body());
      }

      @SuppressWarnings("unchecked")
      Map<String, Object> result = JsonUtils.fromJson(response.body(), Map.class);

      // 微信返回的是JSON字符串，需要特殊处理
      if (provider == OAuthProvider.WECHAT) {
        String accessToken = (String) result.get("access_token");
        String openid = (String) result.get("openid");
        if (accessToken == null || openid == null) {
          throw new RuntimeException("微信登录失败: " + response.body());
        }
        // 微信需要同时返回accessToken和openid
        return accessToken + "|" + openid;
      }

      String accessToken = (String) result.get("access_token");
      if (accessToken == null) {
        // GitHub返回的token字段名可能不同
        accessToken = (String) result.get("token");
      }
      if (accessToken == null) {
        throw new RuntimeException("未找到access_token: " + response.body());
      }
      return accessToken;
    } catch (Throwable e) {
      log.error("交换access_token失败", e);
      throw new RuntimeException("交换access_token失败: " + e.getMessage(), e);
    }
  }

  /**
   * 获取用户信息
   */
  public OAuthUserInfo getUserInfo(OAuthProvider provider, String accessTokenWithOpenid,
      OAuthConfig config) {
    try {
      // 微信的accessToken包含openid，需要分离
      String accessToken = accessTokenWithOpenid;
      String openid = null;
      if (provider == OAuthProvider.WECHAT && accessTokenWithOpenid.contains("|")) {
        String[] parts = accessTokenWithOpenid.split("\\|", 2);
        accessToken = parts[0];
        openid = parts[1];
      }

      String userInfoUrl = getUserInfoUrl(provider, accessToken, openid, config);

      Request.Builder requestBuilder = Request.build(userInfoUrl, httpSender)
          .withMethod(HttpMethod.GET);

      // 设置 Authorization header（GitHub 和 Google 需要）
      if (provider == OAuthProvider.GITHUB || provider == OAuthProvider.GOOGLE) {
        requestBuilder.withHeader("Authorization", "Bearer " + accessToken);
      }

      Response response = requestBuilder.send();

      if (!response.isSuccessful()) {
        throw new RuntimeException("获取用户信息失败: " + response.body());
      }

      @SuppressWarnings("unchecked")
      Map<String, Object> userInfoMap = JsonUtils.fromJson(response.body(), Map.class);

      return switch (provider) {
        case WECHAT -> {
          String finalOpenid = openid != null ? openid : (String) userInfoMap.get("openid");
          String nickname = (String) userInfoMap.get("nickname");
          String headimgurl = (String) userInfoMap.get("headimgurl");
          yield new OAuthUserInfo(finalOpenid, nickname, null, headimgurl, provider);
        }
        case GITHUB -> {
          String id = String.valueOf(userInfoMap.get("id"));
          String login = (String) userInfoMap.get("login");
          String name = (String) userInfoMap.get("name");
          String avatarUrl = (String) userInfoMap.get("avatar_url");
          String email = (String) userInfoMap.get("email");
          yield new OAuthUserInfo(id, name != null ? name : login, email, avatarUrl, provider);
        }
        case GOOGLE -> {
          String sub = (String) userInfoMap.get("sub");
          String name = (String) userInfoMap.get("name");
          String email = (String) userInfoMap.get("email");
          String picture = (String) userInfoMap.get("picture");
          yield new OAuthUserInfo(sub, name, email, picture, provider);
        }
      };
    } catch (Throwable e) {
      log.error("获取用户信息失败", e);
      throw new RuntimeException("获取用户信息失败: " + e.getMessage(), e);
    }
  }

  private String getTokenUrl(OAuthProvider provider, OAuthConfig config) {
    return switch (provider) {
      case WECHAT -> config.getCodeUrl(); // 微信的codeUrl就是tokenUrl
      case GITHUB -> config.getCodeUrl() != null
          ? config.getCodeUrl() : "https://github.com/login/oauth/access_token";
      case GOOGLE -> config.getCodeUrl() != null
          ? config.getCodeUrl() : "https://oauth2.googleapis.com/token";
    };
  }

  private String getUserInfoUrl(OAuthProvider provider, String accessToken, String openid,
      OAuthConfig config) {
    if (config.getUserInfoUrl() != null) {
      String url = config.getUserInfoUrl();
      if (provider == OAuthProvider.WECHAT) {
        // 微信需要将access_token和openid作为参数
        if (openid != null) {
          return url + "?access_token=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8)
              + "&openid=" + URLEncoder.encode(openid, StandardCharsets.UTF_8);
        }
        return url + "?access_token=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
      }
      return url;
    }

    return switch (provider) {
      case WECHAT -> {
        if (openid != null) {
          yield "https://api.weixin.qq.com/sns/userinfo?access_token="
              + URLEncoder.encode(accessToken, StandardCharsets.UTF_8)
              + "&openid=" + URLEncoder.encode(openid, StandardCharsets.UTF_8);
        }
        yield "https://api.weixin.qq.com/sns/userinfo?access_token="
            + URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
      }
      case GITHUB -> "https://api.github.com/user";
      case GOOGLE -> "https://www.googleapis.com/oauth2/v2/userinfo";
    };
  }

  /**
   * OAuth配置信息
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class OAuthConfig {

    private String appId;
    private String secret;
    private String callback;
    private String codeUrl;
    private String userInfoUrl;
  }

  /**
   * OAuth用户信息
   */
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class OAuthUserInfo {

    private String openId;
    private String nickname;
    private String email;
    private String avatar;
    private OAuthProvider provider;
  }
}
