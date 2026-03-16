package cloud.xcan.angus.core.repo.application.query.repository.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.repo.application.query.repository.RepositoryQuery;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntityListRepo;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntityRepo;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntitySearchRepo;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryStatus;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.vo.RepositoryStatisticsVo;
import jakarta.annotation.Resource;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@Biz
@Transactional(readOnly = true)
public class RepositoryQueryImpl implements RepositoryQuery {

  @Resource
  private RepoEntityRepo repoEntityRepo;

  @Resource
  private RepoEntityListRepo repoEntityListRepo;

  @Resource
  private RepoEntitySearchRepo repoEntitySearchRepo;

  @Override
  public Page<RepoEntity> find(GenericSpecification<RepoEntity> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<RepoEntity>>() {
      @Override
      protected Page<RepoEntity> process() {
        return fullTextSearch
            ? repoEntitySearchRepo.find(spec.getCriteria(), pageable, RepoEntity.class, match)
            : repoEntityListRepo.find(spec.getCriteria(), pageable, RepoEntity.class, null);
      }
    }.execute();
  }

  @Override
  public Optional<RepoEntity> findById(Long id) {
    return repoEntityRepo.findById(id);
  }

  @Override
  public RepoEntity findAndCheck(Long id) {
    return repoEntityRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("仓库不存在: " + id));
  }

  @Override
  public RepoEntity findByNameAndCheck(String name) {
    return repoEntityRepo.findByName(name)
        .orElseThrow(() -> new RuntimeException("仓库不存在: " + name));
  }

  @Override
  public RepositoryStatisticsVo getStatistics() {
    return new BizTemplate<RepositoryStatisticsVo>() {
      @Override
      protected RepositoryStatisticsVo process() {
        RepositoryStatisticsVo stats = new RepositoryStatisticsVo();
        stats.setTotalRepositories(repoEntityRepo.count());
        stats.setOnlineRepositories(repoEntityRepo.countByStatus(RepositoryStatus.ONLINE));
        stats.setOfflineRepositories(repoEntityRepo.countByStatus(RepositoryStatus.OFFLINE));
        stats.setMavenRepositories(repoEntityRepo.countByFormat(RepositoryFormat.MAVEN));
        stats.setDockerRepositories(repoEntityRepo.countByFormat(RepositoryFormat.DOCKER));
        stats.setNpmRepositories(repoEntityRepo.countByFormat(RepositoryFormat.NPM));
        return stats;
      }
    }.execute();
  }
}
