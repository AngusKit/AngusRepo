package cloud.xcan.angus.core.gm.domain.application;

import cloud.xcan.angus.api.commonlink.application.Application;
import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ApplicationSearchRepo extends CustomBaseRepository<Application> {
  // 继承全文搜索能力
}
