package cloud.xcan.angus.core.gm.interfaces.user.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserInviteAssembler.toUserInviteResendVo;
import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserInviteAssembler.toUserInviteSignupDto;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.gm.user.dto.UserInviteDto;
import cloud.xcan.angus.api.gm.user.dto.UserInviteFindDto;
import cloud.xcan.angus.api.gm.user.vo.UserInviteResendVo;
import cloud.xcan.angus.api.gm.user.vo.UserInviteVo;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.gm.application.cmd.user.UserInviteCmd;
import cloud.xcan.angus.core.gm.application.query.user.UserInviteQuery;
import cloud.xcan.angus.core.gm.domain.user.UserInvite;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.AuthenticationFacade;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.UserSignupDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.UserInviteFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserAcceptInviteDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserInviteAssembler;
import cloud.xcan.angus.api.gm.user.vo.UserDetailVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class UserInviteFacadeImpl implements UserInviteFacade {

  @Resource
  private UserInviteCmd userInviteCmd;

  @Resource
  private UserInviteQuery userInviteQuery;

  @Resource
  private AuthenticationFacade authenticationFacade;

  @NameJoin
  @Override
  public List<UserInviteVo> inviteUser(UserInviteDto dto) {
    if (dto.getInviteType().isEmail()) {
      List<String> emailList = dto.getEmails();
      ProtocolAssert.assertNotEmpty(dto.getEmails(), "Emails are empty");
      List<UserInviteVo> result = new ArrayList<>(emailList.size());
      for (String email : emailList) {
        UserInvite userInvite = UserInviteAssembler.toCreateDomain(dto, email);
        UserInvite saved = userInviteCmd.create(userInvite);
        result.add(UserInviteAssembler.toVo(saved));
      }
      return result;
    }
    UserInvite userInvite = UserInviteAssembler.toCreateDomain(dto, null);
    UserInvite saved = userInviteCmd.create(userInvite);
    return List.of(UserInviteAssembler.toVo(saved));
  }

  @Override
  public void cancelInvite(Long id) {
    userInviteCmd.cancel(id);
  }

  @Override
  public UserInviteResendVo resendInvite(Long id) {
    UserInvite userInvite = userInviteCmd.resend(id);
    return toUserInviteResendVo(userInvite);
  }

  @NameJoin
  @Override
  public PageResult<UserInviteVo> listInvites(UserInviteFindDto dto) {
    GenericSpecification<UserInvite> spec = UserInviteAssembler.getSpecification(dto);
    Page<UserInvite> page = userInviteQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, UserInviteAssembler::toVo);
  }

  @NameJoin
  @Override
  public UserInviteVo getInviteByCode(String inviteCode) {
    UserInvite userInvite = userInviteQuery.findAndCheck(inviteCode);
    return UserInviteAssembler.toVo(userInvite);
  }

  @NameJoin
  @Override
  public UserDetailVo acceptInvite(UserAcceptInviteDto dto) {
    UserInvite userInvite = userInviteQuery.findAndCheck(dto.getInviteCode());
    UserSignupDto signupDto = toUserInviteSignupDto(dto, userInvite);
    return authenticationFacade.signUp(signupDto);
  }

  @Override
  public void rejectInviteByCode(String inviteCode) {
    userInviteCmd.reject(inviteCode);
  }

}
