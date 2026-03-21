package cloud.xcan.angus.core.gm.interfaces.department.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.department.Department;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentCreateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentFindDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentManagerUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentDetailVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentManagerUpdateVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DepartmentAssembler {

  public static Department toCreateDomain(DepartmentCreateDto dto) {
    Department department = new Department();
    department.setName(dto.getName());
    department.setCode(dto.getCode());
    department.setParentId(dto.getParentId());
    department.setLeaderId(dto.getLeaderId());
    department.setDescription(dto.getDescription());
    department.setSortOrder(nullSafe(dto.getSortOrder(), 1));
    department.setStatus(nullSafe(dto.getStatus(), EnabledStatus.ENABLED));
    return department;
  }

  public static Department toUpdateDomain(Long id, DepartmentUpdateDto dto) {
    Department department = new Department();
    department.setId(id);
    department.setName(dto.getName());
    department.setCode(dto.getCode());
    department.setParentId(dto.getParentId());
    department.setLeaderId(dto.getLeaderId());
    department.setDescription(dto.getDescription());
    department.setSortOrder(nullSafe(dto.getSortOrder(), 1));
    department.setStatus(nullSafe(dto.getStatus(), EnabledStatus.ENABLED));
    return department;
  }

  public static DepartmentManagerUpdateVo toDepartmentManagerUpdateVo(Long id,
      DepartmentManagerUpdateDto dto, Department department) {
    DepartmentManagerUpdateVo vo = new DepartmentManagerUpdateVo();
    vo.setDepartmentId(id);
    vo.setLeaderId(dto.getLeaderId());
    vo.setLeaderName(department.getLeaderName());
    vo.setModifiedDate(LocalDateTime.now());
    return vo;
  }

  public static DepartmentDetailVo toDetailVo(Department department) {
    DepartmentDetailVo vo = new DepartmentDetailVo();
    vo.setId(department.getId());
    vo.setName(department.getName());
    vo.setCode(department.getCode());
    vo.setParentId(department.getParentId());
    vo.setParentName(department.getParentName());
    vo.setLevel(department.getLevel());
    vo.setSortOrder(department.getSortOrder());
    vo.setLeaderId(department.getLeaderId());
    vo.setLeaderName(department.getLeaderName());
    vo.setLeaderAvatar(department.getLeaderAvatar());
    vo.setDescription(department.getDescription());
    vo.setStatus(department.getStatus());
    vo.setUserCount(nullSafe(department.getUserCount(), 0L));
    vo.setPath(department.getPath());

    // 设置审计字段
    vo.setTenantId(department.getTenantId());
    vo.setCreatedBy(department.getCreatedBy());
    vo.setCreatedDate(department.getCreatedDate());
    vo.setModifiedBy(department.getModifiedBy());
    vo.setModifiedDate(department.getModifiedDate());
    return vo;
  }

  /**
   * 从扁平列表构建部门树
   */
  public static List<DepartmentDetailVo> buildDepartmentTree(List<Department> departments) {
    if (departments == null || departments.isEmpty()) {
      return new ArrayList<>();
    }

    // Convert to VO list
    List<DepartmentDetailVo> voList = departments.stream()
        .map(DepartmentAssembler::toDetailVo)
        .collect(Collectors.toList());

    // Build tree structure
    Map<Long, DepartmentDetailVo> voMap = voList.stream()
        .collect(Collectors.toMap(DepartmentDetailVo::getId, vo -> vo));

    List<DepartmentDetailVo> rootDepartments = new ArrayList<>();
    for (DepartmentDetailVo vo : voList) {
      if (vo.getParentId() == null || vo.getParentId() <= 0) {
        rootDepartments.add(vo);
      } else {
        DepartmentDetailVo parent = voMap.get(vo.getParentId());
        if (parent != null) {
          if (parent.getChildren() == null) {
            parent.setChildren(new ArrayList<>());
          }
          parent.getChildren().add(vo);
        }
      }
    }

    // Sort departments by sortOrder
    rootDepartments.sort((a, b) -> {
      int orderA = a.getSortOrder() != null ? a.getSortOrder() : 0;
      int orderB = b.getSortOrder() != null ? b.getSortOrder() : 0;
      return Integer.compare(orderA, orderB);
    });

    // Sort children recursively
    sortDepartmentChildren(rootDepartments);
    return rootDepartments;
  }

  /**
   * 递归排序部门子节点
   */
  private static void sortDepartmentChildren(List<DepartmentDetailVo> departments) {
    if (departments == null || departments.isEmpty()) {
      return;
    }
    departments.sort((a, b) -> {
      int orderA = a.getSortOrder() != null ? a.getSortOrder() : 0;
      int orderB = b.getSortOrder() != null ? b.getSortOrder() : 0;
      return Integer.compare(orderA, orderB);
    });
    for (DepartmentDetailVo dept : departments) {
      if (dept.getChildren() != null && !dept.getChildren().isEmpty()) {
        sortDepartmentChildren(dept.getChildren());
      }
    }
  }

  public static GenericSpecification<Department> getSpecification(DepartmentFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate")
        .orderByFields("id", "createdDate", "modifiedDate", "name", "level")
        .matchSearchFields("name", "code")
        .build();
    return new GenericSpecification<>(filters);
  }

}
