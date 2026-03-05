package cloud.xcan.angus.core.repo.interfaces.security.facade.internal.assembler;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.core.repo.domain.security.ScanPolicy;
import cloud.xcan.angus.core.repo.interfaces.security.facade.dto.ScanPolicyCreateDto;
import cloud.xcan.angus.core.repo.interfaces.security.facade.dto.ScanPolicyFindDto;
import cloud.xcan.angus.core.repo.interfaces.security.facade.dto.ScanPolicyUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.security.facade.vo.ScanPolicyDetailVo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

public class ScanPolicyAssembler {

  public static ScanPolicy toCreateEntity(ScanPolicyCreateDto dto) {
    ScanPolicy entity = new ScanPolicy();
    entity.setName(dto.getName());
    entity.setDescription(dto.getDescription());
    entity.setRepositoryId(dto.getRepositoryId());
    entity.setScanType(dto.getScanType());
    entity.setEnabled(dto.getEnabled());
    entity.setScanOnPush(dto.getScanOnPush());
    entity.setScheduleCron(dto.getScheduleCron());
    entity.setSeverityThreshold(dto.getSeverityThreshold());
    entity.setAutoBlock(dto.getAutoBlock());
    return entity;
  }

  public static ScanPolicy toUpdateEntity(ScanPolicyUpdateDto dto, String id) {
    ScanPolicy entity = new ScanPolicy();
    entity.setId(id);
    entity.setName(dto.getName());
    entity.setDescription(dto.getDescription());
    entity.setScanType(dto.getScanType());
    entity.setEnabled(dto.getEnabled());
    entity.setScanOnPush(dto.getScanOnPush());
    entity.setScheduleCron(dto.getScheduleCron());
    entity.setSeverityThreshold(dto.getSeverityThreshold());
    entity.setAutoBlock(dto.getAutoBlock());
    return entity;
  }

  public static ScanPolicyDetailVo toDetailVo(ScanPolicy entity) {
    if (entity == null) return null;
    ScanPolicyDetailVo vo = new ScanPolicyDetailVo();
    vo.setId(entity.getId());
    vo.setName(entity.getName());
    vo.setDescription(entity.getDescription());
    vo.setRepositoryId(entity.getRepositoryId());
    vo.setRepositoryName(entity.getRepositoryName());
    vo.setScanType(entity.getScanType());
    vo.setEnabled(entity.getEnabled());
    vo.setScanOnPush(entity.getScanOnPush());
    vo.setScheduleCron(entity.getScheduleCron());
    vo.setSeverityThreshold(entity.getSeverityThreshold());
    vo.setAutoBlock(entity.getAutoBlock());
    vo.setLastScanTime(entity.getLastScanTime());
    vo.setCreatedBy(entity.getCreatedBy());
    vo.setCreatedDate(entity.getCreatedDate());
    vo.setModifiedBy(entity.getModifiedBy());
    vo.setModifiedDate(entity.getModifiedDate());
    return vo;
  }

  public static GenericSpecification<ScanPolicy> getSpecification(ScanPolicyFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .matchSearchFields("name", "description")
        .orderByFields("id", "name", "createdDate", "scanType", "enabled")
        .inAndNotFields("scanType", "repositoryId")
        .build();
    return new GenericSpecification<>(filters);
  }
}
