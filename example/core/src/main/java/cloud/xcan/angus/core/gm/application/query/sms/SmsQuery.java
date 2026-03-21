package cloud.xcan.angus.core.gm.application.query.sms;

import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface SmsQuery {

  /**
   * Find SMS records with pagination
   */
  Page<Sms> findRecords(GenericSpecification<Sms> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  /**
   * Get SMS statistics
   */
  SmsStatsVo getStats();
}
