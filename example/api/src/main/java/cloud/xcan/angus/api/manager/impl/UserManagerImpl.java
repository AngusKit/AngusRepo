package cloud.xcan.angus.api.manager.impl;

import static cloud.xcan.angus.api.manager.ManagerMessage.USER_EMAIL_NOT_BIND;
import static cloud.xcan.angus.api.manager.ManagerMessage.USER_EMAIL_NOT_BIND_CODE;
import static cloud.xcan.angus.api.manager.ManagerMessage.USER_MOBILE_NOT_BIND;
import static cloud.xcan.angus.api.manager.ManagerMessage.USER_MOBILE_NOT_BIND_CODE;
import static cloud.xcan.angus.api.manager.ManagerMessage.USER_NOT_EXISTED_T;
import static cloud.xcan.angus.core.biz.BizAssert.assertNotEmpty;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.utils.BeanFieldUtils.setPropertyValue;
import static cloud.xcan.angus.remote.message.ProtocolException.M.USER_DISABLED_KEY;
import static cloud.xcan.angus.remote.message.ProtocolException.M.USER_DISABLED_T;
import static cloud.xcan.angus.remote.message.ProtocolException.M.USER_LOCKED_KEY;
import static cloud.xcan.angus.remote.message.ProtocolException.M.USER_LOCKED_T;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singleton;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.OrgType;
import cloud.xcan.angus.api.commonlink.department.DepartmentUserRepo;
import cloud.xcan.angus.api.commonlink.group.GroupUserRepo;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserBase;
import cloud.xcan.angus.api.commonlink.user.UserBaseRepo;
import cloud.xcan.angus.api.commonlink.user.UserInfo;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.api.enums.AuthObjectType;
import cloud.xcan.angus.api.manager.DepartmentManager;
import cloud.xcan.angus.api.manager.GroupManager;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.jpa.entity.projection.IdAndName;
import cloud.xcan.angus.core.utils.BeanFieldUtils;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.utils.CaffeineCacheUtils;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class UserManagerImpl implements UserManager {

  @Resource
  @Qualifier("commonUserRepo")
  private UserRepo userRepo;

  @Resource
  @Qualifier("commonUserBaseRepo")
  private UserBaseRepo userBaseRepo;

  @Resource
  @Qualifier("commonGroupUserRepo")
  private GroupUserRepo groupUserRepo;

  @Resource
  @Qualifier("commonDepartmentUserRepo")
  private DepartmentUserRepo departmentUserRepo;

  @Resource
  private GroupManager groupManager;

  @Resource
  private DepartmentManager departmentManager;

  public static final Cache<Long, UserBase> USER_INFO_CACHE
      = CaffeineCacheUtils.createCache("USER_INFO_CACHE");

  @Override
  public User findUser(Long userId) {
    return userRepo.findById(userId).orElse(null);
  }

  @Override
  public Page<User> findAllUsers(Specification<User> spec, Pageable pageable) {
    return userRepo.findAll(spec, pageable);
  }

  @Override
  public UserBase findBaseUser(Long userId) {
    return userBaseRepo.findById(userId).orElse(null);
  }

  @Override
  public Page<UserBase> findAllBaseUsers(Specification<UserBase> spec, Pageable pageable) {
    return userBaseRepo.findAll(spec, pageable);
  }

  @Override
  public UserBase findBaseUser(String username) {
    return userBaseRepo.findByUsername(username);
  }

  @Override
  public List<User> findUsers(Collection<Long> userIds) {
    return userRepo.findAllById(userIds);
  }

  @Override
  public List<UserBase> findBaseUsers(Collection<Long> userIds) {
    return userBaseRepo.findAllById(userIds);
  }

  @Override
  public List<UserBase> findBaseUsersByTenantId(Long tenantId) {
    return userBaseRepo.findAllByTenantId(tenantId);
  }

  @Override
  public List<User> findByTenantId(Long tenantId) {
    return userRepo.findAllByTenantId(tenantId);
  }

  @Override
  public List<User> findValidByTenantId(Long tenantId) {
    return userRepo.findValidByTenantId(tenantId);
  }

  @Override
  public List<User> findValidSysAdminByTenantId(Long tenantId) {
    return userRepo.findValidSysAdminByTenantId(tenantId);
  }

  @Override
  public Set<Long> findUserIdsByOrgIds(Collection<Long> orgIds) {
    Set<Long> userIds = new HashSet<>();
    if (isNotEmpty(orgIds)) {
      userIds.addAll(userRepo.findUserIdsByIdIn(orgIds));
      userIds.addAll(getUserIdsByDeptIds(orgIds));
      userIds.addAll(getUserIdsByGroupIds(orgIds));
    }
    return userIds;
  }

  @Override
  public Set<Long> findValidUserIdsByOrgIds(Collection<Long> orgIds) {
    Set<Long> userIds = new HashSet<>();
    if (isNotEmpty(orgIds)) {
      userIds.addAll(userRepo.findValidUserIdsByIdIn(orgIds));
      userIds.addAll(getValidUserIdsByDeptIds(orgIds));
      userIds.addAll(getValidUserIdsByGroupIds(orgIds));
    }
    return userIds;
  }

  @Override
  public List<Long> findValidSysAdminIdsByTenantId(Long tenantId) {
    return userRepo.findValidSysAdminIdsByTenantId(tenantId);
  }

  @Override
  public List<User> findSysAdminByTenantId(Long tenantId) {
    return userRepo.findByTenantIdAndSysAdmin(tenantId, true);
  }

  @Override
  public List<Long> findUserIdsByIdIn(Collection<Long> userIds) {
    return userRepo.findUserIdsByIdIn(userIds);
  }

  @Override
  public List<Long> findValidOrgIdsById(Long userId) {
    return userRepo.findValidOrgIdsById(userId);
  }

  @Override
  public User findAndCheck(Long userId) {
    return userRepo.findById(userId).orElseThrow(()
        -> ResourceNotFound.of(USER_NOT_EXISTED_T, new Object[]{userId}));
  }

  @Override
  public void checkExists(Long userId) {
    assertResourceNotFound(userRepo.existsById(userId), USER_NOT_EXISTED_T, new Object[]{userId});
  }

  @Override
  public List<User> findAndCheck(Collection<Long> userIds) {
    if (isEmpty(userIds)) {
      return emptyList();
    }
    List<User> userInfos = userRepo.findAllById(userIds);
    assertResourceNotFound(userInfos, USER_NOT_EXISTED_T, new Object[]{userIds.iterator().next()});
    if (userIds.size() != userInfos.size()) {
      userIds.removeAll(userInfos.stream().map(User::getId).collect(Collectors.toSet()));
      assertResourceNotFound(userIds.isEmpty(), USER_NOT_EXISTED_T,
          new Object[]{userIds.iterator().next()});
    }
    return userInfos;
  }

  @Override
  public void checkExists(Collection<Long> userIds) {
    if (isEmpty(userIds)) {
      return;
    }
    List<Long> userIdsDb = userRepo.findIdsByIdIn(userIds);
    assertResourceNotFound(userIdsDb, USER_NOT_EXISTED_T, new Object[]{userIds.iterator().next()});
    if (userIds.size() != userIdsDb.size()) {
      userIds.removeAll(userIdsDb);
      assertResourceNotFound(userIds.isEmpty(), USER_NOT_EXISTED_T,
          new Object[]{userIds.iterator().next()});
    }
  }

  @Override
  public User checkValid(Long userId) {
    if (isNull(userId)) {
      return null;
    }
    User user = findAndCheck(userId);
    checkUserValid(user);
    return user;
  }

  @Override
  public List<User> checkValid(Collection<Long> userIds) {
    if (isEmpty(userIds)) {
      return emptyList();
    }
    List<User> userInfos = userRepo.findAllById(userIds);
    assertResourceNotFound(userInfos, USER_NOT_EXISTED_T, new Object[]{userIds.iterator().next()});
    if (userIds.size() != userInfos.size()) {
      userIds.removeAll(userInfos.stream().map(User::getId).collect(Collectors.toSet()));
      assertResourceNotFound(userIds.isEmpty(), USER_NOT_EXISTED_T,
          new Object[]{userIds.iterator().next()});
    }
    for (User user : userInfos) {
      checkUserValid(user);
    }
    return userInfos;
  }

  @Override
  public UserBase checkAndFindBaseUser(Long userId) {
    return userBaseRepo.findById(userId).orElseThrow(() -> ResourceNotFound.of(userId, "User"));
  }

  @Override
  public UserBase checkValidBaseUser(Long userId) {
    if (isNull(userId)) {
      return null;
    }
    UserBase user = checkAndFindBaseUser(userId);
    checkUserValid(user);
    return user;
  }

  @Override
  public List<UserBase> checkValidBaseUser(Collection<Long> userIds) {
    if (isEmpty(userIds)) {
      return emptyList();
    }
    List<UserBase> userBases = userBaseRepo.findAllById(userIds);
    assertResourceNotFound(userBases, USER_NOT_EXISTED_T, new Object[]{userIds.iterator().next()});
    if (userIds.size() != userBases.size()) {
      userIds.removeAll(userBases.stream().map(UserBase::getId).collect(Collectors.toSet()));
      assertResourceNotFound(userIds.isEmpty(), USER_NOT_EXISTED_T,
          new Object[]{userIds.iterator().next()});
    }
    for (UserBase user : userBases) {
      checkUserValid(user);
    }
    return userBases;
  }

  @Override
  public Map<String, List<UserBase>> checkValidBaseUsersByName(Collection<String> names) {
    if (isEmpty(names)) {
      return emptyMap();
    }
    List<UserBase> users = userBaseRepo.findByNameIn(names);
    assertResourceNotFound(users, USER_NOT_EXISTED_T, new Object[]{names.iterator().next()});
    if (names.size() != users.size()) {
      names.removeAll(users.stream().map(UserBase::getName).collect(Collectors.toSet()));
      assertResourceNotFound(names.isEmpty(), USER_NOT_EXISTED_T,
          new Object[]{names.iterator().next()});
    }
    for (UserBase user : users) {
      checkUserValid(user);
    }
    return users.stream().collect(Collectors.groupingBy(UserBase::getName));
  }

  @Override
  public void checkUserValid(User user) {
    assertTrue(user.getStatus().isValid(), USER_DISABLED_T, USER_DISABLED_KEY,
        new Object[]{user.getName()});
    assertTrue(!user.getLocked(), USER_LOCKED_T, USER_LOCKED_KEY,
        new Object[]{user.getName()});
  }

  @Override
  public void checkUserValid(UserBase user) {
    assertTrue(user.getStatus().isValid(), USER_DISABLED_T, USER_DISABLED_KEY,
        new Object[]{user.getName()});
    assertTrue(!user.getLocked(), USER_LOCKED_T, USER_LOCKED_KEY,
        new Object[]{user.getName()});
  }

  @Override
  public Map<Long, User> checkValidAndGetUserMap(Collection<Long> userIds) {
    List<User> userInfos = checkValid(userIds);
    return isEmpty(userInfos) ? null
        : userInfos.stream().collect(Collectors.toMap(User::getId, x -> x));
  }

  @Override
  public void checkOrgExists(OrgType type, Long orgId) {
    switch (type) {
      case USER:
        findAndCheck(orgId);
        break;
      case GROUP:
        groupManager.checkExists(List.of(orgId));
        break;
      case DEPT:
        departmentManager.checkExists(List.of(orgId));
        break;
      default:
        // NOOP
    }
  }

  @Override
  public List<?> findOrgs(OrgType type, Collection<Long> orgIds) {
    return switch (type) {
      case USER -> userBaseRepo.findAllById(orgIds);
      case GROUP -> groupManager.find(orgIds);
      case DEPT -> departmentManager.find(orgIds);
    };
  }

  @Override
  public void checkOrgExists(OrgType type, Collection<Long> orgIds) {
    switch (type) {
      case USER:
        checkExists(orgIds);
        break;
      case GROUP:
        groupManager.checkExists(orgIds);
        break;
      case DEPT:
        departmentManager.checkExists(orgIds);
        break;
      default:
        // NOOP
    }
  }

  @Override
  public Object findAndCheckOrg(OrgType type, Long orgId) {
    return switch (type) {
      case USER -> findAndCheck(orgId);
      case GROUP -> groupManager.findAndCheck(orgId);
      case DEPT -> departmentManager.findAndCheck(orgId);
    };
  }

  @Override
  public List<?> findAndCheckOrg(OrgType type, Collection<Long> orgIds) {
    return switch (type) {
      case USER -> findAndCheck(orgIds);
      case GROUP -> groupManager.findAndCheck(orgIds);
      case DEPT -> departmentManager.findAndCheck(orgIds);
    };
  }

  @Override
  public void checkValidOrg(OrgType type, Long orgId) {
    switch (type) {
      case USER:
        checkValidBaseUser(orgId);
        break;
      case GROUP:
        groupManager.checkValid(orgId);
        break;
      case DEPT:
        departmentManager.findAndCheck(orgId);
        break;
      default:
        // NOOP
    }
  }

  @Override
  public void checkValidOrg(OrgType type, Collection<Long> orgIds) {
    switch (type) {
      case USER:
        checkValidBaseUser(orgIds);
        break;
      case GROUP:
        groupManager.checkValid(orgIds);
        break;
      case DEPT:
        departmentManager.findAndCheck(orgIds);
        break;
      default:
        // NOOP
    }
  }

  @Override
  public Object findAndCheckValidOrg(OrgType type, Long orgId) {
    return switch (type) {
      case USER -> checkValidBaseUser(orgId);
      case GROUP -> groupManager.checkValid(orgId);
      case DEPT -> departmentManager.findAndCheck(orgId);
    };
  }

  @Override
  public List<?> findAndCheckValidOrg(OrgType type, Collection<Long> orgIds) {
    return switch (type) {
      case USER -> checkValidBaseUser(orgIds);
      case GROUP -> groupManager.checkValid(orgIds);
      case DEPT -> departmentManager.findAndCheck(orgIds);
    };
  }

  @Override
  public void checkUserMobileExists(List<User> users) {
    if (isNotEmpty(users)) {
      for (User user : users) {
        assertNotEmpty(user.getPhone(), USER_MOBILE_NOT_BIND_CODE, USER_MOBILE_NOT_BIND);
      }
    }
  }

  @Override
  public void checkUserEmailExists(List<User> users) {
    if (isNotEmpty(users)) {
      for (User user : users) {
        assertNotEmpty(user.getEmail(), USER_EMAIL_NOT_BIND_CODE, USER_EMAIL_NOT_BIND);
      }
    }
  }

  @Override
  public Set<Long> getUserIdByOrgType0(AuthObjectType orgType, Long orgId) {
    Set<Long> userIds = new HashSet<>();
    switch (orgType) {
      case DEPT:
        assert orgId != null;
        userIds.addAll(getUserIdsByDeptIds(singleton(orgId)));
        break;
      case GROUP:
        assert orgId != null;
        userIds.addAll(getUserIdsByGroupIds(singleton(orgId)));
        break;
      default:
        // By USER
        if (nonNull(orgId)) {
          userIds.add(orgId);
        }
    }
    return userIds;
  }

  @Override
  public Set<Long> getUserIdByOrgType(AuthObjectType orgType, Long orgId) {
    Set<Long> userIds = new HashSet<>();
    switch (orgType) {
      case DEPT:
        assert orgId != null;
        userIds.addAll(getUserIdsByDeptIds(singleton(orgId)));
        break;
      case GROUP:
        assert orgId != null;
        userIds.addAll(getUserIdsByGroupIds(singleton(orgId)));
        break;
      default:
        // By USER
        userIds.add(nonNull(orgId) ? orgId : getUserId());
    }
    return userIds;
  }

  @Override
  public Set<Long> getUserIdByOrgTypeIn0(AuthObjectType orgType, Collection<Long> orgIds) {
    Set<Long> userIds = new HashSet<>();
    switch (orgType) {
      case DEPT:
        assert orgIds != null;
        userIds.addAll(getUserIdsByDeptIds(orgIds));
        break;
      case GROUP:
        assert orgIds != null;
        userIds.addAll(getUserIdsByGroupIds(orgIds));
        break;
      default:
        // By USER
        if (nonNull(orgIds)) {
          userIds.addAll(orgIds);
        }
    }
    return userIds;
  }

  @Override
  public Set<Long> getAllValidUserIds() {
    int page = -1;
    Set<Long> allUserIds = new HashSet<>();
    Page<Long> userIdPage;
    // Page<Long> userIdPage; -> Fix:: ClassCastException: class java.math.BigInteger cannot be cast to class java.lang.Long (java.math.BigInteger and java.lang.Long are in module java.base of loader 'bootstrap')at com.mysql.jdbc.ConnectionImpl.buildCollationMapping(Connec
    do {
      userIdPage = userRepo.findValidId(PageRequest.of(++page, 2000));
      if (userIdPage.hasContent()) {
        allUserIds.addAll(userIdPage.getContent());
      }
    } while (userIdPage.hasNext());
    return allUserIds;
  }

  @Override
  public Set<Long> getAllValidUserIdsByTenantId(Long tenantId) {
    int page = -1;
    Set<Long> allUserIds = new HashSet<>();
    Page<Long> userIdPage;
    // Page<Long> userIdPage; -> Fix:: ClassCastException: class java.math.BigInteger cannot be cast to class java.lang.Long (java.math.BigInteger and java.lang.Long are in module java.base of loader 'bootstrap')at com.mysql.jdbc.ConnectionImpl.buildCollationMapping(Connec
    do {
      userIdPage = userRepo.findValidIdByTenantId(tenantId, PageRequest.of(++page, 2000));
      if (userIdPage.hasContent()) {
        allUserIds.addAll(userIdPage.getContent());
      }
    } while (userIdPage.hasNext());
    return allUserIds;
  }

  @Override
  public Set<Long> getValidUserIdsByGroupIds(Collection<Long> groupIds) {
    return groupUserRepo.findValidUserIdsByGroupIds(groupIds);
  }

  @Override
  public Set<Long> getValidUserIdsByGroupIds(Long tenantId, Collection<Long> groupIds) {
    return groupUserRepo.findValidUserIdsByTenantIdAndGroupIds(tenantId, groupIds);
  }

  @Override
  public Set<Long> getUserIdsByGroupIds(Collection<Long> groupIds) {
    return groupUserRepo.findUserIdsByGroupIds(groupIds);
  }

  @Override
  public Set<Long> getValidUserIdsByDeptIds(Collection<Long> deptIds) {
    return departmentUserRepo.findValidUserIdsByDeptIds(deptIds);
  }

  @Override
  public Set<Long> getValidUserIdsByDeptIds(Long tenantId, Collection<Long> deptIds) {
    return departmentUserRepo.findValidUserIdsByTenantIdAndDeptIds(tenantId, deptIds);
  }

  @Override
  public Set<Long> getUserIdsByDeptIds(Collection<Long> deptIds) {
    return departmentUserRepo.findUserIdsByDeptIds(deptIds);
  }

  @Override
  public List<Long> getValidOrgAndUserIds() {
    Long currentUserId = getUserId();
    List<Long> ids = userRepo.findValidOrgIdsById(currentUserId);
    ids.add(currentUserId);
    return ids;
  }

  @Override
  public List<Long> getValidOrgAndUserIds(Long userId) {
    List<Long> ids = userRepo.findValidOrgIdsById(userId);
    ids.add(userId);
    return ids;
  }

  @Override
  public List<Long> getOrgAndUserIds() {
    Long currentUserId = getUserId();
    List<Long> ids = userRepo.findValidOrgIdsById(currentUserId);
    ids.add(currentUserId);
    return ids;
  }

  @Override
  public List<Long> getOrgAndUserIds(Long userId) {
    List<Long> ids = userRepo.findValidOrgIdsById(userId);
    ids.add(userId);
    return ids;
  }

  @Override
  public Map<Long, String> getOrgNameByIds(Collection<Long> orgIds) {
    List<IdAndName> idAndNames = userRepo.findOrgIdAndNameByIds(orgIds);
    return isEmpty(idAndNames) ? emptyMap()
        : idAndNames.stream().collect(Collectors.toMap(IdAndName::getId, IdAndName::getName));
  }

  @Override
  public Map<Long, User> getValidUserInfoMap(Collection<Long> userIds) {
    if (isEmpty(userIds)) {
      return emptyMap();
    }
    return userRepo.findValidByIdIn(userIds).stream()
        .collect(Collectors.toMap(User::getId, o -> o));
  }

  @Override
  public Map<Long, User> getUserInfoMap(Collection<Long> userIds) {
    if (isEmpty(userIds)) {
      return emptyMap();
    }
    return userRepo.findAllById(userIds).stream()
        .collect(Collectors.toMap(User::getId, o -> o));
  }

  @Override
  public Map<Long, UserBase> getValidUserBaseMap(Collection<Long> userIds) {
    if (isEmpty(userIds)) {
      return emptyMap();
    }
    return userBaseRepo.findValidByIdIn(userIds).stream()
        .collect(Collectors.toMap(UserBase::getId, o -> o));
  }

  @Override
  public Map<Long, UserBase> getUserBaseMap(Collection<Long> userIds) {
    if (isEmpty(userIds)) {
      return emptyMap();
    }
    return userBaseRepo.findAllById(userIds).stream()
        .collect(Collectors.toMap(UserBase::getId, o -> o));
  }

  @Override
  public Map<Long, UserInfo> getUserInfoMapByIds(Collection<Long> userIds) {
    if (isEmpty(userIds)) {
      return emptyMap();
    }
    return userBaseRepo.findAllById(userIds).stream()
        .collect(Collectors.toMap(UserBase::getId,
            o -> new UserInfo().setId(o.getId())
                .setUsername(o.getUsername())
                .setName(o.getName())
                .setEmail(o.getEmail())
                .setPhone(o.getPhone())
                .setAvatar(o.getAvatar())
        ));
  }

  @Override
  public Map<String, UserInfo> getUserInfoMapByUsername(Collection<String> usernames) {
    if (isEmpty(usernames)) {
      return emptyMap();
    }
    return userBaseRepo.findByUsernameIn(usernames).stream()
        .collect(Collectors.toMap(UserBase::getUsername,
            o -> new UserInfo().setId(o.getId())
                .setUsername(o.getUsername())
                .setName(o.getName())
                .setEmail(o.getEmail())
                .setPhone(o.getPhone())
                .setAvatar(o.getAvatar())
        ));
  }

  @Override
  public Map<String, UserInfo> getUserInfoMapByEmail(Collection<String> emails) {
    if (isEmpty(emails)) {
      return emptyMap();
    }
    Collection<UserBase> users = userBaseRepo.findByEmailIn(emails);
    if (users.isEmpty()) {
      return emptyMap();
    }
    return users.stream().collect(Collectors.toMap(UserBase::getEmail,
        o -> new UserInfo().setId(o.getId())
            .setUsername(o.getUsername())
            .setName(o.getName())
            .setEmail(o.getEmail())
            .setPhone(o.getPhone())
            .setAvatar(o.getAvatar())
    ));
  }

  @Override
  public Long getCachedTenantId(Long tenantId, Long userId) {
    if (tenantId != null && tenantId > 0) {
      return tenantId;
    } else {
      // 给调度任务数据设置租户ID
      UserBase userBase = USER_INFO_CACHE.getIfPresent(userId);
      if (userBase != null) {
        return userBase.getTenantId();
      } else {
        userBase = findBaseUser(userId);
        if (userBase != null) {
          USER_INFO_CACHE.put(userId, userBase);
          return userBase.getTenantId();
        }
      }
    }
    return tenantId;
  }

  @Override
  public UserBase getCachedUserBase(Long userId) {
    if (userId == null) {
      return null;
    } else {
      // 给调度任务数据设置租户ID
      UserBase userBase = USER_INFO_CACHE.getIfPresent(userId);
      if (userBase != null) {
        return userBase;
      } else {
        userBase = findBaseUser(userId);
        if (userBase != null) {
          USER_INFO_CACHE.put(userId, userBase);
          return userBase;
        }
      }
    }
    return null;
  }

  @SneakyThrows
  @Override
  public void setUserNameAndAvatar(Collection<?> targets, String userIdField) {
    setUserNameAndAvatar(targets, userIdField, "creator", "avatar");
  }

  @SneakyThrows
  @Override
  public void setUserNameAndAvatar(Collection<?> targets, String userIdField,
      String fullNameField, String userAvatarField) {
    if (isNotEmpty(targets)) {
      Set<Long> userIds = new HashSet<>();
      for (Object target : targets) {
        Object userId = FieldUtils.readField(target, userIdField, true);
        if (nonNull(userId)) {
          userIds.add(Long.parseLong(userId.toString()));
        }
      }
      Map<Long, UserBase> userDbMap = getUserBaseMap(userIds);
      if (isEmpty(userDbMap)) {
        return;
      }
      Object first = targets.stream().findFirst().orElseThrow();
      boolean hasName = BeanFieldUtils.hasProperty(first, fullNameField);
      boolean hasAvatar = BeanFieldUtils.hasProperty(first, userAvatarField);
      for (Object target : targets) {
        Object userId = FieldUtils.readField(target, userIdField, true);
        if (nonNull(userId)) {
          UserBase user = userDbMap.get(Long.parseLong(userId.toString()));
          if (nonNull(user)) {
            if (hasName) {
              setPropertyValue(target, fullNameField, user.getName());
            }
            if (hasAvatar) {
              setPropertyValue(target, userAvatarField, user.getAvatar());
            }
          }
        }
      }
    }
  }

}
