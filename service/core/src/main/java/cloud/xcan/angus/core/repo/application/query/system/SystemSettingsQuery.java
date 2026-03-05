package cloud.xcan.angus.core.repo.application.query.system;

import cloud.xcan.angus.core.repo.domain.system.SystemLicense;
import cloud.xcan.angus.core.repo.domain.system.SystemSettings;
import java.util.List;
import java.util.Optional;

public interface SystemSettingsQuery {

  List<SystemSettings> findAll();

  List<SystemSettings> findByPrefix(String prefix);

  Optional<SystemSettings> findByKey(String key);

  Optional<SystemLicense> findCurrentLicense();
}
