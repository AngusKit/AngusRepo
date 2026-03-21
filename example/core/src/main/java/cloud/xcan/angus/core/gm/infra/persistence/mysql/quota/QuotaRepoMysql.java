package cloud.xcan.angus.core.gm.infra.persistence.mysql.quota;

import cloud.xcan.angus.api.commonlink.quota.QuotaRepo;
import org.springframework.stereotype.Repository;

@Repository("quotaRepo")
public interface QuotaRepoMysql extends QuotaRepo {

}
