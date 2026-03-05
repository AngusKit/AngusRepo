package cloud.xcan.angus.core.repo.domain.access;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AccessTokenRepo extends BaseRepository<AccessToken, Long> {

  List<AccessToken> findByRepositoryId(Long repositoryId);

  Optional<AccessToken> findByTokenHash(String tokenHash);

  long countByRepositoryId(Long repositoryId);
}
