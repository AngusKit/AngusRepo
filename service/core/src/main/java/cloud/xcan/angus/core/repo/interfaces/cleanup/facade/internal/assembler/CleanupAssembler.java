package cloud.xcan.angus.core.repo.interfaces.cleanup.facade.internal.assembler;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupExecution;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupPolicy;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.dto.CleanupPolicyCreateDto;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.dto.CleanupPolicyFindDto;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.dto.CleanupPolicyUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.vo.CleanupExecutionVo;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.vo.CleanupPolicyDetailVo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;
import java.util.UUID;

public class CleanupAssembler {

  public static CleanupPolicy toCreateEntity(CleanupPolicyCreateDto dto) {
    CleanupPolicy entity = new CleanupPolicy();
    entity.setId(UUID.randomUUID().toString());
    entity.setName(dto.getName());
    entity.setDescription(dto.getDescription());
    entity.setRepositoryId(dto.getRepositoryId());
    entity.setType(dto.getType());
    entity.setEnabled(dto.getEnabled());
    entity.setDryRun(dto.getDryRun());
    entity.setConditionJson(dto.getConditionJson());
    entity.setScheduleJson(dto.getScheduleJson());
    return entity;
  }

  public static CleanupPolicy toUpdateEntity(CleanupPolicyUpdateDto dto, String id) {
    CleanupPolicy entity = new CleanupPolicy();
    entity.setId(id);
    entity.setName(dto.getName());
    entity.setDescription(dto.getDescription());
    entity.setRepositoryId(dto.getRepositoryId());
    entity.setType(dto.getType());
    entity.setEnabled(dto.getEnabled());
    entity.setDryRun(dto.getDryRun());
    entity.setConditionJson(dto.getConditionJson());
    entity.setScheduleJson(dto.getScheduleJson());
    return entity;
  }

  public static CleanupPolicyDetailVo toPolicyDetailVo(CleanupPolicy entity) {
    if (entity == null) {
      return null;
    }
    CleanupPolicyDetailVo vo = new CleanupPolicyDetailVo();
    vo.setId(entity.getId());
    vo.setName(entity.getName());
    vo.setDescription(entity.getDescription());
    vo.setRepositoryId(entity.getRepositoryId());
    vo.setRepositoryName(entity.getRepositoryName());
    vo.setType(entity.getType());
    vo.setEnabled(entity.getEnabled());
    vo.setDryRun(entity.getDryRun());
    vo.setConditionJson(entity.getConditionJson());
    vo.setScheduleJson(entity.getScheduleJson());
    vo.setLastExecutionStatsJson(entity.getLastExecutionStatsJson());
    vo.setLastExecuted(entity.getLastExecuted());
    vo.setNextExecution(entity.getNextExecution());
    vo.setExecutionCount(entity.getExecutionCount());
    vo.setCreatedBy(entity.getCreatedBy());
    vo.setCreatedDate(entity.getCreatedDate());
    vo.setModifiedBy(entity.getModifiedBy());
    vo.setModifiedDate(entity.getModifiedDate());
    return vo;
  }

  public static CleanupExecutionVo toExecutionVo(CleanupExecution entity) {
    if (entity == null) {
      return null;
    }
    CleanupExecutionVo vo = new CleanupExecutionVo();
    vo.setId(entity.getId());
    vo.setPolicyId(entity.getPolicyId());
    vo.setPolicyName(entity.getPolicyName());
    vo.setStatus(entity.getStatus());
    vo.setProgress(entity.getProgress());
    vo.setStartTime(entity.getStartTime());
    vo.setEndTime(entity.getEndTime());
    vo.setDurationSeconds(entity.calculateDurationSeconds());
    vo.setErrorMessage(entity.getErrorMessage());
    vo.setStatisticsJson(entity.getStatisticsJson());
    vo.setCreatedDate(entity.getCreatedDate());
    return vo;
  }

  public static GenericSpecification<CleanupPolicy> getSpecification(CleanupPolicyFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .matchSearchFields("name", "description")
        .orderByFields("id", "name", "createdDate", "type", "enabled")
        .inAndNotFields("type", "repositoryId")
        .build();
    return new GenericSpecification<>(filters);
  }
}
