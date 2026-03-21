package cloud.xcan.angus.core.gm.application.query.authentication.impl;

import static cloud.xcan.angus.api.commonlink.client.enums.ClientSource.isOperationClientSignIn;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.domain.TipMessage.PASSWORD_IS_TOO_SHORT_T;
import static cloud.xcan.angus.spec.experimental.BizConstant.OWNER_TENANT_ID;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.oauthuser.AuthenticationUser;
import cloud.xcan.angus.core.gm.application.query.authentication.AuthenticationQuery;
import cloud.xcan.angus.core.gm.application.query.authentication.AuthenticationUserQuery;
import cloud.xcan.angus.core.gm.application.query.security.SecurityQuery;
import cloud.xcan.angus.core.gm.domain.security.model.PasswordPolicyConfig;
import cloud.xcan.angus.core.gm.infra.authentication.CaptchaService;
import cloud.xcan.angus.security.model.CustomOAuth2User;
import jakarta.annotation.Resource;
import java.util.Objects;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationQueryImpl implements AuthenticationQuery {

  @Resource
  private CaptchaService captchaService;

  @Resource
  private OAuth2AuthorizationService oauth2AuthorizationService;

  @Resource
  private AuthenticationUserQuery authenticationUserQuery;

  @Resource
  private SecurityQuery securityQuery;

  @Override
  public boolean verifyCaptcha(String captchaKey, String captcha) {
    return captchaService.verify(captchaKey, captcha);
  }

  @Override
  public AuthenticationUser findByToken(String accessToken) {
    if (accessToken == null || accessToken.isEmpty()) {
      return null;
    }
    try {
      // 查找OAuth2授权信息
      OAuth2Authorization authorization = oauth2AuthorizationService.findByToken(
          accessToken, OAuth2TokenType.ACCESS_TOKEN);
      if (authorization == null) {
        return null;
      }
      // 从principal name中获取用户名，然后查找用户ID
      String principalName = authorization.getPrincipalName();
      if (principalName == null) {
        return null;
      }
      return authenticationUserQuery.findByUsername(principalName);
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public void checkMinPasswordLengthByConfig(String password) {
    PasswordPolicyConfig config = (PasswordPolicyConfig) securityQuery.getPasswordPolicy()
        .getConfig();
    assertTrue(Objects.isNull(config)
            || password.length() >= config.getMinLength(), PASSWORD_IS_TOO_SHORT_T,
        new Object[]{password.length() >= config.getMinLength()});
  }

  @Override
  public void checkOperationPlatformLogin(CustomOAuth2User user) {
    if (nonNull(user.getClientSource()) && isOperationClientSignIn(user.getClientSource())) {
      if (!OWNER_TENANT_ID.toString().equals(user.getTenantId()) || !user.isSysAdmin()) {
        throw new InsufficientAuthenticationException(
            "Illegal access, prohibited from logging into the operation platform");
      }
    }
  }
}
