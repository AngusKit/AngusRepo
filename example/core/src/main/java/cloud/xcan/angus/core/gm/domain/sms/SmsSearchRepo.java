package cloud.xcan.angus.core.gm.domain.sms;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 短信全文搜索仓储接口
 */
@NoRepositoryBean
public interface SmsSearchRepo extends CustomBaseRepository<Sms> {

}
