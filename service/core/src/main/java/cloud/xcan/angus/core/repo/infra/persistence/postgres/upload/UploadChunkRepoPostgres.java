package cloud.xcan.angus.core.repo.infra.persistence.postgres.upload;

import cloud.xcan.angus.core.repo.domain.upload.UploadChunkRepo;
import org.springframework.stereotype.Repository;

@Repository("uploadChunkRepo")
public interface UploadChunkRepoPostgres extends UploadChunkRepo {


}
