package cloud.xcan.angus.core.repo.application.cmd.repository.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.repo.application.cmd.repository.RepositoryCmd;
import cloud.xcan.angus.core.repo.application.query.repository.RepositoryQuery;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntityRepo;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryStatus;
import cloud.xcan.angus.remote.message.ProtocolException;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Biz
public class RepositoryCmdImpl extends CommCmd<RepoEntity, Long> implements RepositoryCmd {

  @Resource
  private RepoEntityRepo repoEntityRepo;

  @Resource
  private RepositoryQuery repositoryQuery;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public RepoEntity create(RepoEntity repository) {
    return new BizTemplate<RepoEntity>() {
      @Override
      protected void checkParams() {
        // 检查名称唯一性
        if (repoEntityRepo.existsByName(repository.getName())) {
          throw ProtocolException.of("仓库名称已存在：{0}", 
              new Object[]{repository.getName()});
        }
      }

      @Override
      protected RepoEntity process() {
        repository.setCreatedDate(LocalDateTime.now());
        repository.setModifiedDate(LocalDateTime.now());
        if (repository.getStatus() == null) {
          repository.setStatus(RepositoryStatus.ONLINE);
        }
        if (repository.getArtifacts() == null) {
          repository.setArtifacts(0);
        }
        if (repository.getSizeBytes() == null) {
          repository.setSizeBytes(0L);
        }
        insert0(repository);
        log.info("Repository created: name={}, id={}", repository.getName(), repository.getId());
        return repository;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public RepoEntity update(RepoEntity repository) {
    return new BizTemplate<RepoEntity>() {
      RepoEntity existing;

      @Override
      protected void checkParams() {
        existing = repositoryQuery.findAndCheck(repository.getId());
      }

      @Override
      protected RepoEntity process() {
        existing.setName(repository.getName());
        existing.setDescription(repository.getDescription());
        existing.setRemoteUrl(repository.getRemoteUrl());
        existing.setSettings(repository.getSettings());
        existing.setModifiedBy(repository.getModifiedBy());
        existing.setModifiedDate(LocalDateTime.now());
        repoEntityRepo.save(existing);
        log.info("Repository updated: id={}, name={}", repository.getId(), repository.getName());
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public RepoEntity updateStatus(Long id, RepositoryStatus status) {
    return new BizTemplate<RepoEntity>() {
      RepoEntity existing;

      @Override
      protected void checkParams() {
        existing = repositoryQuery.findAndCheck(id);
      }

      @Override
      protected RepoEntity process() {
        existing.setStatus(status);
        existing.setModifiedDate(LocalDateTime.now());
        repoEntityRepo.save(existing);
        log.info("Repository status updated: id={}, status={}", id, status);
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        log.warn("Repository deleted: id={}", id);
        repoEntityRepo.deleteById(id);
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
        log.warn("Repositories deleted in batch: count={}", ids.size());
        repoEntityRepo.deleteAllById(ids);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<RepoEntity, Long> getRepository() {
    return this.repoEntityRepo;
  }
}
