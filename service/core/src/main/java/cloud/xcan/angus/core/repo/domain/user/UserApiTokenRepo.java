package cloud.xcan.angus.core.repo.domain.user;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface UserApiTokenRepo extends BaseRepository<UserApiToken, Long> {

  List<UserApiToken> findByUserId(Long userId);

  Optional<UserApiToken> findByTokenHash(String tokenHash);

  long countByUserId(Long userId);
}
