package cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;
import static cloud.xcan.angus.spec.utils.ObjectUtils.stringSafe;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.enums.UserSource;
import cloud.xcan.angus.api.commonlink.user.enums.UserStatus;
import cloud.xcan.angus.api.enums.Gender;
import cloud.xcan.angus.api.gm.user.dto.UserCreateDto;
import cloud.xcan.angus.api.gm.user.dto.UserFindDto;
import cloud.xcan.angus.api.gm.user.dto.UserLockDto;
import cloud.xcan.angus.api.gm.user.dto.UserPatchDto;
import cloud.xcan.angus.api.gm.user.dto.UserUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserCurrentDetailVo;
import cloud.xcan.angus.api.gm.user.vo.UserDetailVo;
import cloud.xcan.angus.api.gm.user.vo.UserListVo;
import cloud.xcan.angus.api.gm.user.vo.UserLockVo;
import cloud.xcan.angus.api.gm.user.vo.UserStatusUpdateVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class UserAssembler {

  public static User toCreateDomain(UserCreateDto dto) {
    User user = new User();
    user.setUsername(dto.getUsername());
    user.setName(dto.getName());
    user.setEmail(dto.getEmail());
    user.setEmailVerified(false);
    user.setPhone(dto.getPhone());
    user.setEmailVerified(false);
    user.setPassword(dto.getPassword()); // 应当在业务层加密
    user.setAvatar(dto.getAvatar());
    user.setGender(dto.getGender());
    user.setLandline(dto.getLandline());
    user.setJobTitle(dto.getJobTitle());
    user.setAddress(dto.getAddress());
    user.setDepartmentId(dto.getDepartmentId());
    user.setStatus(nullSafe(dto.getStatus(), UserStatus.ACTIVE));
    user.setSysAdmin(false);
    user.setLocked(false);
    user.setSource(UserSource.ADMIN_ADDED);
    user.setRoleIds(dto.getRoleIds());
    return user;
  }

  public static User toUpdateDomain(Long id, UserUpdateDto dto) {
    User user = new User();
    user.setId(id);
    user.setUsername(stringSafe(dto.getUsername()));
    user.setName(stringSafe(dto.getName()));
    user.setEmail(stringSafe(dto.getEmail()));
    user.setPhone(stringSafe(dto.getPhone()));
    user.setGender(nullSafe(dto.getGender(), Gender.UNKNOWN));
    user.setLandline(stringSafe(dto.getLandline()));
    user.setAvatar(stringSafe(dto.getAvatar()));
    user.setJobTitle(stringSafe(dto.getJobTitle()));
    user.setAddress(stringSafe(dto.getAddress()));
    user.setDepartmentId(dto.getDepartmentId());
    return user;
  }

  /**
   * 部分更新用户：只更新 UserPatchDto 中非空的字段
   */
  public static User toPatchDomain(Long id, User existingUser, UserPatchDto dto) {
    User user = new User();
    user.setId(id);
    // 只更新非空字段，保持现有值
    user.setUsername(nullSafe(dto.getUsername(), existingUser.getUsername()));
    user.setName(nullSafe(dto.getName(), existingUser.getName()));
    user.setEmail(nullSafe(dto.getEmail(), existingUser.getEmail()));
    user.setPhone(nullSafe(dto.getPhone(), existingUser.getPhone()));
    user.setGender(nullSafe(dto.getGender(), existingUser.getGender()));
    user.setLandline(nullSafe(dto.getLandline(), existingUser.getLandline()));
    user.setAvatar(nullSafe(dto.getAvatar(), existingUser.getAvatar()));
    user.setJobTitle(nullSafe(dto.getJobTitle(), existingUser.getJobTitle()));
    user.setAddress(nullSafe(dto.getAddress(), existingUser.getAddress()));
    user.setDepartmentId(nullSafe(dto.getDepartmentId(), existingUser.getDepartmentId()));
    return user;
  }

  public static UserStatusUpdateVo toUserStatusUpdateVo(Long id, User user) {
    UserStatusUpdateVo vo = new UserStatusUpdateVo();
    vo.setId(id);
    vo.setStatus(user.getStatus());
    vo.setModifiedDate(user.getModifiedDate());
    return vo;
  }

  public static UserLockVo toUserLockVo(Long id, UserLockDto dto, User user) {
    UserLockVo vo = new UserLockVo();
    vo.setId(id);
    vo.setLocked(user.getLocked());
    vo.setLockReason(dto.getReason());
    vo.setLockTime(LocalDateTime.now());
    return vo;
  }

  public static UserDetailVo toDetailVo(User user) {
    UserDetailVo vo = new UserDetailVo();
    vo.setId(user.getId());
    vo.setUsername(user.getUsername());
    vo.setName(user.getName());
    vo.setEmail(user.getEmail());
    vo.setEmailVerified(user.getEmailVerified());
    vo.setPhone(user.getPhone());
    vo.setPhoneVerified(user.getPhoneVerified());
    vo.setAvatar(user.getAvatar());
    vo.setGender(user.getGender());
    vo.setLandline(user.getLandline());
    vo.setJobTitle(user.getJobTitle());
    vo.setAddress(user.getAddress());
    vo.setLocation(user.getLocation());
    vo.setBio(user.getBio());
    vo.setWebsite(user.getWebsite());
    vo.setGithub(user.getGithub());
    vo.setTwitter(user.getTwitter());
    vo.setLinkedin(user.getLinkedin());
    vo.setDepartmentId(user.getDepartmentId());
    vo.setStatus(user.getStatus());
    vo.setSysAdmin(user.getSysAdmin());
    vo.setLocked(nullSafe(user.getLocked(), false));
    vo.setSource(user.getSource());
    vo.setLdapId(user.getLdapId());
    // lastLogin 由门面层关联方法设置，不从User实体获取
    vo.setOnline(nullSafe(user.getOnline(), false));
    vo.setOnlineDate(user.getOnlineDate());
    vo.setOfflineDate(user.getOfflineDate());

    // 设置关联资源
    vo.setRoles(user.getRoles());
    vo.setDepartments(user.getDepartments());
    vo.setGroups(user.getGroups());

    // 设置审计信息
    vo.setTenantId(user.getTenantId());
    vo.setCreatedBy(user.getCreatedBy());
    vo.setCreatedDate(user.getCreatedDate());
    vo.setModifiedBy(user.getModifiedBy());
    vo.setModifiedDate(user.getModifiedDate());
    return vo;
  }

  public static UserCurrentDetailVo toCurrentDetailVo(User user) {
    UserCurrentDetailVo vo = new UserCurrentDetailVo();
    vo.setId(user.getId());
    vo.setUsername(user.getUsername());
    vo.setName(user.getName());
    vo.setEmail(user.getEmail());
    vo.setEmailVerified(user.getEmailVerified());
    vo.setPhone(user.getPhone());
    vo.setPhoneVerified(user.getPhoneVerified());
    vo.setAvatar(user.getAvatar());
    vo.setGender(user.getGender());
    vo.setLandline(user.getLandline());
    vo.setJobTitle(user.getJobTitle());
    vo.setAddress(user.getAddress());
    vo.setLocation(user.getLocation());
    vo.setBio(user.getBio());
    vo.setWebsite(user.getWebsite());
    vo.setGithub(user.getGithub());
    vo.setTwitter(user.getTwitter());
    vo.setLinkedin(user.getLinkedin());
    //vo.setDepartment(user.getDepartment());
    vo.setDepartmentId(user.getDepartmentId());
    vo.setStatus(user.getStatus());
    vo.setSysAdmin(user.getSysAdmin());
    vo.setLocked(nullSafe(user.getLocked(), false));
    vo.setSource(user.getSource());
    vo.setLdapId(user.getLdapId());
    vo.setLastLogin(user.getLastLogin());
    vo.setOnline(nullSafe(user.getOnline(), false));
    vo.setOnlineDate(user.getOnlineDate());
    vo.setOfflineDate(user.getOfflineDate());

    // 设置审计信息
    vo.setTenantId(user.getTenantId());
    vo.setCreatedBy(user.getCreatedBy());
    vo.setCreatedDate(user.getCreatedDate());
    vo.setModifiedBy(user.getModifiedBy());
    vo.setModifiedDate(user.getModifiedDate());
    return vo;
  }

  public static UserListVo toListVo(User user) {
    UserListVo vo = new UserListVo();
    vo.setId(user.getId());
    vo.setUsername(user.getUsername());
    vo.setName(user.getName());
    vo.setEmail(user.getEmail());
    vo.setEmailVerified(user.getEmailVerified());
    vo.setPhone(user.getPhone());
    vo.setPhoneVerified(user.getPhoneVerified());
    vo.setGender(user.getGender());
    vo.setAvatar(user.getAvatar());
    vo.setLandline(user.getLandline());
    vo.setJobTitle(user.getJobTitle());
    vo.setAddress(user.getAddress());
    vo.setLocation(user.getLocation());
    vo.setBio(user.getBio());
    vo.setWebsite(user.getWebsite());
    vo.setGithub(user.getGithub());
    vo.setTwitter(user.getTwitter());
    vo.setLinkedin(user.getLinkedin());

    //vo.setDepartment(user.getDepartment());
    vo.setDepartmentId(user.getDepartmentId());
    vo.setStatus(user.getStatus());
    vo.setSysAdmin(user.getSysAdmin());
    vo.setLocked(nullSafe(user.getLocked(), false));
    vo.setSource(user.getSource());
    vo.setLdapId(user.getLdapId());
    vo.setLastLogin(user.getLastLogin());
    vo.setOnline(nullSafe(user.getOnline(), false));
    vo.setOnlineDate(user.getOnlineDate());
    vo.setOfflineDate(user.getOfflineDate());

    // 设置关联资源
    vo.setRoles(user.getRoles());
    vo.setDepartments(user.getDepartments());
    vo.setGroups(user.getGroups());

    // 设置审计信息
    vo.setTenantId(user.getTenantId());
    vo.setCreatedBy(user.getCreatedBy());
    vo.setCreatedDate(user.getCreatedDate());
    vo.setModifiedBy(user.getModifiedBy());
    vo.setModifiedDate(user.getModifiedDate());
    return vo;
  }

  public static GenericSpecification<User> getSpecification(UserFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate")
        .orderByFields("id", "name", "createdDate", "modifiedDate")
        .matchSearchFields("name", "username", "email", "phone")
        .build();
    return new GenericSpecification<>(filters);
  }
}
