package cloud.xcan.angus.core.gm.application.query.user;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.enums.UserStatus;
import cloud.xcan.angus.api.gm.user.vo.UserStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * 用户查询服务接口
 */
public interface UserQuery {

  /**
   * 根据ID查找用户并检查是否存在
   */
  User findAndCheck(Long id);

  /**
   * 根据ID列表查找用户并检查是否存在
   */
  List<User> findAndCheck(Collection<Long> ids);

  /**
   * 分页查找用户
   */
  Page<User> find(GenericSpecification<User> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  /**
   * 根据账号（手机号或邮箱）查找用户列表
   */
  List<User> findAllByAccount(String account);

  /**
   * 获取用户统计信息
   *
   * @param appCode 应用编码，可选，指定时仅统计该应用下的用户
   */
  UserStatsVo getStats(String appCode);

  /**
   * 根据邮箱查找用户
   */
  User findByEmail(String adminEmail);

  /**
   * 根据邮箱查找租户ID
   */
  Long findTenantIdByEmail(String s);

  /**
   * 根据手机号查找租户ID
   */
  Long findTenantIdByPhone(String phone);

  /**
   * 检查邮箱是否存在
   */
  boolean existsByEmail(String email);

  /**
   * 检查手机号是否存在
   */
  boolean existsByPhone(String phone);

  /**
   * 检查用户名是否存在
   */
  boolean existsByUsername(String username);

  /**
   * 检查用户名是否存在
   */
  boolean existsByUsernameAndIdNot(String username, Long id);

  /**
   * 统计用户总数
   */
  long count();

  /**
   * 根据状态统计用户数量
   */
  long countByStatus(UserStatus status);

  /**
   * 统计指定租户的用户数量
   */
  long countByTenantId(Long tenantId);

}
