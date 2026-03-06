package cloud.xcan.angus.core.repo.domain.format.store;

import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;

/**
 * Security scan integration interface.
 * Provides an abstraction for triggering security scans on uploaded artifacts.
 */
public interface SecurityScanIntegration {

  /**
   * Trigger a security scan after an artifact is uploaded.
   *
   * @param artifactId the unique identifier of the uploaded artifact
   * @param format     the repository format of the artifact
   */
  void triggerScanAfterUpload(String artifactId, RepositoryFormat format);
}
