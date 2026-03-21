package cloud.xcan.angus.core.gm.domain.user;

import cloud.xcan.angus.api.commonlink.user.enums.InviteStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface UserInviteRepo extends BaseRepository<UserInvite, Long> {

  /**
   * 根据邀请码查找邀请
   */
  Optional<UserInvite> findByInviteCode(String inviteCode);

  /**
   * 检查邀请码是否存在
   */
  boolean existsByInviteCode(String inviteCode);

  /**
   * 检查邮箱是否存在待处理的邀请
   */
  boolean existsByEmailAndStatus(String email, InviteStatus status);

  /**
   * 统计指定状态的邀请数量
   */
  long countByStatus(InviteStatus status);

  /**
   * 统计指定状态和应用ID的邀请数量（appId 匹配 app_id 字段，含 null）
   */
  long countByStatusAndAppId(InviteStatus status, Long appId);
}
