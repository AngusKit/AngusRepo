package cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler;

import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.enums.SignInType;
import cloud.xcan.angus.api.gm.user.dto.UserInviteDto;
import cloud.xcan.angus.api.gm.user.dto.UserInviteFindDto;
import cloud.xcan.angus.api.gm.user.vo.UserInviteResendVo;
import cloud.xcan.angus.api.gm.user.vo.UserInviteVo;
import cloud.xcan.angus.core.gm.domain.user.UserInvite;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.UserSignupDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserAcceptInviteDto;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 用户邀请数据组装器
 */
public class UserInviteAssembler {

  public static UserInvite toCreateDomain(UserInviteDto dto, String email) {
    UserInvite invite = new UserInvite();
    invite.setEmail(email);
    invite.setInviteType(dto.getInviteType());
    invite.setAppId(dto.getAppId());
    invite.setRoleId(dto.getRoleId());
    invite.setDepartmentId(dto.getDepartmentId());
    invite.setMessage(dto.getMessage());
    invite.setInvitedBy(getUserId());
    invite.setInviteDate(LocalDateTime.now());
    invite.setExpiryDate(LocalDateTime.now().plusDays(nullSafe(dto.getExpireDays(), 7)));
    return invite;
  }

  public static UserInviteVo toVo(UserInvite invite) {
    UserInviteVo vo = new UserInviteVo();
    vo.setId(invite.getId());
    vo.setEmail(invite.getEmail());
    vo.setInviteType(invite.getInviteType());
    vo.setAppId(invite.getAppId());
    vo.setRoleId(invite.getRoleId());
    vo.setMessage(invite.getMessage());
    vo.setDepartmentId(invite.getDepartmentId());
    vo.setInvitedBy(invite.getInvitedBy());
    vo.setInviteDate(invite.getInviteDate());
    vo.setExpiryDate(invite.getExpiryDate());
    vo.setStatus(invite.getStatus());
    vo.setInviteCode(invite.getInviteCode());
    vo.setInviteUrl(invite.getInviteUrl());

    // 设置审计信息
    vo.setTenantId(invite.getTenantId());
    return vo;
  }

  public static UserSignupDto toUserInviteSignupDto(UserAcceptInviteDto dto,
      UserInvite userInvite) {
    UserSignupDto signupDto = new UserSignupDto();
    signupDto.setRegisterType(SignInType.ACCOUNT_PASSWORD);
    signupDto.setEmail(
        userInvite.getInviteType().isEmail() ? userInvite.getEmail() : dto.getEmail());
    signupDto.setName(dto.getName());
    signupDto.setPassword(dto.getPassword());
    signupDto.setConfirmPassword(dto.getConfirmPassword());
    signupDto.setInviteCode(dto.getInviteCode());
    signupDto.setAgreement(true);
    return signupDto;
  }

  public static UserInviteResendVo toUserInviteResendVo(UserInvite userInvite) {
    UserInviteResendVo vo = new UserInviteResendVo();
    vo.setId(userInvite.getId());
    vo.setResentTime(userInvite.getInviteDate());
    return vo;
  }

  public static GenericSpecification<UserInvite> getSpecification(UserInviteFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate", "inviteDate", "expiryDate")
        .orderByFields("id", "createdDate", "modifiedDate", "inviteDate")
        .matchSearchFields("email", "message")
        .build();
    return new GenericSpecification<>(filters);
  }
}
