package cloud.xcan.angus.core.repo.application.cmd.cleanup.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.repo.application.cmd.cleanup.CleanupCmd;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupExecution;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupExecutionRepo;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupPolicy;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupPolicyRepo;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class CleanupCmdImpl extends CommCmd<CleanupPolicy, String> implements CleanupCmd {

  @Autowired(required = false)
  private CleanupPolicyRepo cleanupPolicyRepo;

  @Autowired(required = false)
  private CleanupExecutionRepo cleanupExecutionRepo;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CleanupPolicy create(CleanupPolicy policy) {
    return new BizTemplate<CleanupPolicy>() {
      @Override
      protected void checkParams() {
        if (cleanupPolicyRepo.findByTenantIdAndNameAndRepositoryId(
            policy.getTenantId(), policy.getName(), policy.getRepositoryId()).isPresent()) {
          throw new RuntimeException(
              "清理策略已存在: " + policy.getName());
        }
      }

      @Override
      protected CleanupPolicy process() {
        policy.setId(UUID.randomUUID().toString());
        policy.setCreatedDate(LocalDateTime.now());
        policy.setModifiedDate(LocalDateTime.now());
        if (policy.getEnabled() == null) {
          policy.setEnabled(true);
        }
        if (policy.getDryRun() == null) {
          policy.setDryRun(false);
        }
        if (policy.getExecutionCount() == null) {
          policy.setExecutionCount(0);
        }
        insert0(policy);
        return policy;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CleanupPolicy update(CleanupPolicy policy) {
    return new BizTemplate<CleanupPolicy>() {
      CleanupPolicy existing;

      @Override
      protected void checkParams() {
        existing = cleanupPolicyRepo.findById(policy.getId())
            .orElseThrow(() -> new RuntimeException("清理策略不存在: " + policy.getId()));
        if (policy.getName() != null && !policy.getName().equals(existing.getName())) {
          if (cleanupPolicyRepo.existsByTenantIdAndNameAndRepositoryIdAndIdNot(
              existing.getTenantId(), policy.getName(), existing.getRepositoryId(), existing.getId())) {
            throw new RuntimeException("清理策略名称已存在: " + policy.getName());
          }
        }
      }

      @Override
      protected CleanupPolicy process() {
        if (policy.getName() != null) {
          existing.setName(policy.getName());
        }
        if (policy.getDescription() != null) {
          existing.setDescription(policy.getDescription());
        }
        if (policy.getType() != null) {
          existing.setType(policy.getType());
        }
        if (policy.getEnabled() != null) {
          existing.setEnabled(policy.getEnabled());
        }
        if (policy.getDryRun() != null) {
          existing.setDryRun(policy.getDryRun());
        }
        if (policy.getConditionJson() != null) {
          existing.setConditionJson(policy.getConditionJson());
        }
        if (policy.getScheduleJson() != null) {
          existing.setScheduleJson(policy.getScheduleJson());
        }
        existing.setModifiedBy(policy.getModifiedBy());
        existing.setModifiedDate(LocalDateTime.now());
        cleanupPolicyRepo.save(existing);
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(String id) {
    cleanupPolicyRepo.deleteById(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteBatch(List<String> ids) {
    cleanupPolicyRepo.deleteAllById(ids);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateEnabled(String id, Boolean enabled, Long modifiedBy) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        if (!cleanupPolicyRepo.existsById(id)) {
          throw new RuntimeException("清理策略不存在: " + id);
        }
      }

      @Override
      protected Void process() {
        cleanupPolicyRepo.updateEnabled(
            null, id, enabled, LocalDateTime.now(), modifiedBy);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CleanupExecution execute(String policyId) {
    return new BizTemplate<CleanupExecution>() {
      @Override
      protected void checkParams() {
        if (!cleanupPolicyRepo.existsById(policyId)) {
          throw new RuntimeException("清理策略不存在: " + policyId);
        }
      }

      @Override
      protected CleanupExecution process() {
        CleanupExecution execution = new CleanupExecution();
        execution.setId(UUID.randomUUID().toString());
        execution.setPolicyId(policyId);
        execution.setStatus(CleanupStatus.PENDING);
        execution.setProgress(0);
        execution.setCreatedDate(LocalDateTime.now());
        cleanupExecutionRepo.save(execution);
        return execution;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void cancelExecution(String executionId) {
    new BizTemplate<Void>() {
      CleanupExecution execution;

      @Override
      protected void checkParams() {
        execution = cleanupExecutionRepo.findById(executionId)
            .orElseThrow(() -> new RuntimeException("执行记录不存在: " + executionId));
        if (!execution.isRunning()) {
          throw new RuntimeException("执行已完成，无法取消: " + executionId);
        }
      }

      @Override
      protected Void process() {
        execution.setStatus(CleanupStatus.CANCELLED);
        execution.setEndTime(LocalDateTime.now());
        cleanupExecutionRepo.save(execution);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<CleanupPolicy, String> getRepository() {
    return this.cleanupPolicyRepo;
  }
}
