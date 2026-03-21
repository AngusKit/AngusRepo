package cloud.xcan.angus.core.gm.interfaces.department.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.department.DepartmentUser;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserBase;
import cloud.xcan.angus.api.commonlink.user.UserInfo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUserAddDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUserTransferDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentUserAddVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentUserTransferVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentUserVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DepartmentUserAssembler {

  public static DepartmentUserAddVo toDepartmentUserAddVo(Long departmentId,
      DepartmentUserAddDto dto, Map<Long, UserBase> userBaseMap, int addedCount) {
    List<UserInfo> addedUsers = dto.getUserIds().stream().map(x -> userBaseMap.get(x).toUserInfo())
        .collect(Collectors.toList());
    DepartmentUserAddVo vo = new DepartmentUserAddVo();
    vo.setDepartmentId(departmentId);
    vo.setAddedCount(addedCount);
    vo.setAddedUsers(addedUsers);
    return vo;
  }

  public static DepartmentUserTransferVo toDepartmentUserTransferVo(Long departmentId,
      DepartmentUserTransferDto dto, int transferredCount) {
    DepartmentUserTransferVo vo = new DepartmentUserTransferVo();
    vo.setSourceDepartmentId(departmentId);
    vo.setTargetDepartmentId(dto.getTargetDepartmentId());
    vo.setTransferredCount(transferredCount);
    return vo;
  }

  public static DepartmentUserVo toUserVo(User user) {
    DepartmentUserVo vo = new DepartmentUserVo();
    vo.setId(user.getId());
    vo.setName(user.getName());
    vo.setEmail(user.getEmail());
    vo.setPhone(user.getPhone());
    vo.setAvatar(user.getAvatar());
    vo.setStatus(user.getStatus() != null ? user.getStatus().name() : null);

    // Set join date (use createdDate as join date)
    vo.setJoinDate(user.getCreatedDate());

    // Set auditing fields
    vo.setTenantId(user.getTenantId());
    vo.setCreatedBy(user.getCreatedBy());
    vo.setCreatedDate(user.getCreatedDate());
    vo.setModifiedBy(user.getModifiedBy());
    vo.setModifiedDate(user.getModifiedDate());
    return vo;
  }

  public static DepartmentUserVo toUserVo(DepartmentUser departmentUser, User user) {
    DepartmentUserVo vo = toUserVo(user);
    vo.setIsManager(nullSafe(departmentUser.getIsManager(), false));
    if (departmentUser.getCreatedDate() != null) {
      vo.setJoinDate(departmentUser.getCreatedDate());
    }
    return vo;
  }

  public static GenericSpecification<User> getUserSpecification(DepartmentUserFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate")
        .orderByFields("id", "createdDate", "modifiedDate", "name")
        .matchSearchFields("name", "username", "email", "phone")
        .build();
    return new GenericSpecification<>(filters);
  }
}
