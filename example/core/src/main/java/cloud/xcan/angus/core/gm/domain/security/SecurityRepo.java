package cloud.xcan.angus.core.gm.domain.security;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SecurityRepo extends BaseRepository<Security, Long> {

  List<Security> findByType(SecurityType type);

  Page<Security> findByType(SecurityType type, Pageable pageable);

  Optional<Security> findFirstByType(SecurityType type);
}
