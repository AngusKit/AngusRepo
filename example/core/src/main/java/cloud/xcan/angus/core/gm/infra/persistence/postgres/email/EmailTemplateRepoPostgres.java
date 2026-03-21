package cloud.xcan.angus.core.gm.infra.persistence.postgres.email;

import cloud.xcan.angus.core.gm.domain.email.EmailTemplateRepo;
import org.springframework.stereotype.Repository;

@Repository("emailTemplateRepo")
public interface EmailTemplateRepoPostgres extends EmailTemplateRepo {

}
