package cloud.xcan.angus.core.gm.domain.interfaces;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * API请求日志搜索仓储接口（全文搜索）
 */
@NoRepositoryBean
public interface InterfaceRequestLogSearchRepo extends
    CustomBaseRepository<InterfaceRequestLogInfo> {

}
