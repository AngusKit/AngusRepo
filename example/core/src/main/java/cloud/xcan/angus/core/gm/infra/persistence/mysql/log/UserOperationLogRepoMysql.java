package cloud.xcan.angus.core.gm.infra.persistence.mysql.log;

import cloud.xcan.angus.core.gm.domain.log.UserOperationLogRepo;

/**
 * 用户操作日志仓储MySQL实现
 */
@org.springframework.stereotype.Repository
public interface UserOperationLogRepoMysql extends UserOperationLogRepo {
  // 继承领域层接口，Spring会根据配置自动选择实现
}
