package cloud.xcan.angus.core.repo.application.cmd.security.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.repo.application.cmd.security.ScanPolicyCmd;
import cloud.xcan.angus.core.repo.domain.security.ScanPolicy;
import cloud.xcan.angus.core.repo.domain.security.ScanPolicyRepo;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class ScanPolicyCmdImpl extends CommCmd<ScanPolicy, String> implements ScanPolicyCmd {

  @Autowired(required = false)
  private ScanPolicyRepo scanPolicyRepo;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ScanPolicy create(ScanPolicy policy) {
    return new BizTemplate<ScanPolicy>() {
      @Override
      protected void checkParams() {
        if (scanPolicyRepo.findByTenantIdAndNameAndRepositoryId(
            policy.getTenantId(), policy.getName(), policy.getRepositoryId()).isPresent()) {
          throw new RuntimeException("扫描策略已存在: " + policy.getName());
        }
      }

      @Override
      protected ScanPolicy process() {
        policy.setId(UUID.randomUUID().toString());
        policy.setCreatedDate(LocalDateTime.now());
        policy.setModifiedDate(LocalDateTime.now());
        if (policy.getEnabled() == null) {
          policy.setEnabled(true);
        }
        if (policy.getScanOnPush() == null) {
          policy.setScanOnPush(false);
        }
        if (policy.getAutoBlock() == null) {
          policy.setAutoBlock(false);
        }
        insert0(policy);
        return policy;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ScanPolicy update(ScanPolicy policy) {
    return new BizTemplate<ScanPolicy>() {
      ScanPolicy existing;

      @Override
      protected void checkParams() {
        existing = scanPolicyRepo.findById(policy.getId())
            .orElseThrow(() -> new RuntimeException("扫描策略不存在: " + policy.getId()));
        if (policy.getName() != null && !policy.getName().equals(existing.getName())) {
          if (scanPolicyRepo.existsByTenantIdAndNameAndRepositoryIdAndIdNot(
              existing.getTenantId(), policy.getName(), existing.getRepositoryId(), existing.getId())) {
            throw new RuntimeException("扫描策略名称已存在: " + policy.getName());
          }
        }
      }

      @Override
      protected ScanPolicy process() {
        if (policy.getName() != null) existing.setName(policy.getName());
        if (policy.getDescription() != null) existing.setDescription(policy.getDescription());
        if (policy.getScanType() != null) existing.setScanType(policy.getScanType());
        if (policy.getEnabled() != null) existing.setEnabled(policy.getEnabled());
        if (policy.getScanOnPush() != null) existing.setScanOnPush(policy.getScanOnPush());
        if (policy.getScheduleCron() != null) existing.setScheduleCron(policy.getScheduleCron());
        if (policy.getSeverityThreshold() != null) existing.setSeverityThreshold(policy.getSeverityThreshold());
        if (policy.getAutoBlock() != null) existing.setAutoBlock(policy.getAutoBlock());
        existing.setModifiedBy(policy.getModifiedBy());
        existing.setModifiedDate(LocalDateTime.now());
        scanPolicyRepo.save(existing);
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(String id) {
    scanPolicyRepo.deleteById(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateEnabled(String id, Boolean enabled, Long modifiedBy) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        if (!scanPolicyRepo.existsById(id)) {
          throw new RuntimeException("扫描策略不存在: " + id);
        }
      }

      @Override
      protected Void process() {
        scanPolicyRepo.updateEnabled(null, id, enabled, LocalDateTime.now(), modifiedBy);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<ScanPolicy, String> getRepository() {
    return this.scanPolicyRepo;
  }
}
