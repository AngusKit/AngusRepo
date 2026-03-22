package cloud.xcan.angus.core.repo.application.query.system.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.repo.application.query.system.SystemSettingsQuery;
import cloud.xcan.angus.core.repo.domain.system.SystemLicense;
import cloud.xcan.angus.core.repo.domain.system.SystemLicenseRepo;
import cloud.xcan.angus.core.repo.domain.system.SystemSettings;
import cloud.xcan.angus.core.repo.domain.system.SystemSettingsRepo;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Biz
@Transactional(readOnly = true)
public class SystemSettingsQueryImpl implements SystemSettingsQuery {

  @Resource
  private SystemSettingsRepo systemSettingsRepo;

  @Resource
  private SystemLicenseRepo systemLicenseRepo;

  @Override
  public List<SystemSettings> findAll() {
    return new BizTemplate<List<SystemSettings>>() {
      @Override
      protected List<SystemSettings> process() {
        return systemSettingsRepo.findAll();
      }
    }.execute();
  }

  @Override
  public List<SystemSettings> findByPrefix(String prefix) {
    return systemSettingsRepo.findBySettingKeyStartingWith(prefix);
  }

  @Override
  public Optional<SystemSettings> findByKey(String key) {
    return systemSettingsRepo.findBySettingKey(key);
  }

  @Override
  public Optional<SystemLicense> findCurrentLicense() {
    return new BizTemplate<Optional<SystemLicense>>() {
      @Override
      protected Optional<SystemLicense> process() {
        return systemLicenseRepo.findFirstByOrderByCreatedDateDesc();
      }
    }.execute();
  }
}
