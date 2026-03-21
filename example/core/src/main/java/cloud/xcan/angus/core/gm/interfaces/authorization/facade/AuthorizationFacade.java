package cloud.xcan.angus.core.gm.interfaces.authorization.facade;

import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationSubjectType;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationBatchCreateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationBatchDeleteDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationFindDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationRoleAddDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationBatchVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationListVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationRoleVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationStatsVo;
import cloud.xcan.angus.remote.PageResult;

public interface AuthorizationFacade {

  /**
   * 创建授权
   */
  AuthorizationDetailVo create(AuthorizationCreateDto dto);

  /**
   * 批量创建授权
   */
  AuthorizationBatchVo batchCreate(AuthorizationBatchCreateDto dto);

  /**
   * 更新授权
   */
  AuthorizationDetailVo update(Long id, AuthorizationUpdateDto dto);

  /**
   * 添加角色到授权
   */
  AuthorizationRoleVo addRoles(Long id, AuthorizationRoleAddDto dto);

  /**
   * 从授权中移除角色
   */
  AuthorizationRoleVo removeRole(Long id, Long roleId);

  /**
   * 根据授权主体添加角色到授权
   */
  AuthorizationRoleVo addRolesToAuthorizationBySubject(
      AuthorizationSubjectType subjectType, Long subjectId, AuthorizationRoleAddDto dto);

  /**
   * 根据授权主体从授权中移除角色
   */
  AuthorizationRoleVo removeRoleFromAuthorizationBySubject(
      AuthorizationSubjectType subjectType, Long subjectId, Long roleId);

  /**
   * 删除授权
   */
  void delete(Long id);

  /**
   * 批量删除授权
   */
  void batchDelete(AuthorizationBatchDeleteDto dto);

  /**
   * 获取授权详情
   */
  AuthorizationDetailVo getDetail(Long id);

  /**
   * 获取主体授权信息
   */
  AuthorizationDetailVo getSubjectAuthorization(
      AuthorizationSubjectType subjectType, Long subjectId);

  /**
   * 分页查询授权列表
   */
  PageResult<AuthorizationListVo> list(AuthorizationFindDto dto);

  /**
   * 查询用户授权列表
   */
  PageResult<AuthorizationListVo> listUsers(AuthorizationFindDto dto);

  /**
   * 查询部门授权列表
   */
  PageResult<AuthorizationListVo> listDepartments(AuthorizationFindDto dto);

  /**
   * 查询组授权列表
   */
  PageResult<AuthorizationListVo> listGroups(AuthorizationFindDto dto);

  /**
   * 获取授权统计数据
   */
  AuthorizationStatsVo getStats();
}
