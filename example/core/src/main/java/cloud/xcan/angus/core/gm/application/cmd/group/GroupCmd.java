package cloud.xcan.angus.core.gm.application.cmd.group;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.group.Group;

public interface GroupCmd {

  /**
   * 创建组
   */
  Group create(Group group);

  /**
   * 更新组
   */
  Group update(Group group);

  /**
   * 更新组状态
   */
  Group updateStatus(Long id, EnabledStatus status);

  /**
   * 更新组负责人
   */
  Group updateOwner(Long groupId, Long ownerId);

  /**
   * 删除组
   */
  void delete(Long id);

  /**
   * 根据租户ID删除所有用户组
   */
  void deleteByTenantId(Long tenantId);
}
