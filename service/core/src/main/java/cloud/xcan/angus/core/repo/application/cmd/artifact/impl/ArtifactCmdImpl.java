package cloud.xcan.angus.core.repo.application.cmd.artifact.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.repo.application.cmd.artifact.ArtifactCmd;
import cloud.xcan.angus.core.repo.domain.artifact.Artifact;
import cloud.xcan.angus.core.repo.domain.artifact.ArtifactRepo;
import cloud.xcan.angus.core.repo.domain.artifact.ArtifactStar;
import cloud.xcan.angus.core.repo.domain.artifact.ArtifactStarRepo;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class ArtifactCmdImpl extends CommCmd<Artifact, Long> implements ArtifactCmd {

  @Autowired(required = false)
  private ArtifactRepo artifactRepo;

  @Autowired(required = false)
  private ArtifactStarRepo artifactStarRepo;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Artifact create(Artifact artifact) {
    return new BizTemplate<Artifact>() {
      @Override
      protected void checkParams() {
        if (artifactRepo.existsByRepositoryIdAndNameAndVersion(
            artifact.getRepositoryId(), artifact.getName(), artifact.getVersion())) {
          throw new RuntimeException(
              "制品已存在: " + artifact.getName() + ":" + artifact.getVersion());
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
        existing = artifactRepo.findById(artifact.getId())
            .orElseThrow(() -> new RuntimeException("制品不存在: " + artifact.getId()));
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
        existing = artifactRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("制品不存在: " + id));
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
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    artifactRepo.deleteById(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteBatch(List<Long> ids) {
    artifactRepo.deleteAllById(ids);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void incrementDownloads(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        if (!artifactRepo.existsById(id)) {
          throw new RuntimeException("制品不存在: " + id);
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
          throw new RuntimeException("制品不存在: " + artifactId);
        }
        if (artifactStarRepo.existsByArtifactIdAndUserId(artifactId, userId)) {
          throw new RuntimeException("已收藏该制品");
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
          throw new RuntimeException("制品不存在: " + artifactId);
        }
        if (!artifactStarRepo.existsByArtifactIdAndUserId(artifactId, userId)) {
          throw new RuntimeException("未收藏该制品");
        }
      }

      @Override
      protected Void process() {
        artifactStarRepo.deleteByArtifactIdAndUserId(artifactId, userId);
        artifactRepo.updateStarCount(artifactId, -1);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Artifact, Long> getRepository() {
    return this.artifactRepo;
  }
}
