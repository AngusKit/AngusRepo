package cloud.xcan.angus.core.gm.domain;


public interface CommonConstant {

  /**
   * 允许最大添加用户令牌数量
   */
  int MAX_USER_TOKEN_QUOTA = 30;

  /**
   * 上传用户头像BizKey
   */
  String UPLOAD_AVATAR_KEY = "avatar";

  /**
   * 最大头像上传大小5MB
   */
  int MAX_AVATAR_SIZE = 5 * 1024 * 1024; // 5MB

  /**
   * 应用标签分类编码
   */
  String APPLICATION_TAG_CATEGORY_CODE = "Application";

}
