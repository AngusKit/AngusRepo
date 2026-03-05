package cloud.xcan.angus.core.repo.application.cmd.cleanup;

import cloud.xcan.angus.core.repo.domain.cleanup.CleanupExecution;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupPolicy;
import java.util.List;

public interface CleanupCmd {

  CleanupPolicy create(CleanupPolicy policy);

  CleanupPolicy update(CleanupPolicy policy);

  void delete(String id);

  void deleteBatch(List<String> ids);

  void updateEnabled(String id, Boolean enabled, Long modifiedBy);

  CleanupExecution execute(String policyId);

  void cancelExecution(String executionId);
}
