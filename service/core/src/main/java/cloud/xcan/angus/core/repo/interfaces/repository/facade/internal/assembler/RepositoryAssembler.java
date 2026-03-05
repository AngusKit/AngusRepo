package cloud.xcan.angus.core.repo.interfaces.repository.facade.internal.assembler;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.dto.RepositoryCreateDto;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.dto.RepositoryFindDto;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.dto.RepositoryUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.vo.RepositoryDetailVo;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.vo.RepositoryUrlVo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

public class RepositoryAssembler {

  public static RepoEntity toCreateEntity(RepositoryCreateDto dto) {
    RepoEntity entity = new RepoEntity();
    entity.setName(dto.getName());
    entity.setFormat(dto.getFormat());
    entity.setType(dto.getType());
    entity.setDescription(dto.getDescription());
    entity.setRemoteUrl(dto.getRemoteUrl());
    entity.setBlobStore(dto.getBlobStore());
    entity.setSettings(dto.getSettings());
    return entity;
  }

  public static RepoEntity toUpdateEntity(RepositoryUpdateDto dto, Long id) {
    RepoEntity entity = new RepoEntity();
    entity.setId(id);
    entity.setName(dto.getName());
    entity.setDescription(dto.getDescription());
    entity.setRemoteUrl(dto.getRemoteUrl());
    entity.setSettings(dto.getSettings());
    return entity;
  }

  public static RepositoryDetailVo toDetailVo(RepoEntity entity) {
    if (entity == null) {
      return null;
    }
    RepositoryDetailVo vo = new RepositoryDetailVo();
    vo.setId(entity.getId());
    if (entity.getTenantId() != null) {
      vo.setTenantId(entity.getTenantId());
    }
    vo.setName(entity.getName());
    vo.setFormat(entity.getFormat());
    vo.setType(entity.getType());
    vo.setDescription(entity.getDescription());
    vo.setArtifacts(entity.getArtifacts());
    vo.setSizeBytes(entity.getSizeBytes());
    vo.setUrl(entity.getUrl());
    vo.setStatus(entity.getStatus());
    vo.setRemoteUrl(entity.getRemoteUrl());
    vo.setBlobStore(entity.getBlobStore());
    vo.setSettings(entity.getSettings());
    vo.setCreatedBy(entity.getCreatedBy());
    vo.setCreatorName(entity.getCreatorName());
    vo.setCreatedDate(entity.getCreatedDate());
    vo.setModifiedBy(entity.getModifiedBy());
    vo.setModifiedDate(entity.getModifiedDate());
    return vo;
  }

  public static RepositoryUrlVo toUrlVo(RepoEntity entity) {
    RepositoryUrlVo vo = new RepositoryUrlVo();
    vo.setId(entity.getId());
    vo.setName(entity.getName());
    vo.setUrl(entity.getUrl());
    return vo;
  }

  public static GenericSpecification<RepoEntity> getSpecification(RepositoryFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .matchSearchFields("name", "description")
        .orderByFields("id", "name", "createdDate", "format", "type", "status")
        .inAndNotFields("format", "type", "status")
        .build();
    return new GenericSpecification<>(filters);
  }
}
