package cloud.xcan.angus.core.gm.interfaces.authentication.facade.internal.assembler;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.TokenVo;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.UserSignInVo;
import cloud.xcan.angus.spec.utils.JsonUtils;
import java.util.Map;

public class AuthenticationAssembler {

  public static UserSignInVo toLoginVo(Map<String, String> tokenResult, User user) {
    TokenVo tokenVo = JsonUtils.fromJsonObject(tokenResult, TokenVo.class);
    assert tokenVo != null;
    UserSignInVo loginVo = new UserSignInVo();
    loginVo.setAccessToken(tokenVo.getAccessToken());
    loginVo.setRefreshToken(tokenVo.getRefreshToken());
    loginVo.setTokenType(tokenVo.getTokenType());
    loginVo.setExpiresIn(tokenVo.getExpiresIn());

    if (user != null) {
      loginVo.setUser(user.toUserInfo());
    }
    return loginVo;
  }

}
