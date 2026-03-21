package cloud.xcan.angus.core.gm.interfaces.user.facade.internal;

import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;

import cloud.xcan.angus.api.gm.user.dto.TokenCreateDto;
import cloud.xcan.angus.api.gm.user.dto.TokenUpdateDto;
import cloud.xcan.angus.api.gm.user.dto.TokensQueryDto;
import cloud.xcan.angus.api.gm.user.vo.UserTokenVo;
import cloud.xcan.angus.core.gm.application.cmd.user.UserTokenCmd;
import cloud.xcan.angus.core.gm.application.query.user.UserTokenQuery;
import cloud.xcan.angus.core.gm.domain.user.UserToken;
import cloud.xcan.angus.core.gm.interfaces.user.facade.UserTokenFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserTokenAssembler;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.TokenQuotaVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class UserTokenFacadeImpl implements UserTokenFacade {

  @Resource
  private UserTokenCmd userTokenCmd;

  @Resource
  private UserTokenQuery userTokenQuery;

  @Override
  public UserTokenVo create(TokenCreateDto dto) {
    UserToken token = UserTokenAssembler.toCreateDomain(dto);
    UserToken saved = userTokenCmd.create(getUserId(), token,
        dto.getScopes(), dto.getExpiresInDays(), dto.getPassword());
    return UserTokenAssembler.toDetailVo(saved, true);
  }

  @Override
  public UserTokenVo update(Long tokenId, TokenUpdateDto dto) {
    UserToken token = UserTokenAssembler.toUpdateDomain(dto);
    UserToken saved = userTokenCmd.update(getUserId(), tokenId, token);
    return UserTokenAssembler.toDetailVo(saved, false);
  }

  @Override
  public UserTokenVo revoke(Long tokenId) {
    UserToken saved = userTokenCmd.revoke(getUserId(), tokenId);
    return UserTokenAssembler.toDetailVo(saved, false);
  }

  @Override
  public void delete(Long tokenId) {
    userTokenCmd.delete(getUserId(), tokenId);
  }

  @Override
  public UserTokenVo getDetail(Long tokenId) {
    UserToken token = userTokenQuery.findAndCheck(getUserId(), tokenId);
    return UserTokenAssembler.toDetailVo(token, false);
  }

  @Override
  public PageResult<UserTokenVo> list(TokensQueryDto dto) {
    Page<UserToken> page = userTokenQuery.findByUserId(getUserId(),
        UserTokenAssembler.getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, UserTokenAssembler::toListVo);
  }

  @Override
  public TokenQuotaVo getQuota() {
    TokenQuotaVo quota = userTokenQuery.getQuota(getUserId());
    return UserTokenAssembler.toQuotaVo(quota);
  }
}
