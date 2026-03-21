package cloud.xcan.angus.core.gm.domain.email;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface EmailSearchRepo extends CustomBaseRepository<Email> {

}
