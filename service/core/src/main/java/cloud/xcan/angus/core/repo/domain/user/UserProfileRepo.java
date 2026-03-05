package cloud.xcan.angus.core.repo.domain.user;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface UserProfileRepo extends BaseRepository<UserProfile, Long> {

  Optional<UserProfile> findByEmail(String email);

  boolean existsByEmail(String email);
}
