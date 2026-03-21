package cloud.xcan.angus.core.gm.domain.email;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

@NoRepositoryBean
public interface EmailTrackingRepo extends BaseRepository<EmailTracking, Long> {

  /**
   * 根据邮件ID查找追踪记录
   */
  Optional<EmailTracking> findByEmailId(Long emailId);

  /**
   * 根据邮件ID列表批量查找追踪记录
   */
  @Query("SELECT et FROM EmailTracking et WHERE et.emailId IN :emailIds")
  List<EmailTracking> findByEmailIdIn(@Param("emailIds") List<Long> emailIds);
}
