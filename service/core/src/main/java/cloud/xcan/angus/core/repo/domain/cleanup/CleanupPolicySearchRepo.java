package cloud.xcan.angus.core.repo.domain.cleanup;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 清理策略搜索仓储接口（支持全文搜索）
 */
@NoRepositoryBean
public interface CleanupPolicySearchRepo extends CustomBaseRepository<CleanupPolicy> {
    // 继承全文搜索能力
    // 搜索字段包括：name、description
}