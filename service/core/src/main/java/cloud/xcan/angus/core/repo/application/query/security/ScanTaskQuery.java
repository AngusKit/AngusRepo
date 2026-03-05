package cloud.xcan.angus.core.repo.application.query.security;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.repo.domain.security.ScanTask;
import cloud.xcan.angus.core.repo.interfaces.security.facade.vo.ScanStatisticsVo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ScanTaskQuery {
  Page<ScanTask> find(GenericSpecification<ScanTask> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);
  Optional<ScanTask> findById(String id);
  ScanTask findAndCheck(String id);
  ScanStatisticsVo getStatistics();
}
