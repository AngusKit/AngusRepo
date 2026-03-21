package cloud.xcan.angus.core.gm.domain.user;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 用户邀请全文搜索仓储接口
 */
@NoRepositoryBean
public interface UserInviteSearchRepo extends CustomBaseRepository<UserInvite> {

}
