package cloud.xcan.angus.core.gm.infra.persistence.mysql.email;

import cloud.xcan.angus.core.gm.domain.email.EmailTemplateRepo;
import org.springframework.stereotype.Repository;

@Repository("emailTemplateRepo")
public interface EmailTemplateRepoMysql extends EmailTemplateRepo {

}
