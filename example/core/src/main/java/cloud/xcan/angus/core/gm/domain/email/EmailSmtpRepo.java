package cloud.xcan.angus.core.gm.domain.email;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface EmailSmtpRepo extends BaseRepository<EmailSmtp, Long> {

  /**
   * 查找默认SMTP配置
   */
  @Query("SELECT e FROM EmailSmtp e WHERE e.isDefault = true")
  EmailSmtp findByIsDefaultTrue();
}

