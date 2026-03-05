package cloud.xcan.angus.core.repo.domain.reposettings;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface RepositoryGlobalSettingsRepo extends BaseRepository<RepositoryGlobalSettings, Long> {

  Optional<RepositoryGlobalSettings> findFirstByOrderByIdDesc();
}
