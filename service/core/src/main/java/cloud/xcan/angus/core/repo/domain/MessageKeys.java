package cloud.xcan.angus.core.repo.domain;

public interface MessageKeys {

  // 仓库模块
  String REPOSITORY_NOT_FOUND = "xcm.repo.repository.not.found";
  String REPOSITORY_NAME_EXISTS = "xcm.repo.repository.name.exists";
  String REPOSITORY_STATUS_INVALID = "xcm.repo.repository.status.invalid";

  // 用户模块
  String USER_NOT_FOUND = "xcm.repo.user.not.found";
  String USER_PASSWORD_MISMATCH = "xcm.repo.user.password.mismatch";
  String USER_EMAIL_EXISTS = "xcm.repo.user.email.exists";
  String USER_TOKEN_LIMIT_EXCEEDED = "xcm.repo.user.token.limit.exceeded";

  // 团队模块
  String TEAM_MEMBER_NOT_FOUND = "xcm.repo.team.member.not.found";
  String TEAM_INVITATION_NOT_FOUND = "xcm.repo.team.invitation.not.found";
  String TEAM_INVITATION_EXPIRED = "xcm.repo.team.invitation.expired";
  String TEAM_INVITATION_ALREADY_PROCESSED = "xcm.repo.team.invitation.already.processed";
  String TEAM_INVITATION_DUPLICATE = "xcm.repo.team.invitation.duplicate";

  // 系统设置模块
  String SYSTEM_SETTING_NOT_FOUND = "xcm.repo.system.setting.not.found";
  String SYSTEM_LICENSE_INVALID = "xcm.repo.system.license.invalid";
  String SYSTEM_CONNECTION_TEST_FAILED = "xcm.repo.system.connection.test.failed";
}
