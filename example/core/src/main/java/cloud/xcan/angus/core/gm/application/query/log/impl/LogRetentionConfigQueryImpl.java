package cloud.xcan.angus.core.gm.application.query.log.impl;

import cloud.xcan.angus.api.commonlink.setting.Setting;
import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.logretention.LogRetentionConfig;
import cloud.xcan.angus.api.manager.SettingManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.log.LogRetentionConfigQuery;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * 日志清理配置查询服务实现
 */
@Service
public class LogRetentionConfigQueryImpl implements LogRetentionConfigQuery {

  @Resource
  private SettingManager settingManager;

  @Override
  public List<LogRetentionConfig> findList(Long applicationId) {
    return new BizTemplate<List<LogRetentionConfig>>() {
      @Override
      protected List<LogRetentionConfig> process() {
        List<LogRetentionConfig> configs = findList();
        // 过滤
        return configs.stream()
            .filter(config -> applicationId == null
                || applicationId.equals(config.getApplicationId()))
            .collect(Collectors.toList());
      }
    }.execute();
  }

  @Override
  public List<LogRetentionConfig> findList() {
    return new BizTemplate<List<LogRetentionConfig>>() {
      @Override
      protected List<LogRetentionConfig> process() {
        List<LogRetentionConfig> configs = new ArrayList<>();

        Setting setting = settingManager.getSetting0(SettingKey.LOG_RETENTION_CONFIGS);
        if (setting != null) {
          configs = setting.getLogRetentionConfigs();
        }

        if (configs == null) {
          configs = new ArrayList<>();
        }
        return configs;
      }
    }.execute();
  }

}
