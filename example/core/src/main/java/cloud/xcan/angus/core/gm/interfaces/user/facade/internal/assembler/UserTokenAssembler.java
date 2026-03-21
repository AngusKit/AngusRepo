package cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler;


import cloud.xcan.angus.api.gm.user.dto.TokenCreateDto;
import cloud.xcan.angus.api.gm.user.dto.TokenUpdateDto;
import cloud.xcan.angus.api.gm.user.dto.TokensQueryDto;
import cloud.xcan.angus.api.gm.user.vo.UserTokenVo;
import cloud.xcan.angus.core.gm.domain.user.UserToken;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.TokenQuotaVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

public class UserTokenAssembler {

  public static UserToken toCreateDomain(TokenCreateDto dto) {
    UserToken token = new UserToken();
    token.setName(dto.getName());
    token.setDescription(dto.getDescription());
    token.setAppCode(dto.getAppCode());
    return token;
  }

  public static UserToken toUpdateDomain(TokenUpdateDto dto) {
    UserToken token = new UserToken();
    token.setName(dto.getName());
    token.setDescription(dto.getDescription());
    return token;
  }

  public static UserTokenVo toDetailVo(UserToken token, boolean showFullToken) {
    if (token == null) {
      return null;
    }

    UserTokenVo vo = new UserTokenVo();
    vo.setId(token.getId());
    vo.setUserId(token.getUserId());
    vo.setName(token.getName());
    vo.setDescription(token.getDescription());
    vo.setAppId(token.getAppId());
    vo.setAppCode(token.getAppCode());
    vo.setScopes(token.getScopes());
    vo.setExpiresAt(token.getExpiresAt());
    vo.setStatus(token.getStatus());
    vo.setLastUsedAt(token.getLastUsedAt());
    vo.setUsageCount(token.getUsageCount());
    vo.setRevokedAt(token.getRevokedAt());

    // 令牌显示规则：创建时返回完整值，列表和详情中显示掩码
    // 注意：数据库中存储的是SHA-256哈希值，只有创建时token字段才包含原始值
    if (showFullToken) {
      // 创建时返回完整令牌值
      vo.setToken(token.getPlainToken());
    } else {
      // 列表和详情中显示掩码（根据appId生成前缀）
      vo.setToken("SHA_" + token.getToken());
    }

    // 设置审计信息
    vo.setTenantId(token.getTenantId());
    vo.setCreatedBy(token.getCreatedBy());
    vo.setCreatedDate(token.getCreatedDate());
    vo.setModifiedBy(token.getModifiedBy());
    vo.setModifiedDate(token.getModifiedDate());
    return vo;
  }

  public static UserTokenVo toListVo(UserToken token) {
    return toDetailVo(token, false);
  }

  public static TokenQuotaVo toQuotaVo(TokenQuotaVo quota) {
    if (quota == null) {
      return null;
    }
    TokenQuotaVo vo = new TokenQuotaVo();
    vo.setTotal(quota.getTotal());
    vo.setUsed(quota.getUsed());
    vo.setAvailable(quota.getAvailable());
    vo.setActiveCount(quota.getActiveCount());
    vo.setExpiredCount(quota.getExpiredCount());
    vo.setRevokedCount(quota.getRevokedCount());
    return vo;
  }

  public static GenericSpecification<UserToken> getSpecification(TokensQueryDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .matchSearchFields("name")
        .orderByFields("id", "createdDate", "name", "status")
        .build();
    return new GenericSpecification<>(filters);
  }

}
