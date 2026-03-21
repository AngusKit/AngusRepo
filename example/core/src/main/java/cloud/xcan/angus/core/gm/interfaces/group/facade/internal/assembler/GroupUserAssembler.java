package cloud.xcan.angus.core.gm.interfaces.group.facade.internal.assembler;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserBase;
import cloud.xcan.angus.api.commonlink.user.UserInfo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUserAddDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUserTransferDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupUserAddVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupUserTransferVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupUserVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupUserAssembler {

  public static GroupUserAddVo toGroupMemberAddVo(Long groupId, GroupUserAddDto dto,
      int addedCount, Map<Long, UserBase> userBaseMap) {
    GroupUserAddVo vo = new GroupUserAddVo();
    vo.setGroupId(groupId);
    vo.setAddedCount(addedCount);
    List<UserInfo> addedUsers = dto.getUserIds().stream().map(x -> userBaseMap.get(x).toUserInfo())
        .collect(Collectors.toList());
    vo.setAddedUsers(addedUsers);
    return vo;
  }

  public static GroupUserTransferVo toGroupUserTransferVo(Long groupId,
      GroupUserTransferDto dto, int transferredCount) {
    GroupUserTransferVo vo = new GroupUserTransferVo();
    vo.setSourceGroupId(groupId);
    vo.setTargetGroupId(dto.getTargetGroupId());
    vo.setTransferredCount(transferredCount);
    return vo;
  }

  public static GroupUserVo toMemberVo(User user) {
    GroupUserVo vo = new GroupUserVo();
    vo.setId(user.getId());
    vo.setName(user.getName());
    vo.setEmail(user.getEmail());
    vo.setAvatar(user.getAvatar());
    //vo.setDepartment(user.getDepartment());
    vo.setDepartmentId(user.getDepartmentId());

    vo.setIsOwner(user.getGroupOwner());
    vo.setJoinDate(user.getGroupJoinDate());

    // 设置审计字段
    vo.setTenantId(user.getTenantId());
    vo.setCreatedBy(user.getCreatedBy());
    vo.setCreatedDate(user.getCreatedDate());
    vo.setModifiedBy(user.getModifiedBy());
    vo.setModifiedDate(user.getModifiedDate());
    return vo;
  }

  public static GenericSpecification<User> getMemberSpecification(GroupUserFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate")
        .orderByFields("id", "createdDate", "modifiedDate", "name")
        .matchSearchFields("name", "username", "email", "phone")
        .build();
    return new GenericSpecification<>(filters);
  }
}
