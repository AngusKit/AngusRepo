package cloud.xcan.angus.core.gm.application.cmd.log.impl;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.utils.ObjectUtils.stringSafe;

import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.api.commonlink.user.UserBase;
import cloud.xcan.angus.api.enums.DeviceType;
import cloud.xcan.angus.api.manager.TenantManager;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.api.pojo.DeviceInfo;
import cloud.xcan.angus.api.pojo.LocationInfo;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.domain.log.UserOperationLog;
import cloud.xcan.angus.core.gm.domain.log.UserOperationLogRepo;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.gm.domain.log.enums.ResponseStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.principal.Principal;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import cloud.xcan.angus.spec.utils.CaffeineCacheUtils;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户操作日志命令服务实现
 * <p>
 * 提供便捷的业务写入操作日志方法，简化业务代码中的操作日志创建逻辑
 * </p>
 *
 * @author Angus
 */
@Slf4j
@Service
public class UserOperationLogCmdImpl extends CommCmd<UserOperationLog, Long> implements
    UserOperationLogCmd {

  @Resource
  private UserOperationLogRepo userOperationLogRepo;

  @Resource
  private MessageSource messageSource;

  @Resource
  private UserManager userManager;

  @Resource
  private TenantManager tenantManager;

  public static final Cache<Long, UserBase> USER_INFO_CACHE
      = CaffeineCacheUtils.createCache("USER_OPERATION_LOG_USER_INFO_CACHE");

  // ==================== 基础创建方法 ====================

  @Override
  public UserOperationLog log(OperationAction action, ResourceType resourceType, Long resourceId,
      String resource, String details, ResponseStatus responseStatus, String errorMessage) {
    try {
      Principal principal = PrincipalContext.get();
      Long userId = PrincipalContext.getUserId();
      if (userId == null || userId < 1) {
        log.warn("无法记录操作日志：用户ID为空");
        return null;
      }

      // 获取用户信息
      String userName = getUserName(userId);

      // 获取位置信息
      LocationInfo locationInfo = principal.getLocationInfo();
      String location = stringSafe(locationInfo.getCountry(), "unknown")
          + " " + stringSafe(locationInfo.getCity());

      // 获取设备信息
      DeviceInfo deviceInfo = principal.getDeviceInfo();
      String userAgent = deviceInfo.getUserAgent();
      DeviceType deviceType = deviceInfo.getDeviceType();
      String deviceId = deviceInfo.getDeviceId();

      // 创建操作日志
      UserOperationLog operationLog = new UserOperationLog();
      operationLog.setUserId(userId);
      operationLog.setUserName(userName);
      operationLog.setAction(action);
      operationLog.setResourceType(resourceType);
      operationLog.setResourceId(resourceId);
      operationLog.setResource(resource);
      operationLog.setIp(principal.getRemoteAddress());
      operationLog.setUserAgent(userAgent);
      operationLog.setLocation(location);
      operationLog.setDevice(deviceType);
      operationLog.setDeviceId(deviceId);
      operationLog.setDetails(details);
      operationLog.setResponseStatus(responseStatus);
      operationLog.setErrorMessage(errorMessage);
      operationLog.setTenantId(principal.getOptTenantId());
      operationLog.setCreatedDate(LocalDateTime.now());
      return insert(operationLog);
    } catch (Exception e) {
      log.error("记录操作日志失败", e);
      return null;
    }
  }

  @Override
  public UserOperationLog log(OperationAction action, ResourceType resourceType, Long resourceId,
      String resource, String details, ResponseStatus responseStatus) {
    return log(action, resourceType, resourceId, resource, details, responseStatus, null);
  }

  // ==================== 使用消息键创建操作日志 ====================

  @Override
  public UserOperationLog logByMessageKey(OperationAction action, ResourceType resourceType,
      Long resourceId, String resource, String detailsKey, Object[] detailsArgs,
      ResponseStatus responseStatus, String errorMessage) {
    // 使用 MessageSource 获取国际化消息，语言取自当前用户所属租户的配置
    String details = null;
    if (StringUtils.hasText(detailsKey)) {
      Locale locale = tenantManager.resolveLocale();
      Object[] safeArgs = detailsArgs != null ? detailsArgs : new Object[]{};
      details = messageSource.getMessage(detailsKey, safeArgs, detailsKey, locale);
    }
    return log(action, resourceType, resourceId, resource, details,
        responseStatus, errorMessage);
  }

  @Override
  public UserOperationLog logSuccessByMessageKey(OperationAction action, ResourceType resourceType,
      Long resourceId, String resource, String detailsKey, Object[] detailsArgs) {
    return logByMessageKey(action, resourceType, resourceId, resource, detailsKey, detailsArgs,
        ResponseStatus.SUCCESS, null);
  }

  @Override
  public UserOperationLog logFailureByMessageKey(OperationAction action, ResourceType resourceType,
      Long resourceId, String resource, String detailsKey, Object[] detailsArgs,
      String errorMessage) {
    return logByMessageKey(action, resourceType, resourceId, resource, detailsKey, detailsArgs,
        ResponseStatus.FAILURE, errorMessage);
  }

  // ==================== 私有辅助方法 ====================

  /**
   * 获取用户名称
   */
  private String getUserName(Long userId) {
    if (userId == null || userId < 1) {
      return "System";
    }
    try {
      UserBase userBase = USER_INFO_CACHE.getIfPresent(userId);
      if (userBase != null) {
        return userBase.getName() != null ? userBase.getName() : userBase.getUsername();
      } else {
        userBase = userManager.findBaseUser(userId);
        if (userBase != null) {
          USER_INFO_CACHE.put(userId, userBase);
          return userBase.getName() != null ? userBase.getName() : userBase.getUsername();
        }
      }
    } catch (Exception e) {
      log.warn("获取用户名称失败，用户ID：{}", userId, e);
    }
    return "Unknown User";
  }

  @Override
  protected BaseRepository<UserOperationLog, Long> getRepository() {
    return userOperationLogRepo;
  }
}
