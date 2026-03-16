package cloud.xcan.angus.core.repo.interfaces.artifact.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.repo.interfaces.artifact.facade.internal.assembler.ArtifactAssembler.getSpecification;
import static cloud.xcan.angus.core.repo.interfaces.artifact.facade.internal.assembler.ArtifactAssembler.toCreateEntity;
import static cloud.xcan.angus.core.repo.interfaces.artifact.facade.internal.assembler.ArtifactAssembler.toDetailVo;
import static cloud.xcan.angus.core.repo.interfaces.artifact.facade.internal.assembler.ArtifactAssembler.toUpdateEntity;
import static cloud.xcan.angus.core.repo.interfaces.artifact.facade.internal.assembler.ArtifactAssembler.toVersionVo;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.repo.application.cmd.artifact.ArtifactCmd;
import cloud.xcan.angus.core.repo.application.query.artifact.ArtifactQuery;
import cloud.xcan.angus.core.repo.domain.artifact.Artifact;
import cloud.xcan.angus.core.repo.domain.format.store.BlobStore;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.ArtifactFacade;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.dto.ArtifactBatchDeleteDto;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.dto.ArtifactCreateDto;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.dto.ArtifactFindDto;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.dto.ArtifactUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.internal.assembler.ArtifactAssembler;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.vo.ArtifactDetailVo;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.vo.ArtifactStatisticsVo;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.vo.ArtifactVersionVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ArtifactFacadeImpl implements ArtifactFacade {

  @Resource
  private ArtifactCmd artifactCmd;

  @Resource
  private ArtifactQuery artifactQuery;

  @Resource
  private BlobStore blobStore;

  @Override
  public ArtifactDetailVo create(ArtifactCreateDto dto) {
    Artifact entity = toCreateEntity(dto);
    Artifact created = artifactCmd.create(entity);
    return toDetailVo(created);
  }

  @Override
  public ArtifactDetailVo update(Long id, ArtifactUpdateDto dto) {
    Artifact entity = toUpdateEntity(dto, id);
    Artifact updated = artifactCmd.update(entity);
    return toDetailVo(updated);
  }

  @Override
  public void markLatest(Long id) {
    artifactCmd.markLatest(id);
  }

  @Override
  public void delete(Long id) {
    artifactCmd.delete(id);
  }

  @Override
  public void deleteBatch(ArtifactBatchDeleteDto dto) {
    artifactCmd.deleteBatch(dto.getIds());
  }

  @Override
  public ArtifactDetailVo getById(Long id) {
    Artifact entity = artifactQuery.findAndCheck(id);
    return toDetailVo(entity);
  }

  @Override
  public PageResult<ArtifactDetailVo> list(ArtifactFindDto dto) {
    Page<Artifact> page = artifactQuery.find(
        getSpecification(dto),
        dto.tranPage(),
        dto.fullTextSearch,
        getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, ArtifactAssembler::toDetailVo);
  }

  @Override
  public ArtifactStatisticsVo getStatistics() {
    return artifactQuery.getStatistics();
  }

  @Override
  public void download(Long id, HttpServletResponse response) {
    Artifact artifact = artifactQuery.findAndCheck(id);
    artifactCmd.incrementDownloads(id);
    response.setContentType("application/octet-stream");
    response.setHeader("Content-Disposition",
        "attachment; filename=\"" + artifact.getName() + "\"");
    if (artifact.getSizeBytes() != null) {
      response.setContentLengthLong(artifact.getSizeBytes());
    }

    // Read artifact file from blob storage and write to response output stream
    String tenantId = PrincipalContext.get().getTenantId().toString();
    String repositoryId = artifact.getRepositoryId().toString();
    String path = artifact.getPath() != null ? artifact.getPath() : artifact.getName();
    try (InputStream inputStream = blobStore.retrieve(tenantId, repositoryId, path);
         OutputStream outputStream = response.getOutputStream()) {
      byte[] buffer = new byte[8192];
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }
      outputStream.flush();
    } catch (IOException e) {
      log.error("Failed to download artifact: id={}, path={}", id, path, e);
      throw new RuntimeException("Failed to download artifact file", e);
    }
  }

  @Override
  public String getDownloadUrl(Long id) {
    Artifact artifact = artifactQuery.findAndCheck(id);
    return "/api/v1/artifacts/" + artifact.getId() + "/download";
  }

  @Override
  public void addStar(Long artifactId, Long userId) {
    artifactCmd.addStar(artifactId, userId);
  }

  @Override
  public void removeStar(Long artifactId, Long userId) {
    artifactCmd.removeStar(artifactId, userId);
  }

  @Override
  public List<ArtifactVersionVo> getVersions(Long id) {
    List<Artifact> versions = artifactQuery.findVersions(id);
    return versions.stream().map(ArtifactAssembler::toVersionVo).toList();
  }
}
