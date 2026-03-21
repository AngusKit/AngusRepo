package cloud.xcan.angus.core.gm.application.query.authorization;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.gm.domain.authorization.Authorization;
import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationSubjectType;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface AuthorizationQuery {

  /**
   * 根据ID查找授权并检查是否存在
   */
  Authorization findAndCheck(Long id);

  /**
   * 根据主体类型和主体ID查找授权并检查是否存在
   */
  Authorization getSubjectAuthorization(AuthorizationSubjectType subjectType, Long subjectId);

  /**
   * 分页查找授权
   */
  Page<Authorization> find(GenericSpecification<Authorization> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  /**
   * 获取授权统计数据
   */
  AuthorizationStatsVo getStats();

  /**
   * 统计指定应用的用户数量
   */
  long countWideUsersByApplicationId(Long id);

  /**
   * 根据ID查找授权
   */
  Authorization findById(Long id);

  /**
   * 根据状态分页查找授权
   */
  Page<Authorization> findByStatus(EnabledStatus status, Pageable pageable);

  /**
   * 统计授权总数
   */
  long count();

  /**
   * 根据状态统计授权数量
   */
  long countByStatus(EnabledStatus status);

  /**
   * 根据主体类型统计授权数量
   */
  long countBySubjectType(AuthorizationSubjectType subjectType);

  /**
   * 检查授权主体是否存在
   */
  String checkSubjectExists(AuthorizationSubjectType subjectType, Long subjectId);

  /**
   * 根据角色ID统计授权数量
   */
  long countByRoleId(Long roleId);

  /**
   * 根据角色ID查找所有授权用户
   */
  List<User> findUsersByRoleId(Long roleId);

  /**
   * 根据角色ID统计用户数
   */
  long countUsersByRoleId(Long roleId);

  /**
   * 统计至少拥有一个授权的不同用户数量
   */
  long countTotalUsers();

  /**
   * 从角色ID列表中收集所有用户ID（包括直接授权、部门授权、组授权）
   */
  Set<Long> collectUserIdsByRoleId(Long roleId);

  /**
   * 根据应用ID收集拥有该应用权限的所有用户ID
   */
  Set<Long> collectWideUserIdsByApplicationId(Long appId);

  /**
   * 根据应用编码收集拥有该应用权限的所有用户ID（包含该编码下所有版本的应用）
   */
  Set<Long> collectUserIdsByApplicationCode(String appCode);

  /**
   * 从授权ID列表中收集所有用户ID（包括直接授权、部门授权、组授权）
   */
  Set<Long> collectWideUserIdsFromAuthorizations(List<Long> authorizationIds);

  /**
   * 批量根据角色ID统计用户数
   *
   * @param roleIds 角色ID列表
   * @return Map，key为角色ID，value为用户数
   */
  Map<Long, Long> countUsersByRoleIds(Collection<Long> roleIds);

  /**
   * 设置授权角色信息
   */
  void setRoleInfo(List<Authorization> authorizations);

  /**
   * 批量计算授权记录对应的授权人数 性能优化：按主体类型分组，批量查询用户数，避免N+1查询问题
   */
  void setSubjectUserCounts(List<Authorization> authorizations);
}
