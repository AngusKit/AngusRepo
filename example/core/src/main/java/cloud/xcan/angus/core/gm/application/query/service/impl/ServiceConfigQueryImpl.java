package cloud.xcan.angus.core.gm.application.query.service.impl;

import static cloud.xcan.angus.api.commonlink.setting.Setting.getDefaultEurekaConfig;

import cloud.xcan.angus.api.commonlink.setting.Setting;
import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.eureka.EurekaConfig;
import cloud.xcan.angus.api.manager.SettingManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.service.ServiceConfigQuery;
import cloud.xcan.angus.spec.utils.StringUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ServiceConfigQueryImpl implements ServiceConfigQuery {

  @Resource
  private SettingManager settingManager;

  @Override
  public EurekaConfig getEurekaConfig() {
    return new BizTemplate<EurekaConfig>() {
      @Override
      protected EurekaConfig process() {
        Setting setting = settingManager.getSetting0(SettingKey.EUREKA_CONFIG);
        return setting != null && setting.getEurekaConfig() != null
            ? setting.getEurekaConfig() : getDefaultEurekaConfig();
      }
    }.execute();
  }

  public static String getApplicationUpperCaseCodeByServiceName(String serviceName) {
    String upperServiceName = serviceName.toUpperCase();
    String applicationCode = StringUtils.remove(upperServiceName, "XCAN-");
    return StringUtils.remove(applicationCode, ".BOOT");
  }
}
