package cloud.xcan.angus.core.gm.infra.authentication;

import static cloud.xcan.angus.spec.http.MediaType.APPLICATION_FORM_URLENCODED;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getRequestId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static java.lang.String.format;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.CLIENT_CREDENTIALS;

import cloud.xcan.angus.api.enums.SignInType;
import cloud.xcan.angus.core.spring.SpringContextHolder;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.http.Unauthorized;
import cloud.xcan.angus.spec.experimental.BizConstant.Header;
import cloud.xcan.angus.spec.http.HttpMethod;
import cloud.xcan.angus.spec.http.HttpSender;
import cloud.xcan.angus.spec.http.HttpSender.Request;
import cloud.xcan.angus.spec.http.HttpSender.Response;
import cloud.xcan.angus.spec.http.HttpStatus;
import cloud.xcan.angus.spec.http.HttpUrlConnectionSender;
import cloud.xcan.angus.spec.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

public class OAuth2Utils {

  /**
   * 向授权服务器提交OAuth2客户端登录请求
   */
  public static Map<String, String> submitOauth2ClientSignInRequest(String clientId,
      String clientSecret, String scope) throws Throwable {
    // 构建OAuth2客户端凭证请求
    String authContent = format("client_id=%s&client_secret=%s&grant_type=%s", clientId,
        clientSecret, CLIENT_CREDENTIALS.getValue());
    if (isNotEmpty(scope)) {
      authContent = authContent + "&scope=" + scope;
    }
    return sendOauth2Request(authContent);
  }

  /**
   * 向授权服务器提交OAuth2用户登录请求
   */
  public static Map<String, String> submitOauth2UserLoginRequest(String clientId,
      String clientSecret, SignInType signinType, String userId, String account, String password,
      String scope) throws Throwable {
    // 构造OAuth2密码授权请求
    String authContent = format(
        "client_id=%s&client_secret=%s&grant_type=%s&user_id=%s&account=%s&password=%s",
        clientId, clientSecret, signinType.toOAuth2GrantType(), userId, account, password);
    if (isNotEmpty(scope)) {
      authContent = authContent + "&scope=" + scope;
    }
    return sendOauth2Request(authContent);
  }

  /**
   * 向授权服务器提交OAuth2刷新token请求
   */
  public static Map<String, String> submitOauth2RenewRequest(String clientId, String clientSecret,
      String refreshToken) throws Throwable {
    // 构造OAuth2刷新token请求
    String authContent = format("client_id=%s&client_secret=%s&grant_type=%s&refresh_token=%s",
        clientId, clientSecret, AuthorizationGrantType.REFRESH_TOKEN.getValue(), refreshToken);
    return sendOauth2Request(authContent);
  }

  /**
   * 向授权服务器发送OAuth2请求
   *
   * <p>此方法处理与OAuth2授权服务器的HTTP通信，并处理响应以生成token。</p>
   *
   * @param authContent OAuth2请求内容
   * @return 包含OAuth2响应数据的Map
   * @throws Throwable 如果请求失败
   */
  public static Map<String, String> sendOauth2Request(String authContent) throws Throwable {
    HttpSender sender = new HttpUrlConnectionSender();
    ApplicationInfo applicationInfo = SpringContextHolder.getBean(ApplicationInfo.class);
    assert applicationInfo != null;
    String tokenEndpoint = format("http://%s/oauth2/token", applicationInfo.getInstanceId());
    Response response = Request.build(tokenEndpoint, sender)
        .withMethod(HttpMethod.POST).withHeader(Header.REQUEST_ID, getRequestId())
        .withContent(APPLICATION_FORM_URLENCODED, authContent).send();
    Map<String, String> result = JsonUtils.convert(response.body(), new TypeReference<>() {
    });
    if (!response.isSuccessful()) {
      assert result != null;
      if (response.code() == HttpStatus.UNAUTHORIZED.value) {
        throw Unauthorized.of(nullSafe(result.get("error_description"), result.get("error")));
      } else {
        throw ProtocolException.of(nullSafe(result.get("error_description"), result.get("error")));
      }
    }
    return result;
  }

}
