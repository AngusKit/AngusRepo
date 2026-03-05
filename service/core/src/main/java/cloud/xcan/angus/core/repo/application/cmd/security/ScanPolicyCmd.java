package cloud.xcan.angus.core.repo.application.cmd.security;

import cloud.xcan.angus.core.repo.domain.security.ScanPolicy;
import java.util.List;

public interface ScanPolicyCmd {
  ScanPolicy create(ScanPolicy policy);
  ScanPolicy update(ScanPolicy policy);
  void delete(String id);
  void updateEnabled(String id, Boolean enabled, Long modifiedBy);
}
