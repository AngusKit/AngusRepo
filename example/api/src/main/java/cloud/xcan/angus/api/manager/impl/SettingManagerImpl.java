package cloud.xcan.angus.api.manager.impl;

import cloud.xcan.angus.api.commonlink.setting.Setting;
import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.SettingRepo;
import cloud.xcan.angus.api.manager.SettingManager;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class SettingManagerImpl implements SettingManager {

  @Resource
  private SettingRepo settingRepo;

  @Resource
  private SettingManager settingManager;

  @Override
  public Setting getSetting0(SettingKey key) {
    return settingRepo.findByKey(key).orElse(null);
  }

  @Override
  public Setting getCachedSetting(SettingKey key) {
    return settingManager.getCachedSetting0(key);
  }

  @Override
  @Cacheable(key = "'key_' + #key", value = "setting")
  public Setting getCachedSetting0(SettingKey key) {
    return settingRepo.findByKey(key)
        .orElseThrow(() -> ResourceNotFound.of(key.getValue(), "Setting"));
  }
}
