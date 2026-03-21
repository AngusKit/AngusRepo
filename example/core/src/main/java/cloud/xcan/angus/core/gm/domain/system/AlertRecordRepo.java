package cloud.xcan.angus.core.gm.domain.system;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 告警记录仓储接口
 */
@NoRepositoryBean
public interface AlertRecordRepo extends BaseRepository<AlertRecord, Long> {

}
