package cloud.xcan.angus.core.gm.infra.persistence.postgres.tag;

import cloud.xcan.angus.core.gm.domain.tag.TagRepo;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepoPostgres extends TagRepo {
  // Spring Data JPA will implement methods automatically
}
