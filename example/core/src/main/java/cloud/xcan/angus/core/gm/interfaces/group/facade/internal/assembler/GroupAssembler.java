package cloud.xcan.angus.core.gm.interfaces.group.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupCreateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupFindDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupOwnerUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.dto.GroupUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupDetailVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupOwnerUpdateVo;
import cloud.xcan.angus.core.gm.interfaces.group.facade.vo.GroupOwnerVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupAssembler {

  public static Group toCreateDomain(GroupCreateDto dto) {
    Group group = new Group();
    group.setName(dto.getName());
    group.setCode(dto.getCode());
    group.setDescription(dto.getDescription());
    group.setType(dto.getType());
    group.setOwnerId(dto.getOwnerId());
    group.setUserIds(dto.getUserIds());
    group.setStatus(nullSafe(dto.getStatus(), EnabledStatus.ENABLED));
    return group;
  }

  public static Group toUpdateDomain(Long id, GroupUpdateDto dto) {
    Group group = new Group();
    group.setId(id);
    group.setName(dto.getName());
    group.setCode(dto.getCode());
    group.setDescription(dto.getDescription());
    group.setType(dto.getType());
    group.setOwnerId(dto.getOwnerId());
    group.setStatus(nullSafe(dto.getStatus(), EnabledStatus.ENABLED));
    return group;
  }

  public static GroupDetailVo toDetailVo(Group group) {
    GroupDetailVo vo = new GroupDetailVo();
    vo.setId(group.getId());
    vo.setName(group.getName());
    vo.setCode(group.getCode());
    vo.setDescription(group.getDescription());
    vo.setType(group.getType());
    vo.setStatus(group.getStatus());
    vo.setOwnerId(group.getOwnerId());
    if (group.getOwner() != null) {
      vo.setOwnerName(group.getOwner().getName());
      vo.setOwnerAvatar(group.getOwner().getAvatar());
      vo.setLastActive(group.getOwner().getOnlineDate());
    }
    vo.setUserCount(nullSafe(group.getUserCount(), 0L));

    // 设置设计字段
    vo.setTenantId(group.getTenantId());
    vo.setCreatedBy(group.getCreatedBy());
    vo.setCreatedDate(group.getCreatedDate());
    vo.setModifiedBy(group.getModifiedBy());
    vo.setModifiedDate(group.getModifiedDate());
    return vo;
  }

  public static GroupOwnerUpdateVo toGroupOwnerUpdateVo(
      Long id, GroupOwnerUpdateDto dto, Group group) {
    GroupOwnerUpdateVo vo = new GroupOwnerUpdateVo();
    vo.setGroupId(id);
    vo.setOwnerId(dto.getOwnerId());
    if (group.getOwner() != null) {
      vo.setOwnerName(group.getOwner().getName());
    }
    vo.setModifiedDate(group.getModifiedDate());
    return vo;
  }

  public static List<GroupOwnerVo> toUserVoList(List<Group> groups) {
    if (groups == null || groups.isEmpty()) {
      return new ArrayList<>();
    }
    return groups.stream()
        .map(GroupAssembler::toUserVo)
        .collect(Collectors.toList());
  }

  public static GroupOwnerVo toUserVo(Group group) {
    GroupOwnerVo vo = new GroupOwnerVo();
    vo.setId(group.getId());
    vo.setCode(group.getCode());
    vo.setName(group.getName());
    vo.setType(group.getType());
    vo.setOwnerId(group.getOwnerId());
    if (group.getOwner() != null) {
      vo.setOwnerName(group.getOwner().getName());
      vo.setOwnerAvatar(group.getOwner().getAvatar());
    }
    vo.setUserCount(nullSafe(group.getUserCount(), 0L));
    vo.setStatus(group.getStatus());

    // 设置审计字段
    vo.setTenantId(group.getTenantId());
    vo.setCreatedBy(group.getCreatedBy());
    vo.setCreatedDate(group.getCreatedDate());
    vo.setModifiedBy(group.getModifiedBy());
    vo.setModifiedDate(group.getModifiedDate());
    return vo;
  }

  public static GenericSpecification<Group> getSpecification(GroupFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate")
        .orderByFields("id", "createdDate", "modifiedDate", "name")
        .matchSearchFields("name", "code", "description")
        .build();
    return new GenericSpecification<>(filters);
  }

}
