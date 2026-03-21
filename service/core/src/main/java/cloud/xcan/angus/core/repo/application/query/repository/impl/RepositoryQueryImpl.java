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
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
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
    return new BizTemplate<Optional<RepoEntity>>() {
      @Override
      protected Optional<RepoEntity> process() {
        return repoEntityRepo.findById(id);
      }
    }.execute();
  }

  @Override
  public RepoEntity findAndCheck(Long id) {
    return new BizTemplate<RepoEntity>() {
      @Override
      protected RepoEntity process() {
        return repoEntityRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(id, "Repository"));
      }
    }.execute();
  }

  @Override
  public RepoEntity findByNameAndCheck(String name) {
    return new BizTemplate<RepoEntity>() {
      @Override
      protected RepoEntity process() {
        return repoEntityRepo.findByName(name)
            .orElseThrow(() -> ResourceNotFound.of(name, "Repository"));
      }
    }.execute();
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
