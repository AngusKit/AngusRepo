package cloud.xcan.angus.core.gm.application.query.quota.impl;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.quota.Quota;
import cloud.xcan.angus.api.commonlink.quota.QuotaRepo;
import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.quota.QuotaQuery;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.QuotaStatisticsVo;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.QuotaUsageVo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class QuotaQueryImpl implements QuotaQuery {

  @Resource
  private QuotaRepo quotaRepo;

  @Resource
  private TenantQuery tenantQuery;

  @Override
  public Quota findByCodeAndCheck(String code) {
    return new BizTemplate<Quota>() {
      @Override
      protected Quota process() {
        Tenant main = tenantQuery.getMainTenantOfSameAccount(getOptTenantId());
        return quotaRepo.findByTenantIdAndCode(main.getId(), code)
            .orElseThrow(() -> ResourceNotFound.of("资源配额编码「{0}」不存在", new Object[]{code}));
      }
    }.execute();
  }

  @Override
  public List<Quota> list(String appCode, Boolean enabled) {
    return new BizTemplate<List<Quota>>() {
      @Override
      protected List<Quota> process() {
        Tenant main = tenantQuery.getMainTenantOfSameAccount(getOptTenantId());
        return quotaRepo.findByTenantId(main.getId()).stream()
            .filter(q -> !isNotEmpty(appCode) || Objects.equals(q.getAppCode(), appCode))
            .filter(q -> enabled == null || Objects.equals(q.getEnabled(), enabled))
            .collect(Collectors.toList());
      }
    }.execute();
  }

  @Override
  public QuotaStatisticsVo getStatistics() {
    return new BizTemplate<QuotaStatisticsVo>() {
      @Override
      protected QuotaStatisticsVo process() {
        Tenant main = tenantQuery.getMainTenantOfSameAccount(getOptTenantId());
        List<Quota> quotas = quotaRepo.findByTenantId(main.getId());
        QuotaStatisticsVo vo = new QuotaStatisticsVo();
        vo.setTotalResources(quotas.size());
        vo.setAppliedQuotas((int) quotas.stream()
            .filter(q -> Boolean.TRUE.equals(q.getEnabled()) && q.getLimit() != null
                && q.getLimit() > 0)
            .count());
        vo.setInsufficientQuotas((int) quotas.stream()
            .filter(q -> q.getLimit() > 0 && (double) q.getUsed() / q.getLimit() >= 0.9).count());
        vo.setEnabledQuotas((int) quotas.stream().filter(Quota::getEnabled).count());
        vo.setDisabledQuotas((int) quotas.stream().filter(q -> !q.getEnabled()).count());
        // 按应用统计
        Map<String, List<Quota>> appMap = quotas.stream()
            .collect(Collectors.groupingBy(Quota::getAppCode));
        List<QuotaStatisticsVo.AppStatistic> appStats = new ArrayList<>();
        appMap.forEach((appId, quotaList) -> {
          QuotaStatisticsVo.AppStatistic stat = new QuotaStatisticsVo.AppStatistic();
          stat.setAppCode(quotaList.get(0).getAppCode());
          stat.setQuotaCount(quotaList.size());
          stat.setInsufficientCount((int) quotaList.stream()
              .filter(q -> q.getLimit() > 0 && (double) q.getUsed() / q.getLimit() >= 0.9).count());
          appStats.add(stat);
        });
        vo.setAppStatistics(appStats);
        return vo;
      }
    }.execute();
  }

  @Override
  public QuotaUsageVo getUsage(String code) {
    return new BizTemplate<QuotaUsageVo>() {
      @Override
      protected QuotaUsageVo process() {
        Quota quota = findByCodeAndCheck(code);
        QuotaUsageVo vo = new QuotaUsageVo();
        vo.setId(quota.getId().toString());
        vo.setCode(quota.getCode());
        vo.setName(quota.getName());
        vo.setLimit(quota.getLimit());
        vo.setUsed(quota.getUsed());
        vo.setAvailable(quota.getLimit() - quota.getUsed());
        vo.setUnit(quota.getUnit());
        if (quota.getLimit() > 0) {
          double percentage = (double) quota.getUsed() / quota.getLimit() * 100;
          vo.setUsagePercentage(percentage);
          if (percentage >= 90) {
            vo.setStatus("CRITICAL");
          } else if (percentage >= 75) {
            vo.setStatus("WARNING");
          } else {
            vo.setStatus("NORMAL");
          }
        } else {
          vo.setUsagePercentage(0.0);
          vo.setStatus("NORMAL");
        }
        vo.setLastRefreshTime(quota.getModifiedDate());
        return vo;
      }
    }.execute();
  }

  @Override
  public List<Quota> findByMainTenantId(Long id) {
    return quotaRepo.findByTenantId(id);
  }

}
