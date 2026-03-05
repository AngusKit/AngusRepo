package cloud.xcan.angus.core.repo.application.query.security;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.repo.domain.security.ScanPolicy;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ScanPolicyQuery {
  Page<ScanPolicy> find(GenericSpecification<ScanPolicy> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);
  Optional<ScanPolicy> findById(String id);
  ScanPolicy findAndCheck(String id);
}
