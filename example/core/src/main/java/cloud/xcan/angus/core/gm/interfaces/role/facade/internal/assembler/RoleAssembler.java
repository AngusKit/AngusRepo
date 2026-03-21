package cloud.xcan.angus.core.gm.interfaces.role.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.role.PermissionInfo;
import cloud.xcan.angus.api.commonlink.role.Role;
import cloud.xcan.angus.api.commonlink.role.enums.RoleEffect;
import cloud.xcan.angus.api.gm.RolePermissionVo;
import cloud.xcan.angus.core.gm.interfaces.role.facade.dto.RoleCreateDto;
import cloud.xcan.angus.core.gm.interfaces.role.facade.dto.RoleFindDto;
import cloud.xcan.angus.core.gm.interfaces.role.facade.dto.RolePermissionUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.role.facade.dto.RoleUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.role.facade.vo.RoleDefaultVo;
import cloud.xcan.angus.core.gm.interfaces.role.facade.vo.RoleDetailVo;
import cloud.xcan.angus.core.gm.interfaces.role.facade.vo.RoleListVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RoleAssembler {

  public static Role toCreateDomain(RoleCreateDto dto) {
    Role role = new Role();
    role.setName(dto.getName());
    role.setCode(dto.getCode());
    role.setDescription(dto.getDescription());
    role.setAppId(dto.getAppId());
    role.setEffect(RoleEffect.ALLOW);
    role.setIsDefault(nullSafe(dto.getIsDefault(), false));
    role.setIsSystem(false);

    if (dto.getPermissions() != null && !dto.getPermissions().isEmpty()) {
      List<PermissionInfo> permissions = dto.getPermissions().stream()
          .map(p -> {
            PermissionInfo info = new PermissionInfo();
            info.setMenuId(p.getMenuId());
            info.setResource(p.getResource());
            info.setActions(p.getActions());
            return info;
          })
          .collect(Collectors.toList());
      role.setPermissions(permissions);
    }
    return role;
  }

  public static Role toUpdateDomain(Long id, RoleUpdateDto dto) {
    Role role = new Role();
    role.setId(id);
    role.setName(dto.getName());
    role.setCode(dto.getCode());
    role.setDescription(dto.getDescription());
    //role.setAppId(dto.getAppId());
    role.setEffect(RoleEffect.ALLOW);
    role.setIsDefault(nullSafe(dto.getIsDefault(), false));
    role.setIsSystem(false);

    if (dto.getPermissions() != null && !dto.getPermissions().isEmpty()) {
      List<PermissionInfo> permissions = dto.getPermissions().stream()
          .map(p -> {
            PermissionInfo info = new PermissionInfo();
            info.setMenuId(p.getMenuId());
            info.setResource(p.getResource());
            info.setActions(p.getActions());
            return info;
          })
          .collect(Collectors.toList());
      role.setPermissions(permissions);
    }
    return role;
  }

  public static RoleDetailVo toDetailVo(Role role) {
    RoleDetailVo vo = new RoleDetailVo();
    vo.setId(role.getId());
    vo.setName(role.getName());
    vo.setCode(role.getCode());
    vo.setDescription(role.getDescription());
    vo.setStatus(role.getStatus());
    vo.setIsSystem(nullSafe(role.getIsSystem(), false));
    vo.setIsDefault(nullSafe(role.getIsDefault(), false));
    vo.setUserCount(nullSafe(role.getUserCount(), 0L));
    vo.setAppId(role.getAppId());
    //vo.setAppName(role.getAppName());

    // 设置审计字段
    vo.setTenantId(role.getTenantId());
    vo.setCreatedBy(role.getCreatedBy());
    vo.setCreatedDate(role.getCreatedDate());
    vo.setModifiedBy(role.getModifiedBy());
    vo.setModifiedDate(role.getModifiedDate());

    // Convert permissions
    vo.setPermissions(role.getPermissions());

    // Convert users
    vo.setUsers(role.getUsers());
    return vo;
  }

  public static RoleListVo toListVo(Role role) {
    RoleListVo vo = new RoleListVo();
    vo.setId(role.getId());
    vo.setName(role.getName());
    vo.setCode(role.getCode());
    vo.setDescription(role.getDescription());
    vo.setStatus(role.getStatus());
    vo.setIsSystem(nullSafe(role.getIsSystem(), false));
    vo.setIsDefault(nullSafe(role.getIsDefault(), false));
    vo.setUserCount(nullSafe(role.getUserCount(), 0L));
    vo.setAppId(role.getAppId());
    //vo.setAppName(role.getAppName());

    // 设置审计字段
    vo.setTenantId(role.getTenantId());
    vo.setCreatedBy(role.getCreatedBy());
    vo.setCreatedDate(role.getCreatedDate());
    vo.setModifiedBy(role.getModifiedBy());
    vo.setModifiedDate(role.getModifiedDate());
    return vo;
  }

  public static RolePermissionVo toPermissionVo(Role role) {
    RolePermissionVo vo = new RolePermissionVo();
    vo.setRoleId(role.getId());
    vo.setRoleName(role.getName());
    vo.setModifiedDate(LocalDateTime.now());

    if (role.getPermissions() != null) {
      vo.setPermissions(role.getPermissions());
    } else {
      vo.setPermissions(new ArrayList<>());
    }
    return vo;
  }

  public static RoleDefaultVo toDefaultVo(Role role) {
    RoleDefaultVo vo = new RoleDefaultVo();
    vo.setId(role.getId());
    vo.setName(role.getName());
    vo.setIsDefault(nullSafe(role.getIsDefault(), false));
    vo.setModifiedDate(LocalDateTime.now());
    return vo;
  }

  public static List<PermissionInfo> toPermissionsDomain(RolePermissionUpdateDto dto) {
    if (dto.getPermissions() == null || dto.getPermissions().isEmpty()) {
      return new ArrayList<>();
    }
    return dto.getPermissions().stream()
        .map(p -> {
          PermissionInfo info = new PermissionInfo();
          info.setMenuId(p.getMenuId());
          info.setResource(p.getResource());
          info.setActions(p.getActions());
          return info;
        })
        .collect(Collectors.toList());
  }

  public static GenericSpecification<Role> getSpecification(RoleFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate")
        .orderByFields("id", "createdDate", "modifiedDate", "name")
        .matchSearchFields("name", "code", "description")
        .build();
    return new GenericSpecification<>(filters);
  }
}
