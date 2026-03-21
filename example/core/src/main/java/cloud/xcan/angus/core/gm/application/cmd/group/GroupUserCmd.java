package cloud.xcan.angus.core.gm.application.cmd.group;

import java.util.Collection;
import java.util.List;

/**
 * 组用户命令服务接口
 */
public interface GroupUserCmd {

  /**
   * 批量添加组用户
   */
  int addUsers(Long groupId, List<Long> userIds);

  /**
   * 移除组用户
   */
  void removeUser(Long groupId, Long userId);

  /**
   * 批量移除组用户
   */
  void removeUsers(Long groupId, List<Long> userIds);

  /**
   * 转移组用户
   */
  int transferUsers(Long sourceGroupId, Long targetGroupId, List<Long> userIds);

  /**
   * 根据组ID删除所有用户关系
   */
  void deleteByGroupId(Long groupId);

  /**
   * 根据用户ID集合删除用户关系
   */
  void deleteByUserIds(Collection<Long> ids);
}
