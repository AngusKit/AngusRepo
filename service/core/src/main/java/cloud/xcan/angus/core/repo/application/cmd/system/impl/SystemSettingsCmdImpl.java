package cloud.xcan.angus.core.repo.application.cmd.system.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.repo.application.cmd.system.SystemSettingsCmd;
import cloud.xcan.angus.core.repo.domain.system.SystemLicense;
import cloud.xcan.angus.core.repo.domain.system.SystemLicenseRepo;
import cloud.xcan.angus.core.repo.domain.system.SystemSettings;
import cloud.xcan.angus.core.repo.domain.system.SystemSettingsRepo;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class SystemSettingsCmdImpl extends CommCmd<SystemSettings, Long> implements SystemSettingsCmd {

  @Resource
  private SystemSettingsRepo systemSettingsRepo;

  @Resource
  private SystemLicenseRepo systemLicenseRepo;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public SystemSettings saveSetting(String key, String value, String valueType) {
    return new BizTemplate<SystemSettings>() {
      @Override
      protected SystemSettings process() {
        SystemSettings setting = systemSettingsRepo.findBySettingKey(key)
            .orElse(new SystemSettings());
        setting.setSettingKey(key);
        setting.setSettingValue(value);
        setting.setValueType(valueType);
        setting.setModifiedDate(LocalDateTime.now());
        systemSettingsRepo.save(setting);
        return setting;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public SystemLicense updateLicense(String licenseKey) {
    return new BizTemplate<SystemLicense>() {
      @Override
      protected SystemLicense process() {
        SystemLicense license = systemLicenseRepo.findFirstByOrderByCreatedDateDesc()
            .orElse(new SystemLicense());
        license.setLicenseKey(licenseKey);
        license.setModifiedDate(LocalDateTime.now());
        if (license.getCreatedDate() == null) {
          license.setCreatedDate(LocalDateTime.now());
        }
        systemLicenseRepo.save(license);
        return license;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<SystemSettings, Long> getRepository() {
    return this.systemSettingsRepo;
  }
}
