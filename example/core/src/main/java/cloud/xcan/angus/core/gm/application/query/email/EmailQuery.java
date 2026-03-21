package cloud.xcan.angus.core.gm.application.query.email;

import cloud.xcan.angus.api.commonlink.email.EmailStatus;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.email.enums.EmailType;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface EmailQuery {

  Optional<Email> findById(Long id);

  Email findAndCheck(Long id);

  Page<Email> find(GenericSpecification<Email> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  List<Email> findAll();

  List<Email> findByStatus(EmailStatus status);

  List<Email> findByType(EmailType type);

  EmailStatsVo getStatistics();
}
