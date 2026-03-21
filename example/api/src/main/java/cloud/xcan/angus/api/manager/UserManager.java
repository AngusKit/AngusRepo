package cloud.xcan.angus.api.manager;

import cloud.xcan.angus.api.commonlink.OrgType;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserBase;
import cloud.xcan.angus.api.commonlink.user.UserInfo;
import cloud.xcan.angus.api.enums.AuthObjectType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

public interface UserManager {

  User findUser(Long userId);

  Page<User> findAllUsers(@Nullable Specification<User> spec, Pageable pageable);

  UserBase findBaseUser(Long userId);

  Page<UserBase> findAllBaseUsers(@Nullable Specification<UserBase> spec, Pageable pageable);

  UserBase findBaseUser(String username);

  List<User> findUsers(Collection<Long> userIds);

  List<UserBase> findBaseUsers(Collection<Long> userIds);

  List<UserBase> findBaseUsersByTenantId(Long tenantId);

  List<User> findByTenantId(Long tenantId);

  List<User> findValidByTenantId(Long tenantId);

  List<User> findValidSysAdminByTenantId(Long tenantId);

  Set<Long> findUserIdsByOrgIds(Collection<Long> orgIds);

  Set<Long> findValidUserIdsByOrgIds(Collection<Long> orgIds);

  List<Long> findValidSysAdminIdsByTenantId(Long tenantId);

  List<User> findSysAdminByTenantId(Long tenantId);

  List<Long> findUserIdsByIdIn(Collection<Long> userIds);

  List<Long> findValidOrgIdsById(Long userId);

  User findAndCheck(Long userId);

  void checkExists(Long userId);

  List<User> findAndCheck(Collection<Long> userIds);

  void checkExists(Collection<Long> userIds);

  User checkValid(Long userId);

  List<User> checkValid(Collection<Long> userIds);

  UserBase checkAndFindBaseUser(Long userId);

  UserBase checkValidBaseUser(Long userId);

  List<UserBase> checkValidBaseUser(Collection<Long> userIds);

  Map<String, List<UserBase>> checkValidBaseUsersByName(Collection<String> names);

  void checkUserValid(User userInfo);

  void checkUserValid(UserBase userBase);

  Map<Long, User> checkValidAndGetUserMap(Collection<Long> userIds);

  void checkOrgExists(OrgType type, Long orgId);

  List<?> findOrgs(OrgType type, Collection<Long> orgIds);

  void checkOrgExists(OrgType type, Collection<Long> orgIds);

  Object findAndCheckOrg(OrgType type, Long orgId);

  List<?> findAndCheckOrg(OrgType type, Collection<Long> orgIds);

  void checkValidOrg(OrgType type, Long orgId);

  void checkValidOrg(OrgType type, Collection<Long> orgIds);

  Object findAndCheckValidOrg(OrgType type, Long orgId);

  List<?> findAndCheckValidOrg(OrgType type, Collection<Long> orgIds);

  void checkUserMobileExists(List<User> users);

  void checkUserEmailExists(List<User> users);

  Set<Long> getUserIdByOrgType0(AuthObjectType creatorObjectType, Long creatorObjectId);

  Set<Long> getUserIdByOrgType(AuthObjectType creatorObjectType,
      Long creatorObjectId);

  Set<Long> getUserIdByOrgTypeIn0(AuthObjectType orgType, Collection<Long> orgIds);

  Set<Long> getAllValidUserIds();

  Set<Long> getAllValidUserIdsByTenantId(Long tenantId);

  Set<Long> getValidUserIdsByGroupIds(Collection<Long> groupIds);

  Set<Long> getValidUserIdsByGroupIds(Long tenantId, Collection<Long> groupIds);

  Set<Long> getUserIdsByGroupIds(Collection<Long> deptIds);

  Set<Long> getValidUserIdsByDeptIds(Collection<Long> deptIds);

  Set<Long> getValidUserIdsByDeptIds(Long tenantId, Collection<Long> deptIds);

  Set<Long> getUserIdsByDeptIds(Collection<Long> deptIds);

  List<Long> getValidOrgAndUserIds();

  List<Long> getValidOrgAndUserIds(Long userId);

  List<Long> getOrgAndUserIds();

  List<Long> getOrgAndUserIds(Long userId);

  Map<Long, String> getOrgNameByIds(Collection<Long> orgIds);

  Map<Long, User> getValidUserInfoMap(Collection<Long> userIds);

  Map<Long, User> getUserInfoMap(Collection<Long> userIds);

  Map<Long, UserBase> getValidUserBaseMap(Collection<Long> userIds);

  Map<Long, UserBase> getUserBaseMap(Collection<Long> userIds);

  Map<Long, UserInfo> getUserInfoMapByIds(Collection<Long> userIds);

  Map<String, UserInfo> getUserInfoMapByUsername(Collection<String> usernames);

  Map<String, UserInfo> getUserInfoMapByEmail(Collection<String> emails);

  Long getCachedTenantId(Long tenantId, Long userId);

  UserBase getCachedUserBase(Long userId);

  void setUserNameAndAvatar(Collection<?> targets, String userIdField);

  void setUserNameAndAvatar(Collection<?> targets, String userIdField, String userNameField,
      String userAvatarField);

}
