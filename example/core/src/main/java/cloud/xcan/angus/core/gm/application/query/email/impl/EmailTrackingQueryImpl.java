package cloud.xcan.angus.core.gm.application.query.email.impl;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.email.EmailTrackingQuery;
import cloud.xcan.angus.core.gm.domain.email.EmailTracking;
import cloud.xcan.angus.core.gm.domain.email.EmailTrackingRepo;
import jakarta.annotation.Resource;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class EmailTrackingQueryImpl implements EmailTrackingQuery {

  @Resource
  private EmailTrackingRepo emailTrackingRepo;

  @Override
  public Optional<EmailTracking> findByEmailId(Long emailId) {
    return new BizTemplate<Optional<EmailTracking>>() {
      @Override
      protected Optional<EmailTracking> process() {
        return emailTrackingRepo.findByEmailId(emailId);
      }
    }.execute();
  }
}
