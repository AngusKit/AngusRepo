package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.email.EmailSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public class EmailSearchRepoMysql extends SimpleSearchRepository<Email>
    implements EmailSearchRepo {

}
