package cloud.xcan.angus.core.gm.infra.persistence.mysql.application;

import cloud.xcan.angus.api.commonlink.application.ApplicationRepo;
import org.springframework.stereotype.Repository;

@Repository("applicationRepo")
public interface ApplicationRepoMysql extends ApplicationRepo {

}
