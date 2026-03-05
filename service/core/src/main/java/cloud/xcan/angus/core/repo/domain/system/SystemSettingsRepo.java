package cloud.xcan.angus.core.repo.domain.system;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SystemSettingsRepo extends BaseRepository<SystemSettings, Long> {

  Optional<SystemSettings> findBySettingKey(String settingKey);

  List<SystemSettings> findBySettingKeyStartingWith(String prefix);

  boolean existsBySettingKey(String settingKey);
}
