package cloud.xcan.angus.core.repo.domain.reposettings;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

@NoRepositoryBean
public interface WebhookRepo extends BaseRepository<Webhook, Long> {

  List<Webhook> findByActive(Boolean active);

  long countByActive(Boolean active);

  @Transactional
  @Modifying
  @Query(value = "UPDATE webhook SET active = ?2 WHERE id = ?1", nativeQuery = true)
  void updateActiveStatus(Long webhookId, Boolean active);

  @Transactional
  @Modifying
  @Query(value = "UPDATE webhook SET success_count = success_count + 1 WHERE id = ?1", nativeQuery = true)
  void incrementSuccessCount(Long webhookId);

  @Transactional
  @Modifying
  @Query(value = "UPDATE webhook SET failure_count = failure_count + 1 WHERE id = ?1", nativeQuery = true)
  void incrementFailureCount(Long webhookId);
}
