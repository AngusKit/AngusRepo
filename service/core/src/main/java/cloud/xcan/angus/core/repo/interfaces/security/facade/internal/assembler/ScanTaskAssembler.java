package cloud.xcan.angus.core.repo.interfaces.security.facade.internal.assembler;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.core.repo.domain.security.ScanTask;
import cloud.xcan.angus.core.repo.domain.security.Vulnerability;
import cloud.xcan.angus.core.repo.interfaces.security.facade.dto.ScanTaskCreateDto;
import cloud.xcan.angus.core.repo.interfaces.security.facade.dto.ScanTaskFindDto;
import cloud.xcan.angus.core.repo.interfaces.security.facade.dto.ScanTaskUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.security.facade.vo.ScanTaskDetailVo;
import cloud.xcan.angus.core.repo.interfaces.security.facade.vo.VulnerabilityVo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

public class ScanTaskAssembler {

  public static ScanTask toCreateEntity(ScanTaskCreateDto dto) {
    ScanTask entity = new ScanTask();
    entity.setArtifactId(dto.getArtifactId());
    entity.setRepositoryId(dto.getRepositoryId());
    entity.setScanType(dto.getScanType());
    return entity;
  }

  public static ScanTask toUpdateEntity(ScanTaskUpdateDto dto, String id) {
    ScanTask entity = new ScanTask();
    entity.setId(id);
    entity.setScanType(dto.getScanType());
    return entity;
  }

  public static ScanTaskDetailVo toDetailVo(ScanTask entity) {
    if (entity == null) return null;
    ScanTaskDetailVo vo = new ScanTaskDetailVo();
    vo.setId(entity.getId());
    vo.setArtifactId(entity.getArtifactId());
    vo.setArtifactName(entity.getArtifactName());
    vo.setRepositoryId(entity.getRepositoryId());
    vo.setRepositoryName(entity.getRepositoryName());
    vo.setScanType(entity.getScanType());
    vo.setStatus(entity.getStatus());
    vo.setProgress(entity.getProgress());
    vo.setStartTime(entity.getStartTime());
    vo.setEndTime(entity.getEndTime());
    vo.setDurationSeconds(entity.calculateDurationSeconds());
    vo.setVulnerabilityCount(entity.getVulnerabilityCount());
    vo.setCriticalCount(entity.getCriticalCount());
    vo.setHighCount(entity.getHighCount());
    vo.setMediumCount(entity.getMediumCount());
    vo.setLowCount(entity.getLowCount());
    vo.setErrorMessage(entity.getErrorMessage());
    vo.setCreatedBy(entity.getCreatedBy());
    vo.setCreatedDate(entity.getCreatedDate());
    return vo;
  }

  public static VulnerabilityVo toVulnerabilityVo(Vulnerability entity) {
    if (entity == null) return null;
    VulnerabilityVo vo = new VulnerabilityVo();
    vo.setId(entity.getId());
    vo.setCveId(entity.getCveId());
    vo.setTitle(entity.getTitle());
    vo.setDescription(entity.getDescription());
    vo.setSeverity(entity.getSeverity());
    vo.setCvssScore(entity.getCvssScore());
    vo.setAffectedComponent(entity.getAffectedComponent());
    vo.setAffectedVersion(entity.getAffectedVersion());
    vo.setFixedVersion(entity.getFixedVersion());
    vo.setIsFixed(entity.getIsFixed());
    vo.setSolution(entity.getSolution());
    vo.setDiscoveredDate(entity.getDiscoveredDate());
    return vo;
  }

  public static GenericSpecification<ScanTask> getSpecification(ScanTaskFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .orderByFields("id", "createdDate", "status", "scanType", "vulnerabilityCount")
        .inAndNotFields("scanType", "status", "repositoryId", "artifactId")
        .build();
    return new GenericSpecification<>(filters);
  }
}
