package cloud.xcan.angus.core.repo.application.cmd.reposettings;

import cloud.xcan.angus.core.repo.domain.reposettings.RepositoryGlobalSettings;

public interface RepoSettingsCmd {

  RepositoryGlobalSettings updateSettings(RepositoryGlobalSettings settings);
}
