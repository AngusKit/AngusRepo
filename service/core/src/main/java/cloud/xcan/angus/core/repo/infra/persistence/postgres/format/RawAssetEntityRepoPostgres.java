package cloud.xcan.angus.core.repo.infra.persistence.postgres.format;

import cloud.xcan.angus.core.repo.domain.format.entity.RawAssetEntityRepo;
import org.springframework.stereotype.Repository;

@Repository("rawAssetEntityRepo")
public interface RawAssetEntityRepoPostgres extends RawAssetEntityRepo {
}
