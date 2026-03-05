package cloud.xcan.angus.core.repo.application.cmd.system;

import cloud.xcan.angus.core.repo.domain.system.SystemLicense;
import cloud.xcan.angus.core.repo.domain.system.SystemSettings;

public interface SystemSettingsCmd {

  SystemSettings saveSetting(String key, String value, String valueType);

  SystemLicense updateLicense(String licenseKey);
}
