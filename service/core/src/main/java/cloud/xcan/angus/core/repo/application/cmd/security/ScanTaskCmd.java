package cloud.xcan.angus.core.repo.application.cmd.security;

import cloud.xcan.angus.core.repo.domain.security.ScanTask;

public interface ScanTaskCmd {
  ScanTask create(ScanTask scanTask);
  ScanTask update(ScanTask scanTask);
  void cancel(String id);
  void delete(String id);
}
