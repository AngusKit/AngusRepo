package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.user.UserInvite;
import cloud.xcan.angus.core.gm.domain.user.UserInviteSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户邀请全文搜索仓储MySQL实现
 */
@Repository
public class UserInviteSearchRepoMysql extends SimpleSearchRepository<UserInvite>
    implements UserInviteSearchRepo {

}
