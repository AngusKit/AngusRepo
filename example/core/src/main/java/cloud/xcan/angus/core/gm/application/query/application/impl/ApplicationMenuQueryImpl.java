package cloud.xcan.angus.core.gm.application.query.application.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.application.ApplicationMenu;
import cloud.xcan.angus.api.commonlink.application.ApplicationMenuRepo;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationMenuQuery;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationQuery;
import cloud.xcan.angus.core.utils.PrincipalContextUtils;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ApplicationMenuQueryImpl implements ApplicationMenuQuery {

  @Resource
  private ApplicationMenuRepo applicationMenuRepo;

  @Resource
  private ApplicationQuery applicationQuery;

  @Override
  public ApplicationMenu findAndCheck(Long id) {
    boolean multiTenantCtrl = PrincipalContextUtils.isMultiTenantCtrl();
    try {
      if (multiTenantCtrl) {
        PrincipalContextUtils.setMultiTenantCtrl(false);
      }
      return applicationMenuRepo.findById(id)
          .orElseThrow(() -> ResourceNotFound.of("菜单「{0}」不存在", new Object[]{id}));
    } finally {
      if (multiTenantCtrl) {
        PrincipalContextUtils.setMultiTenantCtrl(true);
      }
    }
  }

  @Override
  public List<ApplicationMenu> findAndCheck(Long appId, Collection<Long> menuIds) {
    boolean multiTenantCtrl = PrincipalContextUtils.isMultiTenantCtrl();
    try {
      if (multiTenantCtrl) {
        PrincipalContextUtils.setMultiTenantCtrl(false);
      }
      List<ApplicationMenu> menus = applicationMenuRepo.findByApplicationIdAndIdIn(appId, menuIds);
      assertResourceNotFound(isNotEmpty(menus), menuIds.iterator().next(), "Menu");

      if (menuIds.size() != menus.size()) {
        for (ApplicationMenu group : menus) {
          assertResourceNotFound(menuIds.contains(group.getId()), group.getName(), "Menu");
        }
      }
      return menus;
    } finally {
      if (multiTenantCtrl) {
        PrincipalContextUtils.setMultiTenantCtrl(true);
      }
    }
  }

  @Override
  public List<ApplicationMenu> findByAppId(Long appId) {
    return new BizTemplate<List<ApplicationMenu>>(false) {
      @Override
      protected void checkParams() {
        // 验证应用是否存在
        applicationQuery.findAndCheck(appId);
      }

      @Override
      protected List<ApplicationMenu> process() {
        return applicationMenuRepo.findByApplicationId(appId);
      }
    }.execute();
  }

  @Override
  public Integer countByApplicationId(Long id) {
    return new BizTemplate<Integer>(false) {
      @Override
      protected Integer process() {
        return applicationMenuRepo.countByApplicationId(id);
      }
    }.execute();
  }

  @Override
  public boolean existsByAppIdAndCode(Long appId, String code) {
    return applicationMenuRepo.existsByApplicationIdAndCode(appId, code);
  }

  @Override
  public boolean existsByAppIdAndCodeAndIdNot(Long appId, String code, Long id) {
    return applicationMenuRepo.existsByApplicationIdAndCodeAndIdNot(appId, code, id);
  }

}

