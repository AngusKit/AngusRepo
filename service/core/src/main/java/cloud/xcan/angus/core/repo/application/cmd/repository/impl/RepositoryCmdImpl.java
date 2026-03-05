package cloud.xcan.angus.core.repo.application.cmd.repository.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.repo.application.cmd.repository.RepositoryCmd;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntityRepo;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class RepositoryCmdImpl extends CommCmd<RepoEntity, Long> implements RepositoryCmd {

  @Autowired(required = false)
  private RepoEntityRepo repoEntityRepo;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public RepoEntity create(RepoEntity repository) {
    return new BizTemplate<RepoEntity>() {
      @Override
      protected void checkParams() {
        // 检查名称唯一性
        if (repoEntityRepo.existsByName(repository.getName())) {
          throw new RuntimeException("仓库名称已存在: " + repository.getName());
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
        existing = repoEntityRepo.findById(repository.getId())
            .orElseThrow(() -> new RuntimeException("仓库不存在: " + repository.getId()));
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
        existing = repoEntityRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("仓库不存在: " + id));
      }

      @Override
      protected RepoEntity process() {
        existing.setStatus(status);
        existing.setModifiedDate(LocalDateTime.now());
        repoEntityRepo.save(existing);
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    repoEntityRepo.deleteById(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteBatch(List<Long> ids) {
    repoEntityRepo.deleteAllById(ids);
  }

  @Override
  protected BaseRepository<RepoEntity, Long> getRepository() {
    return this.repoEntityRepo;
  }
}
