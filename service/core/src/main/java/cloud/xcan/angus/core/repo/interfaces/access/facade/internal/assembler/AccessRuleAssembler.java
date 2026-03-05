package cloud.xcan.angus.core.repo.interfaces.access.facade.internal.assembler;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.core.repo.domain.access.AccessRule;
import cloud.xcan.angus.core.repo.interfaces.access.facade.dto.AccessRuleCreateDto;
import cloud.xcan.angus.core.repo.interfaces.access.facade.dto.AccessRuleFindDto;
import cloud.xcan.angus.core.repo.interfaces.access.facade.dto.AccessRuleUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.access.facade.vo.AccessRuleVo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

public class AccessRuleAssembler {

  public static AccessRule toCreateEntity(AccessRuleCreateDto dto, Long repositoryId) {
    AccessRule entity = new AccessRule();
    entity.setRepositoryId(repositoryId);
    entity.setName(dto.getName());
    entity.setDescription(dto.getDescription());
    entity.setPrincipalType(dto.getPrincipalType());
    entity.setPrincipalId(dto.getPrincipalId());
    entity.setPermissions(dto.getPermissions());
    entity.setPaths(dto.getPaths());
    entity.setEnabled(dto.getEnabled());
    entity.setPriority(dto.getPriority());
    entity.setExpiresAt(dto.getExpiresAt());
    return entity;
  }

  public static AccessRule toUpdateEntity(AccessRuleUpdateDto dto, Long id) {
    AccessRule entity = new AccessRule();
    entity.setId(id);
    entity.setName(dto.getName());
    entity.setDescription(dto.getDescription());
    entity.setPrincipalType(dto.getPrincipalType());
    entity.setPrincipalId(dto.getPrincipalId());
    entity.setPermissions(dto.getPermissions());
    entity.setPaths(dto.getPaths());
    entity.setEnabled(dto.getEnabled());
    entity.setPriority(dto.getPriority());
    entity.setExpiresAt(dto.getExpiresAt());
    return entity;
  }

  public static AccessRuleVo toVo(AccessRule entity) {
    if (entity == null) {
      return null;
    }
    AccessRuleVo vo = new AccessRuleVo();
    vo.setId(entity.getId());
    vo.setRepositoryId(entity.getRepositoryId());
    vo.setName(entity.getName());
    vo.setDescription(entity.getDescription());
    vo.setPrincipalType(entity.getPrincipalType());
    vo.setPrincipalId(entity.getPrincipalId());
    vo.setPrincipalName(entity.getPrincipalName());
    vo.setEnabled(entity.getEnabled());
    vo.setExpiresAt(entity.getExpiresAt());
    vo.setPriority(entity.getPriority());
    vo.setPermissions(entity.getPermissions());
    vo.setPaths(entity.getPaths());
    vo.setCreatedBy(entity.getCreatedBy());
    vo.setCreatedDate(entity.getCreatedDate());
    vo.setModifiedBy(entity.getModifiedBy());
    vo.setModifiedDate(entity.getModifiedDate());
    return vo;
  }

  public static GenericSpecification<AccessRule> getSpecification(AccessRuleFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .matchSearchFields("name", "description")
        .orderByFields("id", "name", "createdDate", "priority")
        .inAndNotFields("principalType", "repositoryId")
        .build();
    return new GenericSpecification<>(filters);
  }
}
