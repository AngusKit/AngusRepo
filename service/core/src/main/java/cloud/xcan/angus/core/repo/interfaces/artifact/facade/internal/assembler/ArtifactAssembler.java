package cloud.xcan.angus.core.repo.interfaces.artifact.facade.internal.assembler;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.core.repo.domain.artifact.Artifact;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.dto.ArtifactCreateDto;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.dto.ArtifactFindDto;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.dto.ArtifactUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.vo.ArtifactDetailVo;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.vo.ArtifactVersionVo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

public class ArtifactAssembler {

  public static Artifact toCreateEntity(ArtifactCreateDto dto) {
    Artifact entity = new Artifact();
    entity.setRepositoryId(dto.getRepositoryId());
    entity.setName(dto.getName());
    entity.setPath(dto.getPath());
    entity.setVersion(dto.getVersion());
    entity.setDescription(dto.getDescription());
    entity.setSizeBytes(dto.getSizeBytes());
    entity.setChecksum(dto.getChecksum());
    entity.setLicense(dto.getLicense());
    entity.setTags(dto.getTags());
    entity.setMetadata(dto.getMetadata());
    return entity;
  }

  public static Artifact toUpdateEntity(ArtifactUpdateDto dto, Long id) {
    Artifact entity = new Artifact();
    entity.setId(id);
    entity.setName(dto.getName());
    entity.setDescription(dto.getDescription());
    entity.setLicense(dto.getLicense());
    entity.setTags(dto.getTags());
    entity.setMetadata(dto.getMetadata());
    return entity;
  }

  public static ArtifactDetailVo toDetailVo(Artifact entity) {
    if (entity == null) {
      return null;
    }
    ArtifactDetailVo vo = new ArtifactDetailVo();
    vo.setId(entity.getId());
    vo.setRepositoryId(entity.getRepositoryId());
    vo.setRepositoryName(entity.getRepositoryName());
    vo.setFormat(entity.getFormat());
    vo.setName(entity.getName());
    vo.setPath(entity.getPath());
    vo.setVersion(entity.getVersion());
    vo.setDescription(entity.getDescription());
    vo.setSizeBytes(entity.getSizeBytes());
    vo.setChecksum(entity.getChecksum());
    vo.setDownloads(entity.getDownloads());
    vo.setStars(entity.getStars());
    vo.setLicense(entity.getLicense());
    vo.setIsLatest(entity.getIsLatest());
    vo.setTags(entity.getTags());
    vo.setVersions(entity.getVersions());
    vo.setVulnerability(entity.getVulnerability());
    vo.setMetadata(entity.getMetadata());
    vo.setCreatedBy(entity.getCreatedBy());
    vo.setCreatedDate(entity.getCreatedDate());
    vo.setModifiedBy(entity.getModifiedBy());
    vo.setModifiedDate(entity.getModifiedDate());
    return vo;
  }

  public static ArtifactVersionVo toVersionVo(Artifact entity) {
    if (entity == null) {
      return null;
    }
    ArtifactVersionVo vo = new ArtifactVersionVo();
    vo.setId(entity.getId());
    vo.setVersion(entity.getVersion());
    vo.setSizeBytes(entity.getSizeBytes());
    vo.setChecksum(entity.getChecksum());
    vo.setDownloads(entity.getDownloads());
    vo.setIsLatest(entity.getIsLatest());
    vo.setCreatedDate(entity.getCreatedDate());
    return vo;
  }

  public static GenericSpecification<Artifact> getSpecification(ArtifactFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .matchSearchFields("name", "description")
        .orderByFields("id", "name", "createdDate", "downloads", "stars", "sizeBytes")
        .inAndNotFields("format", "repositoryId")
        .build();
    return new GenericSpecification<>(filters);
  }
}
