package cloud.xcan.angus.core.gm.domain.log;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 用户操作日志搜索仓储接口（全文搜索）
 */
@NoRepositoryBean
public interface UserOperationLogSearchRepo extends CustomBaseRepository<UserOperationLog> {
  // 继承全文搜索能力
}
