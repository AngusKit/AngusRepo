package cloud.xcan.angus.core.repo.application.cmd.access;

import cloud.xcan.angus.core.repo.domain.access.AccessToken;

public interface AccessTokenCmd {

  AccessToken create(AccessToken accessToken);

  void revoke(Long id);
}
