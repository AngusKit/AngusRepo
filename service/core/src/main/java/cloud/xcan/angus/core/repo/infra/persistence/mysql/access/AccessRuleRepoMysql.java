package cloud.xcan.angus.core.repo.infra.persistence.mysql.access;

import cloud.xcan.angus.core.repo.domain.access.AccessRuleRepo;
import org.springframework.stereotype.Repository;

@Repository("accessRuleRepo")
public interface AccessRuleRepoMysql extends AccessRuleRepo {


}
