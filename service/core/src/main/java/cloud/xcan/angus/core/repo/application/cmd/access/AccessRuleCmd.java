package cloud.xcan.angus.core.repo.application.cmd.access;

import cloud.xcan.angus.core.repo.domain.access.AccessRule;

public interface AccessRuleCmd {

  AccessRule create(AccessRule accessRule);

  AccessRule update(AccessRule accessRule);

  void delete(Long id);
}
