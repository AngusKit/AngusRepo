package cloud.xcan.angus.core.repo.infra.persistence.mysql.format;

import cloud.xcan.angus.core.repo.domain.format.entity.DockerBlobUploadEntityRepo;
import org.springframework.stereotype.Repository;

@Repository("dockerBlobUploadEntityRepo")
public interface DockerBlobUploadEntityRepoMysql extends DockerBlobUploadEntityRepo {


}
