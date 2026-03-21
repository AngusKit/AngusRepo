package cloud.xcan.angus.core.gm.interfaces.user.facade;

import cloud.xcan.angus.api.gm.user.dto.TokenCreateDto;
import cloud.xcan.angus.api.gm.user.dto.TokenUpdateDto;
import cloud.xcan.angus.api.gm.user.dto.TokensQueryDto;
import cloud.xcan.angus.api.gm.user.vo.UserTokenVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.TokenQuotaVo;
import cloud.xcan.angus.remote.PageResult;

/**
 * 用户令牌门面接口
 */
public interface UserTokenFacade {

  /**
   * 创建用户令牌
   */
  UserTokenVo create(TokenCreateDto dto);

  /**
   * 更新令牌信息
   */
  UserTokenVo update(Long tokenId, TokenUpdateDto dto);

  /**
   * 撤销令牌
   */
  UserTokenVo revoke(Long tokenId);

  /**
   * 删除令牌
   */
  void delete(Long tokenId);

  /**
   * 获取令牌详情
   */
  UserTokenVo getDetail(Long tokenId);

  /**
   * 获取令牌列表
   */
  PageResult<UserTokenVo> list(TokensQueryDto dto);

  /**
   * 获取令牌配额统计
   */
  TokenQuotaVo getQuota();
}
