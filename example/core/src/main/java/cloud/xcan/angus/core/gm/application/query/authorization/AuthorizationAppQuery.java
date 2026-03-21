package cloud.xcan.angus.core.gm.application.query.authorization;

import cloud.xcan.angus.api.commonlink.application.Application;
import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationSubjectType;
import java.util.List;

/**
 * 根据授主体（用户/部门/组）查询应用和菜单
 */
public interface AuthorizationAppQuery {

  /**
   * 获取授权主体的应用列表
   */
  List<Application> subjectAppList(AuthorizationSubjectType subjectType, Long subjectId,
      boolean joinMenu, boolean onlyEnabled);

  /**
   * 获取授权主体的应用功能列表
   */
  Application subjectAppList(AuthorizationSubjectType subjectType, Long subjectId,
      String appIdOrCode, String editionType, boolean joinMenu, boolean onlyEnabled);

}
