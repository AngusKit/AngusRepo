package cloud.xcan.angus.core.gm.domain.email;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 邮件模板全文搜索仓储接口
 */
@NoRepositoryBean
public interface EmailTemplateSearchRepo extends CustomBaseRepository<EmailTemplate> {

}
