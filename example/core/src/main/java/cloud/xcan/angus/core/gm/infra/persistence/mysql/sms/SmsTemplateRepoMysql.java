package cloud.xcan.angus.core.gm.infra.persistence.mysql.sms;

import cloud.xcan.angus.core.gm.domain.sms.SmsTemplateRepo;
import org.springframework.stereotype.Repository;

@Repository("smsTemplateRepo")
public interface SmsTemplateRepoMysql extends SmsTemplateRepo {

}

