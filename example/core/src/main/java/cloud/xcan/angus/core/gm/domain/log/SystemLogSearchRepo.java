package cloud.xcan.angus.core.gm.domain.log;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 系统日志搜索仓储接口（全文搜索）
 */
@NoRepositoryBean
public interface SystemLogSearchRepo extends CustomBaseRepository<SystemLog> {
  // 继承全文搜索能力
}
