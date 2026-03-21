package cloud.xcan.angus.core.gm.interfaces.group.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.group.GroupRepo;
import cloud.xcan.angus.api.commonlink.group.enums.GroupType;
import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.group.GroupCmd;
import cloud.xcan.angus.core.gm.application.query.group.GroupQuery;
import cloud.xcan.angus.core.gm.application.query.group.GroupUserQuery;
import cloud.xcan.angus.core.gm.interfaces.group.facade.GroupFacade;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupCreateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupFindDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupOwnerUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.internal.assembler.GroupAssembler;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupDetailVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupOwnerUpdateVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupOwnerVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class GroupFacadeImpl implements GroupFacade {

  @Resource
  private GroupCmd groupCmd;

  @Resource
  private GroupQuery groupQuery;

  @Resource
  private GroupUserQuery groupUserQuery;

  @Resource
  private GroupRepo groupRepo;

  @NameJoin
  @Override
  public GroupDetailVo create(GroupCreateDto dto) {
    Group group = GroupAssembler.toCreateDomain(dto);
    Group saved = groupCmd.create(group);
    groupQuery.setOwnerUser(List.of(saved));
    return GroupAssembler.toDetailVo(saved);
  }

  @NameJoin
  @Override
  public GroupDetailVo update(Long id, GroupUpdateDto dto) {
    Group group = GroupAssembler.toUpdateDomain(id, dto);
    Group saved = groupCmd.update(group);
    groupQuery.setOwnerUser(List.of(saved));
    Map<Long, Long> groupUsers = groupUserQuery.countUsersByGroupIds(List.of(id));
    saved.setUserCount(groupUsers.getOrDefault(id, 0L));
    return GroupAssembler.toDetailVo(saved);
  }

  @NameJoin
  @Override
  public GroupDetailVo updateStatus(Long id, EnabledStatusUpdateDto dto) {
    Group saved = groupCmd.updateStatus(id, dto.getStatus());
    groupQuery.setOwnerUser(List.of(saved));
    Map<Long, Long> groupUsers = groupUserQuery.countUsersByGroupIds(List.of(id));
    saved.setUserCount(groupUsers.getOrDefault(saved.getId(), 0L));
    return GroupAssembler.toDetailVo(saved);
  }

  @Override
  public GroupOwnerUpdateVo updateOwner(Long id, GroupOwnerUpdateDto dto) {
    Group group = groupCmd.updateOwner(id, dto.getOwnerId());
    groupQuery.setOwnerUser(List.of(group));
    Map<Long, Long> groupUsers = groupUserQuery.countUsersByGroupIds(List.of(id));
    group.setUserCount(groupUsers.getOrDefault(group.getId(), 0L));
    return GroupAssembler.toGroupOwnerUpdateVo(id, dto, group);
  }

  @Override
  public void delete(Long id) {
    groupCmd.delete(id);
  }

  @NameJoin
  @Override
  public GroupDetailVo getDetail(Long id) {
    Group group = groupQuery.findAndCheck(id);
    groupQuery.setOwnerUser(List.of(group));
    Map<Long, Long> groupUsers = groupUserQuery.countUsersByGroupIds(List.of(id));
    group.setUserCount(groupUsers.getOrDefault(group.getId(), 0L));
    return GroupAssembler.toDetailVo(group);
  }

  @NameJoin
  @Override
  public PageResult<GroupDetailVo> list(GroupFindDto dto) {
    GenericSpecification<Group> spec = GroupAssembler.getSpecification(dto);
    Page<Group> page = groupQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    if (!page.isEmpty()) {
      groupQuery.setOwnerUser(page.getContent());

      Map<Long, Long> groupUsers = groupUserQuery.countUsersByGroupIds(page.stream()
          .map(Group::getId).collect(Collectors.toList()));
      for (Group group : page.getContent()) {
        group.setUserCount(groupUsers.getOrDefault(group.getId(), 0L));
      }
    }
    return buildVoPageResult(page, GroupAssembler::toDetailVo);
  }

  @Override
  public GroupStatsVo getStats() {
    GroupStatsVo stats = new GroupStatsVo();

    long totalGroups = groupQuery.count();
    long projectGroups = groupQuery.countByType(GroupType.PROJECT);
    long functionGroups = groupQuery.countByType(GroupType.FUNCTION);
    long tempGroups = groupQuery.countByType(GroupType.TEMP);

    stats.setTotalGroups(totalGroups);
    stats.setProjectGroups(projectGroups);
    stats.setFunctionGroups(functionGroups);
    stats.setTempGroups(tempGroups);

    stats.setActiveMembers(groupUserQuery.countActiveUsers());
    stats.setNewGroupsThisMonth(groupQuery.countNewGroupsThisMonth());

    // 统计组用户数量分布
    Long tenantId = getOptTenantId();
    List<Group> allGroups = groupRepo.findAllByTenantId(tenantId);
    List<GroupStatsVo.GroupUsersVo> groupUsers = new ArrayList<>();
    if (!allGroups.isEmpty()) {
      List<Long> groupIds = allGroups.stream()
          .map(Group::getId)
          .collect(Collectors.toList());
      Map<Long, Long> groupUserCountMap = groupUserQuery.countUsersByGroupIds(groupIds);

      Map<Long, Group> groupMap = allGroups.stream()
          .collect(Collectors.toMap(Group::getId, g -> g));

      for (Map.Entry<Long, Long> entry : groupUserCountMap.entrySet()) {
        Group group = groupMap.get(entry.getKey());
        if (group != null) {
          GroupStatsVo.GroupUsersVo groupUsersVo = new GroupStatsVo.GroupUsersVo();
          groupUsersVo.setGroupName(group.getName());
          groupUsersVo.setUserCount(entry.getValue());
          groupUsers.add(groupUsersVo);
        }
      }

      // 按用户数降序排序
      groupUsers.sort((a, b) -> Long.compare(b.getUserCount(), a.getUserCount()));
    }
    stats.setGroupUsers(groupUsers);

    return stats;
  }

  @NameJoin
  @Override
  public List<GroupOwnerVo> getGroupsByUser(Long userId) {
    List<Group> groups = groupQuery.findByUserId(userId);
    groupQuery.setOwnerUser(groups);
    return GroupAssembler.toUserVoList(groups);
  }
}
