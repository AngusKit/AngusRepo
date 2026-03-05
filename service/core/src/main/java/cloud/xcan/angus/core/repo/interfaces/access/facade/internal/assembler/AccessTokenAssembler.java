package cloud.xcan.angus.core.repo.interfaces.access.facade.internal.assembler;

import cloud.xcan.angus.core.repo.domain.access.AccessToken;
import cloud.xcan.angus.core.repo.interfaces.access.facade.dto.AccessTokenCreateDto;
import cloud.xcan.angus.core.repo.interfaces.access.facade.vo.AccessTokenVo;

public class AccessTokenAssembler {

  public static AccessToken toCreateEntity(AccessTokenCreateDto dto, Long repositoryId) {
    AccessToken entity = new AccessToken();
    entity.setRepositoryId(repositoryId);
    entity.setName(dto.getName());
    entity.setDescription(dto.getDescription());
    entity.setPermissions(dto.getPermissions());
    entity.setExpiresAt(dto.getExpiresAt());
    entity.setIpWhitelist(dto.getIpWhitelist());
    return entity;
  }

  public static AccessTokenVo toVo(AccessToken entity) {
    if (entity == null) {
      return null;
    }
    AccessTokenVo vo = new AccessTokenVo();
    vo.setId(entity.getId());
    vo.setRepositoryId(entity.getRepositoryId());
    vo.setName(entity.getName());
    vo.setDescription(entity.getDescription());
    vo.setEnabled(entity.getEnabled());
    vo.setExpiresAt(entity.getExpiresAt());
    vo.setLastUsed(entity.getLastUsed());
    vo.setUsageCount(entity.getUsageCount());
    vo.setPermissions(entity.getPermissions());
    vo.setIpWhitelist(entity.getIpWhitelist());
    vo.setCreatedBy(entity.getCreatedBy());
    vo.setCreatedDate(entity.getCreatedDate());
    return vo;
  }

  public static AccessTokenVo toCreatedVo(AccessToken entity) {
    AccessTokenVo vo = toVo(entity);
    if (vo != null) {
      vo.setToken(entity.getTokenHash());
    }
    return vo;
  }
}
