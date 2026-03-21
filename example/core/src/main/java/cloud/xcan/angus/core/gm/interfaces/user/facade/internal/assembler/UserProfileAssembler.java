package cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler;

import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserProfileUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserProfileVo;

/**
 * 用户个人信息数据组装器
 */
public class UserProfileAssembler {

  public static User toUpdateDomain(UserProfileUpdateDto dto) {
    User user = new User();
    user.setId(getUserId());
    user.setName(dto.getName());
    user.setGender(dto.getGender());
    user.setBio(dto.getBio());
    user.setJobTitle(dto.getJobTitle());
    user.setLocation(dto.getLocation());
    user.setAddress(dto.getAddress());
    user.setDepartmentId(dto.getDepartmentId());
    user.setWebsite(dto.getWebsite());
    user.setGithub(dto.getGithub());
    user.setTwitter(dto.getTwitter());
    user.setLinkedin(dto.getLinkedin());
    return user;
  }

  public static UserProfileVo toVo(User user) {
    UserProfileVo vo = new UserProfileVo();
    vo.setId(user.getId());
    vo.setUsername(user.getUsername());
    vo.setName(user.getName());
    vo.setEmail(user.getEmail());
    vo.setPhone(user.getPhone());
    vo.setAvatar(user.getAvatar());
    vo.setGender(user.getGender());
    vo.setLandline(user.getLandline());
    vo.setBio(user.getBio());
    vo.setJobTitle(user.getJobTitle());
    vo.setAddress(user.getAddress());
    vo.setLocation(user.getLocation());
    //vo.setDepartment(user.getDepartment());
    vo.setDepartmentId(user.getDepartmentId());
    vo.setWebsite(user.getWebsite());
    vo.setGithub(user.getGithub());
    vo.setTwitter(user.getTwitter());
    vo.setLinkedin(user.getLinkedin());
    // 设置审计信息
    vo.setTenantId(user.getTenantId());
    vo.setCreatedBy(user.getCreatedBy());
    vo.setCreatedDate(user.getCreatedDate());
    vo.setModifiedBy(user.getModifiedBy());
    vo.setModifiedDate(user.getModifiedDate());
    return vo;
  }
}
