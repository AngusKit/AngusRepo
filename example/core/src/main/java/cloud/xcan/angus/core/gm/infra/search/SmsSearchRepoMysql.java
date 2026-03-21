package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.core.gm.domain.sms.SmsSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

/**
 * 短信全文搜索仓储MySQL实现
 */
@Repository
public class SmsSearchRepoMysql extends SimpleSearchRepository<Sms>
    implements SmsSearchRepo {

}
