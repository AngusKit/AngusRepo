package cloud.xcan.angus.core.gm.domain.sms;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SmsProviderRepo extends BaseRepository<SmsProvider, Long> {

  Optional<SmsProvider> findByIsDefaultTrue();

}

