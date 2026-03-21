package cloud.xcan.angus.api.manager.impl;

import cloud.xcan.angus.api.commonlink.quota.Quota;
import cloud.xcan.angus.api.commonlink.quota.QuotaRepo;
import cloud.xcan.angus.api.manager.QuotaManager;
import cloud.xcan.angus.core.utils.PrincipalContextUtils;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class QuotaManagerImpl implements QuotaManager {

  @Resource
  private QuotaRepo quotaRepo;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Quota findTenantQuota(Long mainTenantId, String quotaCode) {
    return findOrCreateQuotaFromTemplate(mainTenantId, quotaCode);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void checkTenantQuota(Long mainTenantId, String quotaCode, Long currentUsedCount) {
    // 查询或创建配额
    Quota quota = findOrCreateQuotaFromTemplate(mainTenantId, quotaCode);

    // 检查配额是否启用
    if (Boolean.FALSE.equals(quota.getEnabled())) {
      log.warn("配额「{}」已禁用，跳过检查", quotaCode);
      return;
    }

    // 检查配额限制（limit为0表示无限制）
    if (quota.getLimit() > 0 && currentUsedCount > quota.getLimit()) {
      throw ProtocolException.of("资源配额「{0}」使用量「{1}」超过限制「{2}」",
          new Object[]{quota.getName(), currentUsedCount, quota.getLimit()});
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void checkTenantQuotaByTotalUsage(Long mainTenantId, String quotaCode) {
    // 查询或创建配额
    Quota quota = findOrCreateQuotaFromTemplate(mainTenantId, quotaCode);

    // 检查配额是否启用
    if (Boolean.FALSE.equals(quota.getEnabled())) {
      log.warn("配额「{}」已禁用，跳过检查", quotaCode);
      return;
    }

    // 获取配额中的总使用量
    long totalUsed = quota.getUsed() != null ? quota.getUsed() : 0L;

    // 检查配额限制（limit为0表示无限制）
    if (quota.getLimit() > 0 && totalUsed > quota.getLimit()) {
      throw ProtocolException.of("资源配额「{0}」总使用量「{1}」超过限制「{2}」",
          new Object[]{quota.getName(), totalUsed, quota.getLimit()});
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void checkTenantQuotaByTotalUsage(Long mainTenantId, String quotaCode, Long totalUsage) {
    if (totalUsage == null || totalUsage < 0) {
      log.warn("总使用量无效，跳过检查：tenantId={}, quotaCode={}, totalUsage={}", mainTenantId,
          quotaCode,
          totalUsage);
      return;
    }

    // 查询或创建配额
    Quota quota = findOrCreateQuotaFromTemplate(mainTenantId, quotaCode);

    // 检查配额是否启用
    if (Boolean.FALSE.equals(quota.getEnabled())) {
      log.warn("配额「{}」已禁用，跳过检查", quotaCode);
      return;
    }

    // 检查配额限制（limit为0表示无限制）
    if (quota.getLimit() > 0 && totalUsage > quota.getLimit()) {
      throw ProtocolException.of("资源配额「{0}」总使用量「{1}」超过限制「{2}」",
          new Object[]{quota.getName(), totalUsage, quota.getLimit()});
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void increaseTenantQuota(Long mainTenantId, String quotaCode, Long amount) {
    if (amount == null || amount <= 0) {
      log.warn("配额增加量无效，跳过操作：tenantId={}, quotaCode={}, amount={}", mainTenantId,
          quotaCode, amount);
      return;
    }

    // 查询或创建配额（包含租户校验）
    Quota quota = findOrCreateQuotaFromTemplate(mainTenantId, quotaCode);

    // 检查配额限制（limit为0表示无限制）
    Long oldUsed = quota.getUsed();
    Long newUsed = oldUsed + amount;
    if (Boolean.TRUE.equals(quota.getEnabled())
        && quota.getLimit() > 0 && newUsed > quota.getLimit()) {
      throw ProtocolException.of("资源配额「{0}」使用量「{1}」超过限制「{2}」",
          new Object[]{quota.getName(), newUsed, quota.getLimit()});
    }

    // 增加使用量
    quota.setUsed(newUsed);
    quotaRepo.save(quota);

    log.debug("配额使用量已增加：quotaCode={}, amount={}, oldUsed={}, newUsed={}",
        quotaCode, amount, oldUsed, newUsed);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void decreaseTenantQuota(Long mainTenantId, String quotaCode, Long amount) {
    if (amount == null || amount <= 0) {
      log.warn("配额减少量无效，跳过操作：tenantId={}, quotaCode={}, amount={}", mainTenantId,
          quotaCode, amount);
      return;
    }

    // 查询或创建配额
    Quota quota = findOrCreateQuotaFromTemplate(mainTenantId, quotaCode);

    // 减少使用量，但不能小于0
    Long oldUsed = quota.getUsed();
    Long newUsed = Math.max(0L, oldUsed - amount);
    quota.setUsed(newUsed);
    quotaRepo.save(quota);

    log.debug("配额使用量已减少：quotaCode={}, amount={}, oldUsed={}, newUsed={}",
        quotaCode, amount, oldUsed, newUsed);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void resetTenantQuota(Long mainTenantId, String quotaCode) {
    // 查询或创建配额
    Quota quota = findOrCreateQuotaFromTemplate(mainTenantId, quotaCode);

    // 重置使用量
    quota.setUsed(0L);
    quotaRepo.save(quota);
  }

  /**
   * 查找或根据模板创建配额
   * <p>
   * 如果当前租户的配额不存在，会查找模板配额（租户ID为-1的全局模板），然后根据模板创建新的配额。
   * </p>
   *
   * @param mainTenantId 租户ID
   * @param quotaCode    配额编码
   * @return 配额对象
   */
  private Quota findOrCreateQuotaFromTemplate(Long mainTenantId, String quotaCode) {
    // 1. 先查询当前租户的配额
    Optional<Quota> quotaOpt = quotaRepo.findByTenantIdAndCode(mainTenantId, quotaCode);
    if (quotaOpt.isPresent()) {
      return quotaOpt.get();
    }

    // 2. 如果不存在，查找模板配额
    boolean multiTenantCtrl = PrincipalContextUtils.isMultiTenantCtrl();
    try {
      if (multiTenantCtrl) {
        PrincipalContextUtils.setMultiTenantCtrl(false);
      }

      Quota templateQuota = findTemplateQuota(quotaCode);
      if (templateQuota == null) {
        throw ResourceNotFound.of("资源配额编码「{0}」不存在，且未找到对应的模板配额",
            new Object[]{quotaCode});
      }

      // 3. 根据模板创建新的配额
      Quota newQuota = createQuotaFromTemplate(mainTenantId, templateQuota);
      log.info("根据模板配额创建新配额：tenantId={}, quotaCode={}, templateTenantId={}",
          mainTenantId, quotaCode, templateQuota.getTenantId());

      return newQuota;
    } finally {
      if (multiTenantCtrl) {
        PrincipalContextUtils.setMultiTenantCtrl(true);
      }
    }
  }

  /**
   * 查找模板配额
   * <p>
   * 模板配额的isInitTemplate=true，表示全局模板配额。
   * </p>
   *
   * @param quotaCode 配额编码
   * @return 模板配额，如果不存在返回null
   */
  private Quota findTemplateQuota(String quotaCode) {
    Optional<Quota> templateQuota = quotaRepo.findByTenantIdAndCodeAndIsInitTemplate(
        quotaCode, true);
    return templateQuota.orElse(null);
  }

  /**
   * 根据模板创建配额
   *
   * @param mainTenantId 目标租户ID
   * @param template     模板配额
   * @return 新创建的配额
   */
  private Quota createQuotaFromTemplate(Long mainTenantId, Quota template) {
    Quota newQuota = new Quota();
    newQuota.setId(System.currentTimeMillis());
    newQuota.setTenantId(mainTenantId);
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

}
