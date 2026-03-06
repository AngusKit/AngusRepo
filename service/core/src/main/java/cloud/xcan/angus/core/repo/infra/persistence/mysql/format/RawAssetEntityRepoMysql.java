package cloud.xcan.angus.core.repo.infra.persistence.mysql.format;

import cloud.xcan.angus.core.repo.domain.format.entity.RawAssetEntityRepo;
import org.springframework.stereotype.Repository;

@Repository("rawAssetEntityRepo")
public interface RawAssetEntityRepoMysql extends RawAssetEntityRepo {
}
