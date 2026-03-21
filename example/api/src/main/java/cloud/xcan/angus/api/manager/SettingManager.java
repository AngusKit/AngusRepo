package cloud.xcan.angus.api.manager;

import cloud.xcan.angus.api.commonlink.setting.Setting;
import cloud.xcan.angus.api.commonlink.setting.SettingKey;

public interface SettingManager {

  Setting getSetting0(SettingKey key);

  Setting getCachedSetting(SettingKey key);

  Setting getCachedSetting0(SettingKey key);
}
