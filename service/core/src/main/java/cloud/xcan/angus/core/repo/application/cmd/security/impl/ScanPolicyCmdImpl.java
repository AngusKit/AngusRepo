package cloud.xcan.angus.core.repo.application.cmd.security.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.repo.application.cmd.security.ScanPolicyCmd;
import cloud.xcan.angus.core.repo.application.query.security.ScanPolicyQuery;
import cloud.xcan.angus.core.repo.domain.security.ScanPolicy;
import cloud.xcan.angus.core.repo.domain.security.ScanPolicyRepo;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Biz
public class ScanPolicyCmdImpl extends CommCmd<ScanPolicy, String> implements ScanPolicyCmd {

  @Resource
  private ScanPolicyRepo scanPolicyRepo;

  @Resource
  private ScanPolicyQuery scanPolicyQuery;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ScanPolicy create(ScanPolicy policy) {
    return new BizTemplate<ScanPolicy>() {
      @Override
      protected void checkParams() {
        if (scanPolicyRepo.findByTenantIdAndNameAndRepositoryId(
            policy.getTenantId(), policy.getName(), policy.getRepositoryId()).isPresent()) {
          throw ProtocolException.of("扫描策略已存在：{0}", new Object[]{policy.getName()});
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
        log.info("Scan policy created: name={}, id={}", policy.getName(), policy.getId());
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
        existing = scanPolicyQuery.findAndCheck(policy.getId());
        if (policy.getName() != null && !policy.getName().equals(existing.getName())) {
          if (scanPolicyRepo.existsByTenantIdAndNameAndRepositoryIdAndIdNot(
              existing.getTenantId(), policy.getName(), existing.getRepositoryId(), existing.getId())) {
            throw ProtocolException.of("扫描策略名称已存在：{0}", new Object[]{policy.getName()});
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
        log.info("Scan policy updated: id={}, name={}", policy.getId(), policy.getName());
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(String id) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        log.warn("Scan policy deleted: id={}", id);
        scanPolicyRepo.deleteById(id);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateEnabled(String id, Boolean enabled, Long modifiedBy) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        if (!scanPolicyRepo.existsById(id)) {
          throw ResourceNotFound.of(id, "ScanPolicy");
        }
      }

      @Override
      protected Void process() {
        scanPolicyRepo.updateEnabled(null, id, enabled, LocalDateTime.now(), modifiedBy);
        log.info("Scan policy enabled status updated: id={}, enabled={}", id, enabled);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<ScanPolicy, String> getRepository() {
    return this.scanPolicyRepo;
  }
}
