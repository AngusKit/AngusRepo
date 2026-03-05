package cloud.xcan.angus.core.repo.infra.store;

import cloud.xcan.angus.core.repo.domain.format.store.SecurityScanIntegration;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Default implementation of {@link SecurityScanIntegration}.
 *
 * <p>Provides a placeholder for future security scan integration. Currently logs
 * scan trigger events for auditing and diagnostic purposes.
 */
@Slf4j
@Component
public class DefaultSecurityScanIntegration implements SecurityScanIntegration {

  @Override
  public void triggerScanAfterUpload(String artifactId, RepositoryFormat format) {
    log.info("Security scan triggered for artifact={}, format={}", artifactId, format);
    // TODO: Integrate with external vulnerability scanning service
  }
}
