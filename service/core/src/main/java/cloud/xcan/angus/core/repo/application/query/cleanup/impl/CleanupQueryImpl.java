package cloud.xcan.angus.core.repo.application.query.cleanup.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.repo.application.query.cleanup.CleanupQuery;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupExecution;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupExecutionRepo;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupPolicy;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupPolicyListRepo;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupPolicyRepo;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupPolicySearchRepo;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupStatus;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.vo.CleanupStatisticsVo;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Biz
public class CleanupQueryImpl implements CleanupQuery {

  @Resource
  private CleanupPolicyRepo cleanupPolicyRepo;

  @Resource
  private CleanupPolicyListRepo cleanupPolicyListRepo;

  @Resource
  private CleanupPolicySearchRepo cleanupPolicySearchRepo;

  @Resource
  private CleanupExecutionRepo cleanupExecutionRepo;

  @Override
  public Page<CleanupPolicy> find(GenericSpecification<CleanupPolicy> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<CleanupPolicy>>() {
      @Override
      protected Page<CleanupPolicy> process() {
        return fullTextSearch
            ? cleanupPolicySearchRepo.find(spec.getCriteria(), pageable, CleanupPolicy.class, match)
            : cleanupPolicyListRepo.find(spec.getCriteria(), pageable, CleanupPolicy.class, null);
      }
    }.execute();
  }

  @Override
  public Optional<CleanupPolicy> findPolicyById(String id) {
    return cleanupPolicyRepo.findByTenantIdAndId(PrincipalContext.getTenantId(), id);
  }

  @Override
  public CleanupPolicy findPolicyAndCheck(String id) {
    return cleanupPolicyRepo.findByTenantIdAndId(PrincipalContext.getTenantId(), id)
        .orElseThrow(() -> new RuntimeException("清理策略不存在: " + id));
  }

  @Override
  public List<CleanupPolicy> findPoliciesByRepositoryId(String repositoryId) {
    return cleanupPolicyRepo.findByTenantIdAndRepositoryId(
        PrincipalContext.getTenantId(), repositoryId);
  }

  @Override
  public List<CleanupExecution> findExecutionsByPolicyId(String policyId) {
    return cleanupExecutionRepo.findByTenantIdAndPolicyIdOrderByCreatedDateDesc(
        PrincipalContext.getTenantId(), policyId);
  }

  @Override
  public Optional<CleanupExecution> findExecutionById(String id) {
    return cleanupExecutionRepo.findByTenantIdAndId(PrincipalContext.getTenantId(), id);
  }

  @Override
  public CleanupStatisticsVo getStatistics() {
    return new BizTemplate<CleanupStatisticsVo>() {
      @Override
      protected CleanupStatisticsVo process() {
        String tenantId = PrincipalContext.getTenantId();
        CleanupStatisticsVo stats = new CleanupStatisticsVo();
        stats.setTotalPolicies(cleanupPolicyRepo.countTotalPolicies(tenantId));
        stats.setEnabledPolicies(cleanupPolicyRepo.countEnabledPolicies(tenantId));
        stats.setTotalExecutions(cleanupExecutionRepo.countTotalExecutions(tenantId));
        stats.setCompletedExecutions(
            cleanupExecutionRepo.countByStatus(tenantId, CleanupStatus.COMPLETED));
        stats.setFailedExecutions(
            cleanupExecutionRepo.countByStatus(tenantId, CleanupStatus.FAILED));
        stats.setTotalDeletedArtifacts(cleanupExecutionRepo.sumDeletedArtifacts(tenantId));
        stats.setTotalFreedSpaceBytes(cleanupExecutionRepo.sumFreedSpaceBytes(tenantId));
        return stats;
      }
    }.execute();
  }
}
