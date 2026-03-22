package cloud.xcan.angus.core.repo.application.cmd.access.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.repo.application.cmd.access.AccessRuleCmd;
import cloud.xcan.angus.core.repo.domain.access.AccessRule;
import cloud.xcan.angus.core.repo.domain.access.AccessRuleRepo;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class AccessRuleCmdImpl extends CommCmd<AccessRule, Long> implements AccessRuleCmd {

  @Resource
  private AccessRuleRepo accessRuleRepo;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public AccessRule create(AccessRule accessRule) {
    return new BizTemplate<AccessRule>() {
      @Override
      protected void checkParams() {
        // Validate required fields
      }

      @Override
      protected AccessRule process() {
        accessRule.setCreatedDate(LocalDateTime.now());
        accessRule.setModifiedDate(LocalDateTime.now());
        if (accessRule.getEnabled() == null) {
          accessRule.setEnabled(true);
        }
        if (accessRule.getPriority() == null) {
          accessRule.setPriority(0);
        }
        insert0(accessRule);
        return accessRule;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public AccessRule update(AccessRule accessRule) {
    return new BizTemplate<AccessRule>() {
      AccessRule existing;

      @Override
      protected void checkParams() {
        existing = accessRuleRepo.findById(accessRule.getId())
            .orElseThrow(() -> new RuntimeException("访问规则不存在: " + accessRule.getId()));
      }

      @Override
      protected AccessRule process() {
        if (accessRule.getName() != null) {
          existing.setName(accessRule.getName());
        }
        if (accessRule.getDescription() != null) {
          existing.setDescription(accessRule.getDescription());
        }
        if (accessRule.getPrincipalType() != null) {
          existing.setPrincipalType(accessRule.getPrincipalType());
        }
        if (accessRule.getPrincipalId() != null) {
          existing.setPrincipalId(accessRule.getPrincipalId());
        }
        if (accessRule.getEnabled() != null) {
          existing.setEnabled(accessRule.getEnabled());
        }
        if (accessRule.getExpiresAt() != null) {
          existing.setExpiresAt(accessRule.getExpiresAt());
        }
        if (accessRule.getPriority() != null) {
          existing.setPriority(accessRule.getPriority());
        }
        if (accessRule.getPermissions() != null) {
          existing.setPermissions(accessRule.getPermissions());
        }
        if (accessRule.getPaths() != null) {
          existing.setPaths(accessRule.getPaths());
        }
        existing.setModifiedBy(accessRule.getModifiedBy());
        existing.setModifiedDate(LocalDateTime.now());
        accessRuleRepo.save(existing);
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
        accessRuleRepo.deleteById(id);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<AccessRule, Long> getRepository() {
    return this.accessRuleRepo;
  }
}
