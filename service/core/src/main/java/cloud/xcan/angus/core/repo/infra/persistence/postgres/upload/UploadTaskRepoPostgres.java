package cloud.xcan.angus.core.repo.infra.persistence.postgres.upload;

import cloud.xcan.angus.core.repo.domain.upload.UploadTaskRepo;
import org.springframework.stereotype.Repository;

@Repository("uploadTaskRepo")
public interface UploadTaskRepoPostgres extends UploadTaskRepo {


}
