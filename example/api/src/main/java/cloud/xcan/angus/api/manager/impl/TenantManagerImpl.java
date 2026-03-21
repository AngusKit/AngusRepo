package cloud.xcan.angus.api.manager.impl;

import static cloud.xcan.angus.api.manager.ManagerMessage.TENANT_NOT_EXISTED_T;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getApplicationInfo;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.experimental.BizConstant.OWNER_TENANT_ID;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.tenant.TenantRepo;
import cloud.xcan.angus.api.manager.TenantManager;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.utils.PrincipalContextUtils;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.SysException;
import cloud.xcan.angus.remote.message.http.Forbidden;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import cloud.xcan.angus.spec.utils.CaffeineCacheUtils;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public class TenantManagerImpl implements TenantManager {

  @Resource
  @Qualifier("commonTenantRepo")
  private TenantRepo tenantRepo;

  @Resource
  private UserManager userManager;

  public static final Cache<Long, Language> TENANT_LANGUAGE_CACHE
      = CaffeineCacheUtils.createCache("TENANT_LANGUAGE_CACHE", 2048, 2, 5);

  @Override
  public Page<Tenant> findAll(@Nullable Specification<Tenant> spec, Pageable pageable) {
    return tenantRepo.findAll(spec, pageable);
  }

  @Override
  public Tenant findAndCheck(Long tenantId) {
    return tenantRepo.findById(tenantId)
        .orElseThrow(() -> ResourceNotFound.of(tenantId, "Tenant"));
  }

  @Override
  public Tenant findAndCheckOwnerTenant() {
    // When Cloud Service edition
    if (getApplicationInfo().isCloudServiceEdition()) {
      return findAndCheck(OWNER_TENANT_ID);
    }
    // When privation edition
    return tenantRepo.findFirst().orElseThrow(() -> SysException.of("Tenant not found"));
  }

  @Override
  public List<Tenant> findAndCheck(Collection<Long> tenantIds) {
    if (isEmpty(tenantIds)) {
      return null;
    }
    List<Tenant> tenants = tenantRepo.findAllByIdIn(tenantIds);
    assertResourceNotFound(tenants, TENANT_NOT_EXISTED_T,
        new Object[]{tenantIds.iterator().next()});
    if (tenantIds.size() != tenants.size()) {
      tenantIds.removeAll(tenants.stream().map(Tenant::getId).collect(Collectors.toSet()));
      assertResourceNotFound(tenantIds.isEmpty(), TENANT_NOT_EXISTED_T,
          new Object[]{tenantIds.iterator().next()});
    }
    return tenants;
  }

  @Override
  public Language getCachedDefaultLanguage(Long tenantId) {
    Language language = TENANT_LANGUAGE_CACHE.getIfPresent(tenantId);
    if (language != null) {
      return language;
    }

    Tenant tenant = tenantRepo.findById(tenantId).orElse(null);
    language = Language.DEFAULT;
    if (tenant != null && tenant.getDefaultLanguage() != null) {
      language = tenant.getDefaultLanguage();
    }

    TENANT_LANGUAGE_CACHE.put(tenantId, language);
    return language;
  }

  /**
   * 解析当前用户所属租户的 Locale，用于国际化消息
   */
  @Override
  public Locale resolveLocale() {
    Long tenantId = getOptTenantId();
    if (tenantId != null) {
      return getCachedDefaultLanguage(tenantId).toLocale();
    }
    return Language.DEFAULT.toLocale();
  }

  /**
   * 解析通知用户所属租户的 Locale，用于国际化消息
   */
  @Override
  public Locale resolveLocale(Long targetUserId) {
    Long tenantId = targetUserId != null && targetUserId > 0
        ? userManager.getCachedTenantId(getOptTenantId(), targetUserId)
        : getOptTenantId();
    if (tenantId != null) {
      return getCachedDefaultLanguage(tenantId).toLocale();
    }
    return Language.DEFAULT.toLocale();
  }

  /**
   * 解析当前用户所属租户的 Language，用于国际化消息
   */
  @Override
  public Language resolveLanguage() {
    Long tenantId = getOptTenantId();
    if (tenantId != null) {
      return getCachedDefaultLanguage(tenantId);
    }
    return Language.DEFAULT;
  }

  @Override
  public List<Tenant> getSameAccountTenants() {
    Long currentTenantId = getOptTenantId();
    return currentTenantId < 1 && PrincipalContext.getApiType().isPubTypeApi()
        ? Collections.emptyList() : getSameAccountTenants(currentTenantId);
  }

  @Override
  public List<Long> getTenantIdsBySameAccount() {
    Long tenantId = getOptTenantId();
    return getSameAccountTenants(tenantId).stream().map(Tenant::getId).toList();
  }

  @Override
  public Tenant getMainTenantOfSameAccount(Long tenantId) {
    Tenant tenant = findAndCheck(tenantId);
    if (tenant.getAccountType().isMainAccount()) {
      return tenant;
    }
    return tenantRepo.findById(tenant.getMainTenantId()).orElseThrow(
        () -> ResourceNotFound.of("租户「{0}」主账户没有没有找到", new Object[]{tenantId}));
  }

  @Override
  public List<Tenant> getSameAccountTenants(Long tenantId) {
    boolean multiTenantCtrl = PrincipalContextUtils.isMultiTenantCtrl();
    if (multiTenantCtrl) {
      PrincipalContextUtils.setMultiTenantCtrl(false);
    }
    List<Tenant> tenants = new ArrayList<>();
    Tenant tenant = tenantRepo.findById(tenantId)
        .orElseThrow(() -> ResourceNotFound.of("租户「{0}」不存在", new Object[]{tenantId}));
    tenants.add(tenant);
    if (tenant.getAccountType().isMainAccount()) {
      List<Tenant> subTenants = tenantRepo.findByMainTenantId(tenant.getId());
      tenants.addAll(subTenants);
    } else {
      List<Tenant> otherTenants = tenantRepo.findByMainTenantId(tenant.getMainTenantId());
      tenants.addAll(otherTenants.stream().filter(t -> !t.getId().equals(tenant.getId())).toList());
    }
    if (multiTenantCtrl) {
      PrincipalContextUtils.setMultiTenantCtrl(true);
    }
    return tenants;
  }

  @Override
  public List<Long> getTenantIdsBySameAccount(Long tenantId) {
    return getSameAccountTenants(tenantId).stream().map(Tenant::getId).toList();
  }

  @Override
  public Long getMainTenantId(Long currentTenantId) {
    return getMainTenantOfSameAccount(currentTenantId).getId();
  }

  @Override
  public boolean isMainTenant(Long tenantId) {
    return Objects.equals(getMainTenantId(tenantId), tenantId);
  }

  @Override
  public void checkMultitenancyPermission() {
    Long currentTenantId = getOptTenantId();
    Long optTenantId = getOptTenantId();
    if (optTenantId == null || Objects.equals(currentTenantId, optTenantId)) {
      return;
    }
    if (!getTenantIdsBySameAccount(currentTenantId).contains(optTenantId)) {
      throw Forbidden.of("禁止操作非同账号租户「{0}」，当前租户：「{1}」",
          new Object[]{optTenantId, currentTenantId});
    }
  }

  @Override
  public void checkValidStatus(String tenantId) {
    // 将String tenantId转换为Long
    long tenantIdLong;
    try {
      tenantIdLong = Long.parseLong(tenantId);
    } catch (NumberFormatException e) {
      throw ResourceNotFound.of("租户「{0}」不存在", new Object[]{tenantId});
    }

    // 查询租户
    Tenant tenant = tenantRepo.findById(tenantIdLong)
        .orElseThrow(() -> ResourceNotFound.of("租户「{0}」不存在", new Object[]{tenantId}));

    // 检查租户状态是否为启用
    if (tenant.getStatus() != EnabledStatus.ENABLED) {
      throw ProtocolException.of("租户「{0}」已被禁用", new Object[]{tenant.getName()});
    }

    // 检查租户是否过期
    if (tenant.getExpireDate() != null && tenant.getExpireDate().isBefore(LocalDateTime.now())) {
      throw ProtocolException.of("租户「{0}」已过期", new Object[]{tenant.getName()});
    }
  }
}
