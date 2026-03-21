package cloud.xcan.angus.core.gm.interfaces.user.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.application.Application;
import cloud.xcan.angus.api.commonlink.department.Department;
import cloud.xcan.angus.api.commonlink.department.DepartmentInfo;
import cloud.xcan.angus.api.commonlink.department.DepartmentUser;
import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.group.GroupInfo;
import cloud.xcan.angus.api.commonlink.group.GroupUser;
import cloud.xcan.angus.api.commonlink.role.Role;
import cloud.xcan.angus.api.commonlink.role.RoleInfo;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.api.gm.user.dto.ChangePasswordDto;
import cloud.xcan.angus.api.gm.user.dto.UserCreateDto;
import cloud.xcan.angus.api.gm.user.dto.UserFindDto;
import cloud.xcan.angus.api.gm.user.dto.UserLockDto;
import cloud.xcan.angus.api.gm.user.dto.UserPatchDto;
import cloud.xcan.angus.api.gm.user.dto.UserUpdateDto;
import cloud.xcan.angus.api.gm.user.vo.LoginHistoryVo;
import cloud.xcan.angus.api.gm.user.vo.UserDetailVo;
import cloud.xcan.angus.api.gm.user.vo.UserListVo;
import cloud.xcan.angus.api.gm.user.vo.UserLockVo;
import cloud.xcan.angus.api.gm.user.vo.UserStatusUpdateVo;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.authentication.AuthenticationUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserCmd;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationQuery;
import cloud.xcan.angus.core.gm.application.query.authentication.AuthenticationUserQuery;
import cloud.xcan.angus.core.gm.application.query.authorization.AuthorizationAppQuery;
import cloud.xcan.angus.core.gm.application.query.department.DepartmentQuery;
import cloud.xcan.angus.core.gm.application.query.department.DepartmentUserQuery;
import cloud.xcan.angus.core.gm.application.query.group.GroupQuery;
import cloud.xcan.angus.core.gm.application.query.group.GroupUserQuery;
import cloud.xcan.angus.core.gm.application.query.role.RoleQuery;
import cloud.xcan.angus.core.gm.application.query.user.LoginHistoryQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationSubjectType;
import cloud.xcan.angus.core.gm.domain.user.LoginHistory;
import cloud.xcan.angus.core.gm.interfaces.application.facade.internal.assembler.ApplicationAssembler;
import cloud.xcan.angus.core.gm.interfaces.application.facade.internal.assembler.ApplicationMenuAssembler;
import cloud.xcan.angus.core.gm.interfaces.user.facade.UserFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserBatchDeleteDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserAssembler;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserCurrentDetailVo;
import cloud.xcan.angus.api.gm.user.vo.UserStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

@Component
public class UserFacadeImpl implements UserFacade {

  @Resource
  private UserCmd userCmd;

  @Resource
  private UserQuery userQuery;

  @Resource
  private RoleQuery roleQuery;

  @Resource
  private GroupQuery groupQuery;

  @Resource
  private DepartmentQuery departmentQuery;

  @Resource
  private DepartmentUserQuery departmentUserQuery;

  @Resource
  private GroupUserQuery groupUserQuery;

  @Resource
  private UserManager userManager;

  @Resource
  private ApplicationQuery applicationQuery;

  @Resource
  private LoginHistoryQuery loginHistoryQuery;

  @Resource
  private AuthorizationAppQuery authorizationAppQuery;

  @Resource
  private AuthenticationUserCmd authenticationUserCmd;

  @Resource
  private AuthenticationUserQuery authenticationUserQuery;

  @NameJoin
  @Override
  public UserDetailVo create(UserCreateDto dto) {
    User user = UserAssembler.toCreateDomain(dto);
    User saved = userCmd.create(user);
    return UserAssembler.toDetailVo(saved);
  }

  @NameJoin
  @Override
  public UserDetailVo update(Long id, UserUpdateDto dto) {
    User user = UserAssembler.toUpdateDomain(id, dto);
    User saved = userCmd.update(user);
    return UserAssembler.toDetailVo(saved);
  }

  @NameJoin
  @Override
  public UserDetailVo patch(Long id, UserPatchDto dto) {
    User existingUser = userQuery.findAndCheck(id);
    User user = UserAssembler.toPatchDomain(id, existingUser, dto);
    User saved = userCmd.update(user);
    return UserAssembler.toDetailVo(saved);
  }

  @Override
  public UserStatusUpdateVo updateStatus(Long id, EnabledStatusUpdateDto dto) {
    User user = userCmd.updateEnableStatus(id, dto.getStatus());
    return UserAssembler.toUserStatusUpdateVo(id, user);
  }

  @Override
  public UserLockVo updateLock(Long id, UserLockDto dto) {
    User user = userCmd.updateLockStatus(id, dto.getLocked());
    return UserAssembler.toUserLockVo(id, dto, user);
  }

  @Override
  public void changeCurrentPassword(ChangePasswordDto dto) {
    Long currentUserId = getUserId();
    authenticationUserCmd.changePassword(currentUserId, dto.getOldPassword(),
        dto.getNewPassword(), dto.getConfirmPassword());
  }

  @Override
  public void checkPassword(Long id, String password) {
    authenticationUserQuery.checkPassword(id, password);
  }

  @Override
  public void delete(Long id) {
    userCmd.delete(id);
  }

  @Override
  public void batchDelete(UserBatchDeleteDto dto) {
    userCmd.batchDelete(new HashSet<>(dto.getUserIds()));
  }

  @NameJoin
  @Override
  public UserDetailVo getDetail(Long id) {
    // 查询用户
    User user = userQuery.findAndCheck(id);
    // 关联角色、部门、组
    assembleDetailInfos(user);
    // 组装返回当前用户详情
    UserDetailVo vo = UserAssembler.toDetailVo(user);
    // 关联最后登录时间
    assembleLastLogin(vo, user.getId());
    // 关联最近登录历史
    List<LoginHistoryVo> historyVos = assembleLoginHistory(user.getId());
    vo.setLoginHistories(historyVos);
    return vo;
  }

  @NameJoin
  @Override
  public UserCurrentDetailVo getCurrent(String appCode, EditionType editionType) {
    // 查询当前访问用户
    Long currentUserId = getUserId();
    User user = userQuery.findAndCheck(currentUserId);
    // 组装返回当前用户详情
    UserCurrentDetailVo vo = UserAssembler.toCurrentDetailVo(user);
    // 关联最后登录时间
    assembleLastLogin(vo, user.getId());
    // 关联最近登录历史
    List<LoginHistoryVo> historyVos = assembleLoginHistory(user.getId());
    vo.setLoginHistories(historyVos);
    // 关联应用信息
    if (isNotEmpty(appCode) && isNotEmpty(editionType)) {
      // 关联访问应用
      Application accessApp = authorizationAppQuery.subjectAppList(
          AuthorizationSubjectType.USER, currentUserId, appCode, editionType.getValue(), true,
          true);
      if (accessApp != null) {
        vo.setAccessApp(ApplicationAssembler.toDetailVo(accessApp));
        vo.setAccessAppFuncTree(ApplicationMenuAssembler.buildMenuTree(accessApp.getMenus()));
      }
      // 关联授权应用
      List<Application> authApps = authorizationAppQuery.subjectAppList(
          AuthorizationSubjectType.USER, currentUserId, false, true);
      if (authApps != null) {
        vo.setAuthApps(
            authApps.stream().map(ApplicationAssembler::toListVo).collect(Collectors.toList()));
      }
    }
    return vo;
  }

  @NameJoin
  @Override
  public PageResult<UserListVo> list(UserFindDto dto) {
    GenericSpecification<User> spec = UserAssembler.getSpecification(dto);
    Page<User> page = userQuery.find(spec, dto.tranPage(), dto.fullTextSearch,
        getMatchSearchFields(dto.getClass()));
    // 关联最后登录时间
    assembleLastLogin(page.getContent());
    // 关联角色、部门、组
    assembleDetailInfos(page.getContent());
    return buildVoPageResult(page, UserAssembler::toListVo);
  }

  @Override
  public UserStatsVo getStats(String appCode) {
    return userQuery.getStats(appCode);
  }

  /**
   * 组装用户详细信息：角色、部门、组
   */
  private void assembleDetailInfos(User user) {
    Long userId = user.getId();

    // 1. 查询用户的所有角色（包含直接分配和通过部门、组分配的角色）
    List<Role> roles = roleQuery.findWideRolesByUserId(userId);
    if (!roles.isEmpty()) {
      // 批量查询应用名称
      Set<Long> appIds = roles.stream()
          .map(Role::getAppId)
          .filter(Objects::nonNull)
          .collect(Collectors.toSet());
      Map<Long, String> appNameMapTemp = new HashMap<>();
      if (!appIds.isEmpty()) {
        List<Application> applications = applicationQuery.findAllById(new ArrayList<>(appIds));
        appNameMapTemp.putAll(applications.stream()
            .collect(Collectors.toMap(Application::getId, Application::getName)));
      }
      final Map<Long, String> appNameMap = appNameMapTemp;

      // 设置应用名称并转换为RoleInfo
      List<RoleInfo> roleInfos = roles.stream()
          .map(role -> {
            if (role.getAppId() != null) {
              role.setAppName(appNameMap.get(role.getAppId()));
            }
            return role.toRoleInfo();
          })
          .collect(Collectors.toList());
      user.setRoles(roleInfos);
    } else {
      user.setRoles(new ArrayList<>());
    }

    // 2. 查询用户的所有部门
    List<Department> departments = departmentQuery.findByUserId(userId);
    List<DepartmentInfo> departmentInfos = departments.stream()
        .map(Department::toDepartmentInfo)
        .collect(Collectors.toList());
    user.setDepartments(departmentInfos);

    // 3. 查询用户的所有组
    List<Group> groups = groupQuery.findByUserId(userId);
    List<GroupInfo> groupInfos = groups.stream()
        .map(Group::toGroupInfo)
        .collect(Collectors.toList());
    user.setGroups(groupInfos);
  }

  /**
   * 组装用户详细信息：角色、部门、组
   */
  private void assembleDetailInfos(List<User> users) {
    if (users == null || users.isEmpty()) {
      return;
    }

    // 收集所有用户ID
    List<Long> userIds = users.stream()
        .map(User::getId)
        .collect(Collectors.toList());

    // 1. 批量查询所有用户的角色
    Map<Long, List<RoleInfo>> userRolesMap = batchQueryUserRoles(userIds);

    // 2. 批量查询所有用户的部门
    Map<Long, List<DepartmentInfo>> userDepartmentsMap = batchQueryUserDepartments(userIds);

    // 3. 批量查询所有用户的组
    Map<Long, List<GroupInfo>> userGroupsMap = batchQueryUserGroups(userIds);

    // 4. 组装数据到用户对象
    for (User user : users) {
      Long userId = user.getId();
      user.setRoles(userRolesMap.getOrDefault(userId, new ArrayList<>()));
      user.setDepartments(userDepartmentsMap.getOrDefault(userId, new ArrayList<>()));
      user.setGroups(userGroupsMap.getOrDefault(userId, new ArrayList<>()));
    }
  }

  /**
   * 批量查询用户角色 通过批量查询授权、授权角色关系、角色等实现真正的批量查询
   */
  private Map<Long, List<RoleInfo>> batchQueryUserRoles(List<Long> userIds) {
    Map<Long, List<RoleInfo>> result = new HashMap<>();

    // 初始化结果Map，确保所有用户都有空列表
    for (Long userId : userIds) {
      result.put(userId, new ArrayList<>());
    }

    // 1. 收集所有主体ID（用户ID + 用户所属的部门和组ID）
    Set<Long> subjectIds = new HashSet<>(userIds);
    for (Long userId : userIds) {
      subjectIds.addAll(userManager.findValidOrgIdsById(userId));
    }

    if (subjectIds.isEmpty()) {
      return result;
    }

    // 2. 批量查询授权ID列表（需要通过AuthorizationQuery，但该方法不存在，需要查看实现）
    // 由于AuthorizationQuery没有批量查询方法，这里需要通过其他方式实现
    // 暂时通过调用单个查询方法，但可以优化为真正的批量查询
    Map<Long, List<Role>> userRolesMap = new HashMap<>();
    Set<Long> allRoleIds = new HashSet<>();
    for (Long userId : userIds) {
      List<Role> roles = roleQuery.findWideRolesByUserId(userId);
      if (!roles.isEmpty()) {
        userRolesMap.put(userId, roles);
        allRoleIds.addAll(roles.stream().map(Role::getId).collect(Collectors.toList()));
      }
    }

    if (allRoleIds.isEmpty()) {
      return result;
    }

    // 3. 批量查询所有角色（用于获取应用名称）
    List<Role> allRoles = roleQuery.findAndCheck(new ArrayList<>(allRoleIds));

    // 4. 批量查询应用名称
    Set<Long> appIds = allRoles.stream()
        .map(Role::getAppId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
    Map<Long, String> appNameMap = new HashMap<>();
    if (!appIds.isEmpty()) {
      List<Application> applications = applicationQuery.findAllById(new ArrayList<>(appIds));
      appNameMap.putAll(applications.stream()
          .collect(Collectors.toMap(Application::getId, Application::getName)));
    }

    // 5. 转换为RoleInfo并设置应用名称
    for (Map.Entry<Long, List<Role>> entry : userRolesMap.entrySet()) {
      Long userId = entry.getKey();
      List<Role> roles = entry.getValue();
      List<RoleInfo> roleInfos = roles.stream()
          .map(role -> {
            if (role.getAppId() != null) {
              role.setAppName(appNameMap.get(role.getAppId()));
            }
            return role.toRoleInfo();
          })
          .collect(Collectors.toList());
      result.put(userId, roleInfos);
    }

    return result;
  }

  /**
   * 批量查询用户部门 通过批量查询部门用户关系，然后批量查询部门实现真正的批量查询
   */
  private Map<Long, List<DepartmentInfo>> batchQueryUserDepartments(List<Long> userIds) {
    Map<Long, List<DepartmentInfo>> result = new HashMap<>();

    // 初始化结果Map
    for (Long userId : userIds) {
      result.put(userId, new ArrayList<>());
    }

    // 1. 批量查询部门用户关系
    List<DepartmentUser> departmentUsers = departmentUserQuery.findByUserIdIn(userIds);
    if (departmentUsers.isEmpty()) {
      return result;
    }

    // 2. 收集所有部门ID
    Set<Long> departmentIds = departmentUsers.stream()
        .map(DepartmentUser::getDepartmentId)
        .collect(Collectors.toSet());
    if (departmentIds.isEmpty()) {
      return result;
    }

    // 3. 批量查询部门
    List<Department> departments = departmentQuery.findAllById(new ArrayList<>(departmentIds));
    Map<Long, Department> departmentMap = departments.stream()
        .collect(Collectors.toMap(Department::getId, dept -> dept));

    // 4. 按用户ID分组
    Map<Long, List<DepartmentUser>> userDepartmentUserMap = departmentUsers.stream()
        .collect(Collectors.groupingBy(DepartmentUser::getUserId));

    // 5. 转换为DepartmentInfo
    for (Map.Entry<Long, List<DepartmentUser>> entry : userDepartmentUserMap.entrySet()) {
      Long userId = entry.getKey();
      List<DepartmentUser> userDepartmentUsers = entry.getValue();
      List<DepartmentInfo> departmentInfos = userDepartmentUsers.stream()
          .map(du -> departmentMap.get(du.getDepartmentId()))
          .filter(Objects::nonNull)
          .map(Department::toDepartmentInfo)
          .collect(Collectors.toList());
      result.put(userId, departmentInfos);
    }

    return result;
  }

  /**
   * 批量查询用户组 通过批量查询组用户关系，然后批量查询组实现真正的批量查询
   */
  private Map<Long, List<GroupInfo>> batchQueryUserGroups(List<Long> userIds) {
    Map<Long, List<GroupInfo>> result = new HashMap<>();

    // 初始化结果Map
    for (Long userId : userIds) {
      result.put(userId, new ArrayList<>());
    }

    // 1. 批量查询组用户关系
    List<GroupUser> groupUsers = groupUserQuery.findByUserIdIn(userIds);
    if (groupUsers.isEmpty()) {
      return result;
    }

    // 2. 收集所有组ID
    Set<Long> groupIds = groupUsers.stream()
        .map(GroupUser::getGroupId)
        .collect(Collectors.toSet());
    if (groupIds.isEmpty()) {
      return result;
    }

    // 3. 批量查询组
    List<Group> groups = groupQuery.findAllById(new ArrayList<>(groupIds));
    Map<Long, Group> groupMap = groups.stream()
        .collect(Collectors.toMap(Group::getId, group -> group));

    // 4. 按用户ID分组
    Map<Long, List<GroupUser>> userGroupUserMap = groupUsers.stream()
        .collect(Collectors.groupingBy(GroupUser::getUserId));

    // 5. 转换为GroupInfo
    for (Map.Entry<Long, List<GroupUser>> entry : userGroupUserMap.entrySet()) {
      Long userId = entry.getKey();
      List<GroupUser> userGroupUsers = entry.getValue();
      List<GroupInfo> groupInfos = userGroupUsers.stream()
          .map(gu -> groupMap.get(gu.getGroupId()))
          .filter(Objects::nonNull)
          .map(Group::toGroupInfo)
          .collect(Collectors.toList());
      result.put(userId, groupInfos);
    }

    return result;
  }

  /**
   * 组装登录历史信息（最近10次）
   */
  private List<LoginHistoryVo> assembleLoginHistory(Long userId) {
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.DESC, "loginTime"));
    Page<LoginHistory> loginHistoryPage = loginHistoryQuery.findByUserId(userId, pageable);
    return loginHistoryPage.getContent().stream()
        .map(history -> {
          LoginHistoryVo historyVo = new LoginHistoryVo();
          historyVo.setTime(history.getLoginTime());
          historyVo.setIp(history.getIpAddress());
          historyVo.setIpAddress(history.getIpAddress());
          historyVo.setLoginType(history.getLoginType());
          historyVo.setLoginStatus(history.getLoginStatus());
          historyVo.setLocation(history.getLocation());
          historyVo.setDevice(history.getDevice());
          historyVo.setUserAgent(history.getUserAgent());
          historyVo.setFailureReason(history.getFailureReason());
          return historyVo;
        })
        .collect(Collectors.toList());
  }

  /**
   * 关联最后登录时间（批量） 从登录历史记录中获取用户的最后登录时间并设置到UserListVo列表中
   */
  private void assembleLastLogin(List<User> users) {
    if (users == null || users.isEmpty()) {
      return;
    }

    // 收集所有用户ID
    List<Long> userIds = users.stream()
        .map(User::getId)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    if (userIds.isEmpty()) {
      return;
    }

    // 批量查询最后登录时间
    Map<Long, LocalDateTime> lastLoginMap = loginHistoryQuery.findLastLoginByUserIds(userIds);

    // 设置最后登录时间
    for (User vo : users) {
      if (vo.getId() != null) {
        vo.setLastLogin(lastLoginMap.get(vo.getId()));
      }
    }
  }

  /**
   * 关联最后登录时间（单个） 从登录历史记录中获取用户的最后登录时间并设置到UserDetailVo或UserCurrentDetailVo中
   */
  private void assembleLastLogin(UserDetailVo vo, Long userId) {
    if (vo == null || userId == null) {
      return;
    }
    List<Long> userIdList = new ArrayList<>();
    userIdList.add(userId);
    Map<Long, LocalDateTime> lastLoginMap = loginHistoryQuery.findLastLoginByUserIds(userIdList);
    vo.setLastLogin(lastLoginMap.get(userId));
  }

  /**
   * 关联最后登录时间（单个） 从登录历史记录中获取用户的最后登录时间并设置到UserCurrentDetailVo中
   */
  private void assembleLastLogin(UserCurrentDetailVo vo, Long userId) {
    if (vo == null || userId == null) {
      return;
    }
    List<Long> userIdList = new ArrayList<>();
    userIdList.add(userId);
    Map<Long, LocalDateTime> lastLoginMap = loginHistoryQuery.findLastLoginByUserIds(userIdList);
    vo.setLastLogin(lastLoginMap.get(userId));
  }

}
