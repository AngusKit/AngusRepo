package cloud.xcan.angus.core.repo.application.cmd.artifact.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.repo.application.cmd.artifact.ArtifactCmd;
import cloud.xcan.angus.core.repo.domain.artifact.Artifact;
import cloud.xcan.angus.core.repo.domain.artifact.ArtifactRepo;
import cloud.xcan.angus.core.repo.application.query.artifact.ArtifactQuery;
import cloud.xcan.angus.core.repo.domain.artifact.ArtifactStar;
import cloud.xcan.angus.core.repo.domain.artifact.ArtifactStarRepo;
import cloud.xcan.angus.core.repo.domain.format.store.BlobStore;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Biz
public class ArtifactCmdImpl extends CommCmd<Artifact, Long> implements ArtifactCmd {

  @Resource
  private ArtifactRepo artifactRepo;

  @Resource
  private ArtifactStarRepo artifactStarRepo;

  @Resource
  private BlobStore blobStore;

  @Resource
  private ArtifactQuery artifactQuery;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Artifact create(Artifact artifact) {
    return new BizTemplate<Artifact>() {
      @Override
      protected void checkParams() {
        if (artifactRepo.existsByRepositoryIdAndNameAndVersion(
            artifact.getRepositoryId(), artifact.getName(), artifact.getVersion())) {
          throw ProtocolException.of("制品已存在：{0}:{1}", 
              new Object[]{artifact.getName(), artifact.getVersion()});
        }
      }

      @Override
      protected Artifact process() {
        artifact.setCreatedDate(LocalDateTime.now());
        artifact.setModifiedDate(LocalDateTime.now());
        if (artifact.getDownloads() == null) {
          artifact.setDownloads(0);
        }
        if (artifact.getStars() == null) {
          artifact.setStars(0);
        }
        if (artifact.getIsLatest() == null) {
          artifact.setIsLatest(false);
        }
        if (artifact.getSizeBytes() == null) {
          artifact.setSizeBytes(0L);
        }
        insert0(artifact);
        log.info("Artifact created: name={}, version={}, id={}", 
            artifact.getName(), artifact.getVersion(), artifact.getId());
        return artifact;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Artifact update(Artifact artifact) {
    return new BizTemplate<Artifact>() {
      Artifact existing;

      @Override
      protected void checkParams() {
        existing = artifactQuery.findAndCheck(artifact.getId());
      }

      @Override
      protected Artifact process() {
        if (artifact.getName() != null) {
          existing.setName(artifact.getName());
        }
        if (artifact.getDescription() != null) {
          existing.setDescription(artifact.getDescription());
        }
        if (artifact.getLicense() != null) {
          existing.setLicense(artifact.getLicense());
        }
        if (artifact.getTags() != null) {
          existing.setTags(artifact.getTags());
        }
        if (artifact.getMetadata() != null) {
          existing.setMetadata(artifact.getMetadata());
        }
        existing.setModifiedBy(artifact.getModifiedBy());
        existing.setModifiedDate(LocalDateTime.now());
        artifactRepo.save(existing);
        log.info("Artifact updated: id={}, name={}", artifact.getId(), artifact.getName());
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void markLatest(Long id) {
    new BizTemplate<Void>() {
      Artifact existing;

      @Override
      protected void checkParams() {
        existing = artifactQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        // Reset isLatest for all artifacts with same name in same repository
        List<Artifact> siblings = artifactRepo.findByRepositoryId(existing.getRepositoryId());
        for (Artifact sibling : siblings) {
          if (sibling.getName().equals(existing.getName()) && sibling.getIsLatest()) {
            artifactRepo.updateIsLatest(sibling.getId(), false);
          }
        }
        // Set current artifact as latest
        artifactRepo.updateIsLatest(id, true);
        log.info("Artifact marked as latest: id={}", id);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        log.warn("Artifact deleted: id={}", id);
        artifactRepo.deleteById(id);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteBatch(List<Long> ids) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        log.warn("Artifacts deleted in batch: count={}", ids.size());
        artifactRepo.deleteAllById(ids);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void incrementDownloads(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        if (!artifactRepo.existsById(id)) {
          throw ResourceNotFound.of(id, "Artifact");
        }
      }

      @Override
      protected Void process() {
        artifactRepo.updateDownloadCount(id, 1);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void addStar(Long artifactId, Long userId) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        if (!artifactRepo.existsById(artifactId)) {
          throw ResourceNotFound.of(artifactId, "Artifact");
        }
        if (artifactStarRepo.existsByArtifactIdAndUserId(artifactId, userId)) {
          throw ProtocolException.of("已收藏该制品");
        }
      }

      @Override
      protected Void process() {
        ArtifactStar star = new ArtifactStar();
        star.setArtifactId(artifactId);
        star.setUserId(userId);
        star.setStarredDate(LocalDateTime.now());
        artifactStarRepo.save(star);
        artifactRepo.updateStarCount(artifactId, 1);
        log.debug("Artifact starred: artifactId={}, userId={}", artifactId, userId);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void removeStar(Long artifactId, Long userId) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        if (!artifactRepo.existsById(artifactId)) {
          throw ResourceNotFound.of(artifactId, "Artifact");
        }
        if (!artifactStarRepo.existsByArtifactIdAndUserId(artifactId, userId)) {
          throw ProtocolException.of("未收藏该制品");
        }
      }

      @Override
      protected Void process() {
        artifactStarRepo.deleteByArtifactIdAndUserId(artifactId, userId);
        artifactRepo.updateStarCount(artifactId, -1);
        log.debug("Artifact unstarred: artifactId={}, userId={}", artifactId, userId);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void download(Long id, HttpServletResponse response) {
    new BizTemplate<Void>() {
      Artifact artifact;
      String tenantId;
      String repositoryId;
      String path;

      @Override
      protected void checkParams() {
        artifact = artifactQuery.findAndCheck(id);
        tenantId = PrincipalContext.get().getTenantId().toString();
        repositoryId = artifact.getRepositoryId().toString();
        path = artifact.getPath() != null ? artifact.getPath() : artifact.getName();
      }

      @Override
      protected Void process() {
        // Increment download count
        artifactRepo.updateDownloadCount(id, 1);
        
        // Set response headers
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition",
            "attachment; filename=\"" + artifact.getName() + "\"");
        if (artifact.getSizeBytes() != null) {
          response.setContentLengthLong(artifact.getSizeBytes());
        }

        // Read artifact file from blob storage and write to response output stream
        try (InputStream inputStream = blobStore.retrieve(tenantId, repositoryId, path);
             OutputStream outputStream = response.getOutputStream()) {
          byte[] buffer = new byte[8192];
          int bytesRead;
          while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
          }
          outputStream.flush();
          log.info("Artifact downloaded: id={}, path={}, tenantId={}", id, path, tenantId);
        } catch (IOException e) {
          log.error("Failed to download artifact: id={}, path={}", id, path, e);
          throw ProtocolException.of("制品下载失败，请稍后重试");
        }
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Artifact, Long> getRepository() {
    return this.artifactRepo;
  }
}
