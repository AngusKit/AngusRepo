package cloud.xcan.angus.core.gm.application.cmd.quota.impl;

import cloud.xcan.angus.api.commonlink.quota.Quota;
import cloud.xcan.angus.api.commonlink.quota.QuotaRepo;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.quota.QuotaCmd;
import cloud.xcan.angus.core.gm.application.query.quota.QuotaQuery;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.utils.PrincipalContextUtils;
import cloud.xcan.angus.remote.message.ProtocolException;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 资源配额命令服务实现
 */
@Slf4j
@Service
public class QuotaCmdImpl extends CommCmd<Quota, Long> implements QuotaCmd {

  @Resource
  private QuotaRepo quotaRepo;

  @Resource
  private QuotaQuery quotaQuery;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Quota update(Quota quota) {
    return new BizTemplate<Quota>() {
      Quota existing;

      @Override
      protected void checkParams() {
        existing = quotaQuery.findByCodeAndCheck(quota.getCode());
        // 检查是否为许可控制，如果是许可控制则不允许修改
        if (Boolean.TRUE.equals(existing.getIsLicenseControl())) {
          throw ProtocolException.of("配额「{0}」由许可控制，不允许修改",
              new Object[]{existing.getName()});
        }
        // 检查配额限额不能小于已使用量
        if (quota.getLimit() != null && quota.getLimit() < existing.getUsed()) {
          throw ProtocolException.of("配额限额不能小于当前已使用量「{0}」",
              new Object[]{existing.getUsed()});
        }
      }

      @Override
      protected Quota process() {
        // 更新字段
        existing.setName(quota.getName());
        existing.setAppCode(quota.getAppCode());
        existing.setLimit(quota.getLimit());
        existing.setUnit(quota.getUnit());
        existing.setDescription(quota.getDescription());
        existing.setIcon(quota.getIcon());
        Quota saved = quotaRepo.save(existing);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.QUOTA,
            saved.getId(),
            saved.getName(),
            OperationMessage.QUOTA_UPDATE_DETAILS,
            new Object[]{
                saved.getName(),
                saved.getLimit(),
                saved.getUnit()
            }
        );

        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public List<Quota> batchUpdateLimits(List<String> codes, List<Long> limits) {
    return new BizTemplate<List<Quota>>() {
      @Override
      protected List<Quota> process() {
        List<Quota> results = new ArrayList<>();
        for (int i = 0; i < codes.size(); i++) {
          String code = codes.get(i);
          Long limit = limits.get(i);
          Quota quota = quotaQuery.findByCodeAndCheck(code);
          // 检查是否为许可控制，如果是许可控制则不允许修改
          if (Boolean.TRUE.equals(quota.getIsLicenseControl())) {
            throw ProtocolException.of("配额「{0}」由许可控制，不允许修改",
                new Object[]{quota.getName()});
          }
          // 检查配额限额不能小于已使用量
          if (limit < quota.getUsed()) {
            throw ProtocolException.of("配额「{0}」的限额不能小于当前已使用量「{1}」",
                new Object[]{quota.getName(), quota.getUsed()});
          }
          quota.setLimit(limit);
          results.add(quotaRepo.save(quota));
        }

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.QUOTA,
            null,
            "配额限额",
            OperationMessage.QUOTA_BATCH_UPDATE_LIMITS_DETAILS,
            new Object[]{results.size()}
        );

        return results;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Quota updateStatus(String code, Boolean enabled) {
    return new BizTemplate<Quota>() {
      @Override
      protected void checkParams() {
        Quota quota = quotaQuery.findByCodeAndCheck(code);
        // 检查是否为许可控制，如果是许可控制则不允许修改状态
        if (Boolean.TRUE.equals(quota.getIsLicenseControl())) {
          throw ProtocolException.of("配额「{0}」由许可控制，不允许修改状态",
              new Object[]{quota.getName()});
        }
      }

      @Override
      protected Quota process() {
        Quota quota = quotaQuery.findByCodeAndCheck(code);
        quota.setEnabled(enabled);
        Quota saved = quotaRepo.save(quota);

        // 记录操作日志
        String enabledText = Boolean.TRUE.equals(enabled) ? "YES" : "NO";
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.QUOTA,
            saved.getId(),
            saved.getName(),
            OperationMessage.QUOTA_UPDATE_STATUS_DETAILS,
            new Object[]{
                saved.getName(),
                enabledText
            }
        );

        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public List<Quota> initTenantQuotasFromTemplates(Long tenantId) {
    return new BizTemplate<List<Quota>>() {
      @Override
      protected List<Quota> process() {
        List<Quota> createdQuotas = new ArrayList<>();

        // 1. 查询所有模板配额
        boolean multiTenantCtrl = PrincipalContextUtils.isMultiTenantCtrl();
        try {
          if (multiTenantCtrl) {
            PrincipalContextUtils.setMultiTenantCtrl(false);
          }

          List<Quota> templateQuotas = quotaRepo.findAllTemplateQuotas();
          if (templateQuotas.isEmpty()) {
            log.info("未找到模板配额，跳过初始化：tenantId={}", tenantId);
            return createdQuotas;
          }

          // 2. 遍历每个模板配额，检查租户是否已有对应配额
          for (Quota templateQuota : templateQuotas) {
            Optional<Quota> existingQuotaOpt = quotaRepo.findByTenantIdAndCode(
                tenantId, templateQuota.getCode());

            // 3. 如果不存在，根据模板创建新配额
            if (existingQuotaOpt.isEmpty()) {
              Quota newQuota = createQuotaFromTemplate(tenantId, templateQuota);
              createdQuotas.add(newQuota);
              log.info("根据模板配额创建新配额：tenantId={}, quotaCode={}, quotaName={}",
                  tenantId, templateQuota.getCode(), templateQuota.getName());
            } else {
              log.debug("租户配额已存在，跳过创建：tenantId={}, quotaCode={}",
                  tenantId, templateQuota.getCode());
            }
          }

          return createdQuotas;
        } finally {
          if (multiTenantCtrl) {
            PrincipalContextUtils.setMultiTenantCtrl(true);
          }
        }
      }
    }.execute();
  }

  /**
   * 根据模板创建配额
   * <p>
   * 参考 QuotaManagerImpl.createQuotaFromTemplate 的实现
   * </p>
   *
   * @param tenantId 目标租户ID
   * @param template 模板配额
   * @return 新创建的配额
   */
  private Quota createQuotaFromTemplate(Long tenantId, Quota template) {
    Quota newQuota = new Quota();
    newQuota.setId(uidGenerator.getUID());
    newQuota.setTenantId(tenantId);
    newQuota.setCode(template.getCode());
    newQuota.setName(template.getName());
    newQuota.setAppCode(template.getAppCode());
    newQuota.setLimit(template.getLimit());
    newQuota.setUsed(0L); // 新配额使用量初始化为0
    newQuota.setUnit(template.getUnit());
    newQuota.setDescription(template.getDescription());
    newQuota.setIcon(template.getIcon());
    newQuota.setEnabled(template.getEnabled());
    newQuota.setIsLicenseControl(template.getIsLicenseControl());
    newQuota.setIsInitTemplate(false); // 创建的配额不是模板
    return quotaRepo.save(newQuota);
  }

  @Override
  protected BaseRepository<Quota, Long> getRepository() {
    return quotaRepo;
  }
}
