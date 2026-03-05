package cloud.xcan.angus.core.repo.application.query.upload;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.repo.domain.upload.UploadTask;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.vo.UploadStatisticsVo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface UploadQuery {

  Page<UploadTask> find(GenericSpecification<UploadTask> spec, PageRequest pageable);

  Optional<UploadTask> findById(Long id);

  UploadTask findAndCheck(Long id);

  UploadStatisticsVo getStatistics();
}
