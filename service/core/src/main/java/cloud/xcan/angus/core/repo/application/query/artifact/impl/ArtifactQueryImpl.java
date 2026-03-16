package cloud.xcan.angus.core.repo.application.query.artifact.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.repo.application.query.artifact.ArtifactQuery;
import cloud.xcan.angus.core.repo.domain.artifact.Artifact;
import cloud.xcan.angus.core.repo.domain.artifact.ArtifactListRepo;
import cloud.xcan.angus.core.repo.domain.artifact.ArtifactRepo;
import cloud.xcan.angus.core.repo.domain.artifact.ArtifactSearchRepo;
import cloud.xcan.angus.core.repo.domain.artifact.ArtifactStarRepo;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.vo.ArtifactStatisticsVo;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@Biz
@Transactional(readOnly = true)
public class ArtifactQueryImpl implements ArtifactQuery {

  @Resource
  private ArtifactRepo artifactRepo;

  @Resource
  private ArtifactListRepo artifactListRepo;

  @Resource
  private ArtifactSearchRepo artifactSearchRepo;

  @Resource
  private ArtifactStarRepo artifactStarRepo;

  @Override
  public Page<Artifact> find(GenericSpecification<Artifact> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Artifact>>() {
      @Override
      protected Page<Artifact> process() {
        return fullTextSearch
            ? artifactSearchRepo.find(spec.getCriteria(), pageable, Artifact.class, match)
            : artifactListRepo.find(spec.getCriteria(), pageable, Artifact.class, null);
      }
    }.execute();
  }

  @Override
  public Optional<Artifact> findById(Long id) {
    return artifactRepo.findById(id);
  }

  @Override
  public Artifact findAndCheck(Long id) {
    return artifactRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("制品不存在: " + id));
  }

  @Override
  public ArtifactStatisticsVo getStatistics() {
    return new BizTemplate<ArtifactStatisticsVo>() {
      @Override
      protected ArtifactStatisticsVo process() {
        ArtifactStatisticsVo stats = new ArtifactStatisticsVo();
        stats.setTotalArtifacts(artifactRepo.count());
        // Calculate total downloads and total size via all artifacts
        List<Artifact> all = artifactRepo.findAll();
        long totalDownloads = 0;
        long totalSize = 0;
        String topDownloaded = null;
        int maxDownloads = 0;
        for (Artifact a : all) {
          totalDownloads += (a.getDownloads() != null ? a.getDownloads() : 0);
          totalSize += (a.getSizeBytes() != null ? a.getSizeBytes() : 0);
          if (a.getDownloads() != null && a.getDownloads() > maxDownloads) {
            maxDownloads = a.getDownloads();
            topDownloaded = a.getName();
          }
        }
        stats.setTotalDownloads(totalDownloads);
        stats.setTotalSize(totalSize);
        stats.setAverageSize(all.isEmpty() ? 0 : totalSize / all.size());
        stats.setTopDownloaded(topDownloaded);
        return stats;
      }
    }.execute();
  }

  @Override
  public List<Artifact> findVersions(Long id) {
    Artifact artifact = findAndCheck(id);
    return artifactRepo.findByRepositoryId(artifact.getRepositoryId())
        .stream()
        .filter(a -> a.getName().equals(artifact.getName()))
        .toList();
  }

  @Override
  public boolean isStarredByUser(Long artifactId, Long userId) {
    return artifactStarRepo.existsByArtifactIdAndUserId(artifactId, userId);
  }
}
