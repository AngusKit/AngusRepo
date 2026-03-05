package cloud.xcan.angus.core.repo.domain;


public interface Constants {

  // 通用限制
  int MAX_NAME_LENGTH = 255;
  int MAX_DESC_LENGTH = 2000;
  int MAX_URL_LENGTH = 500;
  int MAX_EMAIL_LENGTH = 255;
  int MAX_TOKEN_LENGTH = 255;

  // 仓库相关
  int MAX_REPOSITORY_NAME_LENGTH = 255;
  int MAX_REPOSITORY_DESC_LENGTH = 2000;
  int MAX_REPOSITORY_URL_LENGTH = 500;

  // 团队相关
  int MAX_INVITATION_MESSAGE_LENGTH = 1000;
  int INVITATION_EXPIRE_DAYS = 7;

  // 用户相关
  int MAX_DEPARTMENT_LENGTH = 255;
  int MAX_AVATAR_SIZE = 2 * 1024 * 1024; // 2MB

  // API Token相关
  int MAX_API_TOKEN_NAME_LENGTH = 255;
  int MAX_API_TOKEN_PER_USER = 20;

  // 系统设置相关
  int MAX_SETTING_KEY_LENGTH = 255;
  int MAX_LICENSE_KEY_LENGTH = 10000;
}
