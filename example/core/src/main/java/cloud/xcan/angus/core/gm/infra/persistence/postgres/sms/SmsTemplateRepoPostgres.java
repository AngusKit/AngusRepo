package cloud.xcan.angus.core.gm.infra.persistence.postgres.sms;

import cloud.xcan.angus.core.gm.domain.sms.SmsTemplateRepo;
import org.springframework.stereotype.Repository;

@Repository("smsTemplateRepo")
public interface SmsTemplateRepoPostgres extends SmsTemplateRepo {

}

