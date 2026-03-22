package cloud.xcan.angus.core.repo.application.cmd.reposettings.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.repo.application.cmd.reposettings.RepoSettingsCmd;
import cloud.xcan.angus.core.repo.domain.reposettings.RepositoryGlobalSettings;
import cloud.xcan.angus.core.repo.domain.reposettings.RepositoryGlobalSettingsRepo;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class RepoSettingsCmdImpl extends CommCmd<RepositoryGlobalSettings, Long>
    implements RepoSettingsCmd {

  @Resource
  private RepositoryGlobalSettingsRepo settingsRepo;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public RepositoryGlobalSettings updateSettings(RepositoryGlobalSettings settings) {
    return new BizTemplate<RepositoryGlobalSettings>() {
      RepositoryGlobalSettings existing;

      @Override
      protected void checkParams() {
        existing = settingsRepo.findFirstByOrderByIdDesc()
            .orElse(null);
      }

      @Override
      protected RepositoryGlobalSettings process() {
        if (existing == null) {
          settings.setModifiedDate(LocalDateTime.now());
          insert0(settings);
          return settings;
        }
        if (settings.getDefaultRepository() != null) {
          existing.setDefaultRepository(settings.getDefaultRepository());
        }
        if (settings.getAnonymousAccess() != null) {
          existing.setAnonymousAccess(settings.getAnonymousAccess());
        }
        if (settings.getIndexingEnabled() != null) {
          existing.setIndexingEnabled(settings.getIndexingEnabled());
        }
        if (settings.getCompressionEnabled() != null) {
          existing.setCompressionEnabled(settings.getCompressionEnabled());
        }
        if (settings.getStorageQuotaGb() != null) {
          existing.setStorageQuotaGb(settings.getStorageQuotaGb());
        }
        if (settings.getRetentionDays() != null) {
          existing.setRetentionDays(settings.getRetentionDays());
        }
        if (settings.getAutoCleanup() != null) {
          existing.setAutoCleanup(settings.getAutoCleanup());
        }
        if (settings.getDeduplicationEnabled() != null) {
          existing.setDeduplicationEnabled(settings.getDeduplicationEnabled());
        }
        existing.setModifiedBy(settings.getModifiedBy());
        existing.setModifiedDate(LocalDateTime.now());
        settingsRepo.save(existing);
        return existing;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<RepositoryGlobalSettings, Long> getRepository() {
    return this.settingsRepo;
  }
}
