package cloud.xcan.angus.core.gm.application.cmd.authorization;

import cloud.xcan.angus.core.gm.domain.authorization.Authorization;
import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationSubjectType;
import java.util.List;

public interface AuthorizationCmd {

  /**
   * 创建授权
   */
  Authorization create(Authorization authorization);

  /**
   * 更新存在的授权
   */
  Authorization update(Authorization authorization);

  /**
   * 添加角色到授权
   */
  Authorization addRoles(Long authorizationId, List<Long> roleIds);

  /**
   * 删除授权从角色
   */
  Authorization removeRole(Long authorizationId, Long roleId);

  /**
   * 删除授权
   */
  void delete(Long id);

  /**
   * 批量删除授权
   */
  void batchDelete(List<Long> authorizationIds);

  /**
   * 根据角色ID删除授权
   */
  void deleteByRoleId(Long roleId);

  /**
   * 根据角色ID列表删除授权
   */
  void deleteByRoleIdIn(List<Long> roleIds);

  /**
   * 根据主体类型和主体ID删除授权
   */
  void deleteBySubjectTypeAndId(AuthorizationSubjectType subjectType, Long subjectId);
}
