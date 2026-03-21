package cloud.xcan.angus.core.gm.interfaces.group.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.group.facade.internal.assembler.GroupUserAssembler.toGroupMemberAddVo;
import static cloud.xcan.angus.core.gm.interfaces.group.facade.internal.assembler.GroupUserAssembler.toGroupUserTransferVo;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserBase;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.group.GroupUserCmd;
import cloud.xcan.angus.core.gm.application.query.group.GroupUserQuery;
import cloud.xcan.angus.core.gm.interfaces.group.facade.GroupUserFacade;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUserAddDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUserRemoveDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUserTransferDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.internal.assembler.GroupUserAssembler;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupUserAddVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupUserTransferVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupUserVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class GroupUserFacadeImpl implements GroupUserFacade {

  @Resource
  private GroupUserCmd groupUserCmd;

  @Resource
  private GroupUserQuery groupUserQuery;

  @Resource
  private UserManager userManager;

  @Override
  public GroupUserAddVo addUsers(Long groupId, GroupUserAddDto dto) {
    int addedCount = groupUserCmd.addUsers(groupId, dto.getUserIds());
    Map<Long, UserBase> userBaseMap = userManager.getUserBaseMap(dto.getUserIds());
    return toGroupMemberAddVo(groupId, dto, addedCount, userBaseMap);
  }

  @Override
  public GroupUserTransferVo transferUsers(Long groupId, GroupUserTransferDto dto) {
    int transferredCount = groupUserCmd.transferUsers(groupId, dto.getTargetGroupId(),
        dto.getUserIds());
    return toGroupUserTransferVo(groupId, dto, transferredCount);
  }

  @Override
  public void removeUser(Long groupId, Long userId) {
    groupUserCmd.removeUser(groupId, userId);
  }

  @Override
  public void removeUsers(Long groupId, GroupUserRemoveDto dto) {
    groupUserCmd.removeUsers(groupId, dto.getUserIds());
  }

  @NameJoin
  @Override
  public PageResult<GroupUserVo> listUsers(Long groupId, GroupUserFindDto dto) {
    GenericSpecification<User> spec = GroupUserAssembler.getMemberSpecification(dto);
    Page<User> page = groupUserQuery.findUsers(groupId, spec, dto.tranPage());
    return buildVoPageResult(page, GroupUserAssembler::toMemberVo);
  }

  @NameJoin
  @Override
  public PageResult<GroupUserVo> listUsersNotInGroup(Long groupId, GroupUserFindDto dto) {
    GenericSpecification<User> spec = GroupUserAssembler.getMemberSpecification(dto);
    Page<User> page = groupUserQuery.findUsersNotInGroup(groupId, spec, dto.tranPage());
    return buildVoPageResult(page, GroupUserAssembler::toMemberVo);
  }

}
