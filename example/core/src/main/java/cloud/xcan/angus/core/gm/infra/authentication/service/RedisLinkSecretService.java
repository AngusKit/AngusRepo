package cloud.xcan.angus.core.gm.infra.authentication.service;


import static cloud.xcan.angus.api.commonlink.GMConstant.CACHE_EMAIL_CHECK_SECRET_PREFIX;
import static cloud.xcan.angus.api.commonlink.GMConstant.CACHE_SMS_CHECK_SECRET_PREFIX;
import static cloud.xcan.angus.api.commonlink.GMConstant.TEMPLATE_CODE_LOGIN_VERIFICATION;
import static cloud.xcan.angus.api.commonlink.GMConstant.TEMPLATE_CODE_VERIFICATION_CODE;
import static cloud.xcan.angus.core.gm.domain.TipMessage.LINK_SECRET_ILLEGAL;
import static cloud.xcan.angus.core.gm.domain.TipMessage.LINK_SECRET_TIMEOUT;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isBlank;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNull;

import cloud.xcan.angus.api.enums.SignInType;
import cloud.xcan.angus.lettucex.util.RedisService;
import cloud.xcan.angus.security.authentication.service.LinkSecretService;
import cloud.xcan.angus.spec.locale.MessageHolder;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;

public class RedisLinkSecretService implements LinkSecretService {

  @Resource
  private RedisService<String> stringRedisService;

  @Override
  public void matches(SignInType type, String userId, String linkSecret)
      throws AuthenticationException {
    if (isNull(stringRedisService)) {
      throw new AuthenticationServiceException("RedisService instance is null");
    }

    String cacheUserSecretKey = "";
    if (SignInType.SMS_CODE.equals(type)) {
      cacheUserSecretKey = String.format(CACHE_SMS_CHECK_SECRET_PREFIX,
          TEMPLATE_CODE_VERIFICATION_CODE, userId);
    } else if (SignInType.EMAIL_CODE.equals(type)) {
      cacheUserSecretKey = String.format(CACHE_EMAIL_CHECK_SECRET_PREFIX,
          TEMPLATE_CODE_LOGIN_VERIFICATION, userId);
    }
    String cacheLinkSecret = stringRedisService.get(cacheUserSecretKey);
    if (isBlank(cacheLinkSecret)) {
      throw new AuthenticationServiceException(MessageHolder.message(LINK_SECRET_TIMEOUT));
    }
    if (!StringUtils.equals(cacheLinkSecret, linkSecret)) {
      throw new AuthenticationServiceException(MessageHolder.message(LINK_SECRET_ILLEGAL));
    }
    stringRedisService.delete(cacheUserSecretKey);
  }
}
