package cloud.xcan.angus.core.gm.application.cmd.log;

import cloud.xcan.angus.core.gm.domain.log.UserOperationLog;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.gm.domain.log.enums.ResponseStatus;

/**
 * 用户操作日志命令服务接口
 */
public interface UserOperationLogCmd {

  UserOperationLog log(OperationAction action, ResourceType resourceType, Long resourceId,
      String resource, String details, ResponseStatus responseStatus, String errorMessage);

  UserOperationLog log(OperationAction action, ResourceType resourceType, Long resourceId,
      String resource, String details, ResponseStatus responseStatus);

  UserOperationLog logByMessageKey(OperationAction action, ResourceType resourceType,
      Long resourceId, String resource, String detailsKey, Object[] detailsArgs,
      ResponseStatus responseStatus, String errorMessage);

  UserOperationLog logSuccessByMessageKey(OperationAction action, ResourceType resourceType,
      Long resourceId, String resource, String detailsKey, Object[] detailsArgs);

  UserOperationLog logFailureByMessageKey(OperationAction action, ResourceType resourceType,
      Long resourceId, String resource, String detailsKey, Object[] detailsArgs,
      String errorMessage);
}
