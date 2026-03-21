package cloud.xcan.angus.api.manager;

import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.core.jpa.multitenancy.TenantAccountQuery;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

public interface TenantManager extends TenantAccountQuery {

  Page<Tenant> findAll(@Nullable Specification<Tenant> spec, Pageable pageable);

  Tenant findAndCheck(Long tenantId);

  Tenant findAndCheckOwnerTenant();

  List<Tenant> findAndCheck(Collection<Long> tenantIds);

  Language getCachedDefaultLanguage(Long tenantId);

  Locale resolveLocale();

  Locale resolveLocale(Long targetUserId);

  Language resolveLanguage();

  List<Tenant> getSameAccountTenants();

  List<Long> getTenantIdsBySameAccount();

  Tenant getMainTenantOfSameAccount(Long tenantId);

  List<Tenant> getSameAccountTenants(Long tenantId);

  void checkMultitenancyPermission();

  void checkValidStatus(String tenantId);
}
