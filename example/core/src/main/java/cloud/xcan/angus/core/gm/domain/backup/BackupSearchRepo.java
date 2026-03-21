package cloud.xcan.angus.core.gm.domain.backup;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BackupSearchRepo extends CustomBaseRepository<Backup> {
  // 继承全文搜索能力
}
