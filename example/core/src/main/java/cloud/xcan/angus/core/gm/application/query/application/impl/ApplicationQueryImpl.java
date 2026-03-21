package cloud.xcan.angus.core.gm.application.query.application.impl;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isCloudServiceEdition;
import static cloud.xcan.angus.spec.experimental.BizConstant.OWNER_TENANT_ID;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.application.Application;
import cloud.xcan.angus.api.commonlink.application.ApplicationRepo;
import cloud.xcan.angus.api.commonlink.application.enums.ApplicationSource;
import cloud.xcan.angus.api.commonlink.application.enums.ApplicationType;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationQuery;
import cloud.xcan.angus.core.gm.domain.application.ApplicationSearchRepo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.remote.search.SearchCriteria;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ApplicationQueryImpl implements ApplicationQuery {

  @Resource
  private ApplicationRepo applicationRepo;

  @Resource
  private ApplicationSearchRepo applicationSearchRepo;

  @Override
  public Application findAndCheck(Long id) {
    return new BizTemplate<Application>(false) {
      @Override
      protected Application process() {
        return applicationRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("应用「{0}」不存在", new Object[]{id}));
      }
    }.execute();
  }

  @Override
  public Page<Application> find(GenericSpecification<Application> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Application>>(false) {
      @Override
      protected Page<Application> process() {
        return fullTextSearch
            ? applicationSearchRepo.find(spec.getCriteria(), pageable, Application.class, match)
            : applicationRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public List<Application> findAll() {
    return new BizTemplate<List<Application>>(false) {
      @Override
      protected List<Application> process() {
        return applicationRepo.findAll();
      }
    }.execute();
  }

  @Override
  public ApplicationStatsVo getStats() {
    return new BizTemplate<ApplicationStatsVo>(false) {
      @Override
      protected ApplicationStatsVo process() {
        // 构建状态查询条件
        GenericSpecification<Application> enabledSpec = new GenericSpecification<>();
        enabledSpec.getCriteria().add(SearchCriteria.equal("status", EnabledStatus.ENABLED));

        GenericSpecification<Application> disabledSpec = new GenericSpecification<>();
        disabledSpec.getCriteria().add(SearchCriteria.equal("status", EnabledStatus.DISABLED));

        // 构建类型查询条件
        GenericSpecification<Application> baseSpec = new GenericSpecification<>();
        baseSpec.getCriteria().add(SearchCriteria.equal("type", ApplicationType.BASE));

        GenericSpecification<Application> businessSpec = new GenericSpecification<>();
        businessSpec.getCriteria().add(SearchCriteria.equal("type", ApplicationType.BUSINESS));

        // 构建来源查询条件
        GenericSpecification<Application> customSpec = new GenericSpecification<>();
        customSpec.getCriteria().add(SearchCriteria.equal("source", ApplicationSource.CUSTOM));

        ApplicationStatsVo stats = new ApplicationStatsVo();
        stats.setTotalApplications(applicationRepo.count());
        stats.setEnabledApplications(applicationRepo.count(enabledSpec));
        stats.setDisabledApplications(applicationRepo.count(disabledSpec));
        stats.setBaseApplications(applicationRepo.count(baseSpec));
        stats.setBusinessApplications(applicationRepo.count(businessSpec));
        stats.setCustomApplications(applicationRepo.count(customSpec));
        return stats;
      }
    }.execute();
  }

  @Override
  public Optional<Application> findByCodeAndEditionType(String code, String editionType) {
    return applicationRepo.findByCodeAndEditionType(code, EditionType.valueOf(editionType))
        .stream()
        .filter(x -> x.getStatus().isEnabled())
        .max(Comparator.comparing(Application::getId));
  }

  @Override
  public Optional<Application> findByCode(String code) {
    return applicationRepo.findByCode(code)
        .stream()
        .filter(x -> x.getStatus().isEnabled())
        .max(Comparator.comparing(Application::getId));
  }

  @Override
  public Application checkCanModify(Long id) {
    Application application = findAndCheck(id);
    return checkCanModify(application);
  }

  @Override
  public Application checkCanModify(Application application) {
    // 允许租户1维护应用信息
    if (OWNER_TENANT_ID.equals(getOptTenantId())) {
      return application;
    }
    // 禁止修改云服务版本应用
    if (isCloudServiceEdition()) {
      throw ProtocolException.of("禁止修改云服务版本应用「{0}」",
          new Object[]{application.getCode()});
    }
    // 禁止修改来源是安装类型的应用
    if (ApplicationSource.INSTALLED.equals(application.getSource())) {
      throw ProtocolException.of("安装应用「{0}」不允许修改或删除",
          new Object[]{application.getCode()});
    }
    return application;
  }

  @Override
  public List<Application> findAllById(Collection<Long> ids) {
    return applicationRepo.findAllById(ids);
  }

  @Override
  public List<Application> findAllByType(ApplicationSource source) {
    return applicationRepo.findBySource(source);
  }

  @Override
  public Application findById(Long id) {
    return applicationRepo.findById(id).orElse(null);
  }
}
