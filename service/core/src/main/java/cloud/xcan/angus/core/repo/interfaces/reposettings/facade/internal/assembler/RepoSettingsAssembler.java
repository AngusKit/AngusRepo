package cloud.xcan.angus.core.repo.interfaces.reposettings.facade.internal.assembler;

import cloud.xcan.angus.core.repo.domain.reposettings.RepositoryGlobalSettings;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.dto.GlobalSettingsUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.vo.GlobalSettingsVo;

public class RepoSettingsAssembler {

  public static RepositoryGlobalSettings toUpdateEntity(GlobalSettingsUpdateDto dto) {
    RepositoryGlobalSettings entity = new RepositoryGlobalSettings();
    entity.setDefaultRepository(dto.getDefaultRepository());
    entity.setAnonymousAccess(dto.getAnonymousAccess());
    entity.setIndexingEnabled(dto.getIndexingEnabled());
    entity.setCompressionEnabled(dto.getCompressionEnabled());
    entity.setStorageQuotaGb(dto.getStorageQuotaGb());
    entity.setRetentionDays(dto.getRetentionDays());
    entity.setAutoCleanup(dto.getAutoCleanup());
    entity.setDeduplicationEnabled(dto.getDeduplicationEnabled());
    return entity;
  }

  public static GlobalSettingsVo toSettingsVo(RepositoryGlobalSettings entity) {
    if (entity == null) {
      return new GlobalSettingsVo();
    }
    GlobalSettingsVo vo = new GlobalSettingsVo();
    vo.setId(entity.getId());
    vo.setDefaultRepository(entity.getDefaultRepository());
    vo.setAnonymousAccess(entity.getAnonymousAccess());
    vo.setIndexingEnabled(entity.getIndexingEnabled());
    vo.setCompressionEnabled(entity.getCompressionEnabled());
    vo.setStorageQuotaGb(entity.getStorageQuotaGb());
    vo.setRetentionDays(entity.getRetentionDays());
    vo.setAutoCleanup(entity.getAutoCleanup());
    vo.setDeduplicationEnabled(entity.getDeduplicationEnabled());
    vo.setModifiedBy(entity.getModifiedBy());
    vo.setModifiedDate(entity.getModifiedDate());
    return vo;
  }
}
