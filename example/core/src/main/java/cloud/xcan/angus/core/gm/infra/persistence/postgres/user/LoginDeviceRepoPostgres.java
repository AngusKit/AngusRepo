package cloud.xcan.angus.core.gm.infra.persistence.postgres.user;

import cloud.xcan.angus.core.gm.domain.user.LoginDeviceRepo;

/**
 * 登录设备仓储PostgreSQL实现
 */
@org.springframework.stereotype.Repository
public interface LoginDeviceRepoPostgres extends LoginDeviceRepo {
  // 继承领域层接口，Spring会根据配置自动选择实现
}
