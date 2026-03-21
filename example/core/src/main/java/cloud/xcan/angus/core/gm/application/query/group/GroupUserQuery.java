package cloud.xcan.angus.core.gm.application.query.group;

import cloud.xcan.angus.api.commonlink.group.GroupUser;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * 组用户查询服务接口
 */
public interface GroupUserQuery {

  /**
   * 根据组ID分页查询用户列表
   */
  Page<User> findUsers(Long groupId, GenericSpecification<User> spec, PageRequest pageable);

  /**
   * 分页查询未加入指定组的用户列表
   */
  Page<User> findUsersNotInGroup(Long groupId, GenericSpecification<User> spec,
      PageRequest pageable);

  /**
   * 统计活跃用户数量（所有组中的不重复用户数）
   */
  long countActiveUsers();

  /**
   * 根据组ID统计用户数量
   */
  Map<Long, Long> countUsersByGroupIds(List<Long> groupIds);

  /**
   * 根据组ID查找用户ID列表（从组-用户关系表）
   */
  List<Long> findUserIdsByGroupId(Long groupId);

  /**
   * 检查组-用户关系是否存在
   */
  boolean existsByGroupIdAndUserId(Long groupId, Long userId);

  /**
   * 根据用户ID列表批量查找组用户关系
   */
  List<GroupUser> findByUserIdIn(List<Long> userIds);

}
