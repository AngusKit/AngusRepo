package cloud.xcan.angus.core.gm.interfaces.user.facade;

import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.BatchUpdateNotificationDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UpdateAppearanceDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UpdateNotificationDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.AppearancePreferencesVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.LanguageVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.NotificationPreferencesVo;
import java.util.List;

/**
 * 用户设置门面接口
 */
public interface UserSettingFacade {

  /**
   * 更新外观偏好设置
   */
  AppearancePreferencesVo updateAppearance(UpdateAppearanceDto dto);

  /**
   * 获取外观偏好设置详情
   */
  AppearancePreferencesVo getAppearance();

  /**
   * 获取支持的语言列表
   */
  List<LanguageVo> getSupportedLanguages();

  /**
   * 更新通知偏好设置
   */
  NotificationPreferencesVo updateNotification(UpdateNotificationDto dto);

  /**
   * 获取通知偏好设置详情
   */
  NotificationPreferencesVo getNotification();

  /**
   * 批量更新通知类型设置
   */
  NotificationPreferencesVo batchUpdateNotification(BatchUpdateNotificationDto dto);

}
