package cloud.xcan.angus.core.repo.domain.system;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SystemLicenseRepo extends BaseRepository<SystemLicense, Long> {

  Optional<SystemLicense> findFirstByOrderByCreatedDateDesc();
}
