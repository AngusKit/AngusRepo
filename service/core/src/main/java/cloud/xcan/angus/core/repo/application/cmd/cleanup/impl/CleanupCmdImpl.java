package cloud.xcan.angus.core.repo.application.cmd.cleanup.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.repo.application.cmd.cleanup.CleanupCmd;
import cloud.xcan.angus.core.repo.application.query.cleanup.CleanupQuery;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupExecution;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupExecutionRepo;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupPolicy;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupPolicyRepo;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupStatus;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Biz
public class CleanupCmdImpl extends CommCmd<CleanupPolicy, String> implements CleanupCmd {

  @Resource
  private CleanupPolicyRepo cleanupPolicyRepo;

  @Resource
  private CleanupExecutionRepo cleanupExecutionRepo;

  @Resource
  private CleanupQuery cleanupQuery;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public CleanupPolicy create(CleanupPolicy policy) {
    return new BizTemplate<CleanupPolicy>() {
      @Override
      protected void checkParams() {
        if (cleanupPolicyRepo.findByTenantIdAndNameAndRepositoryId(
            policy.getTenantId(), policy.getName(), policy.getRepositoryId()).isPresent()) {
          throw ProtocolException.of("清理策略已存在：{0}", new Object[]{policy.getName()});
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
        log.info("Cleanup policy created: name={}, id={}", policy.getName(), policy.getId());
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
        existing = cleanupQuery.findPolicyAndCheck(policy.getId());
        if (policy.getName() != null && !policy.getName().equals(existing.getName())) {
          if (cleanupPolicyRepo.existsByTenantIdAndNameAndRepositoryIdAndIdNot(
              existing.getTenantId(), policy.getName(), existing.getRepositoryId(), existing.getId())) {
            throw ProtocolException.of("清理策略名称已存在：{0}", new Object[]{policy.getName()});
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
        log.info("Cleanup policy updated: id={}, name={}", policy.getId(), policy.getName());
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(String id) {
    log.warn("Cleanup policy deleted: id={}", id);
    cleanupPolicyRepo.deleteById(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteBatch(List<String> ids) {
    log.warn("Cleanup policies deleted in batch: count={}", ids.size());
    cleanupPolicyRepo.deleteAllById(ids);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateEnabled(String id, Boolean enabled, Long modifiedBy) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        if (!cleanupPolicyRepo.existsById(id)) {
          throw ResourceNotFound.of(id, "CleanupPolicy");
        }
      }

      @Override
      protected Void process() {
        cleanupPolicyRepo.updateEnabled(
            null, id, enabled, LocalDateTime.now(), modifiedBy);
        log.info("Cleanup policy enabled status updated: id={}, enabled={}", id, enabled);
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
          throw ResourceNotFound.of(policyId, "CleanupPolicy");
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
        log.info("Cleanup execution created: policyId={}, executionId={}", policyId, execution.getId());
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
            .orElseThrow(() -> ResourceNotFound.of(executionId, "CleanupExecution"));
        if (!execution.isRunning()) {
          throw ProtocolException.of("执行已完成，无法取消");
        }
      }

      @Override
      protected Void process() {
        execution.setStatus(CleanupStatus.CANCELLED);
        execution.setEndTime(LocalDateTime.now());
        cleanupExecutionRepo.save(execution);
        log.info("Cleanup execution cancelled: executionId={}", executionId);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<CleanupPolicy, String> getRepository() {
    return this.cleanupPolicyRepo;
  }
}
