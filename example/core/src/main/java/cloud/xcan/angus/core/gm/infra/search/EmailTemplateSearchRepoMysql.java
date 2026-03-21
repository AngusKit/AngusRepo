package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.email.EmailTemplate;
import cloud.xcan.angus.core.gm.domain.email.EmailTemplateSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

/**
 * 邮件模板全文搜索仓储MySQL实现
 */
@Repository
public class EmailTemplateSearchRepoMysql extends SimpleSearchRepository<EmailTemplate>
    implements EmailTemplateSearchRepo {

}
