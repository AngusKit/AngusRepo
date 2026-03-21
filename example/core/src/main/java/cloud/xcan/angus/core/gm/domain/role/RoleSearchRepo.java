package cloud.xcan.angus.core.gm.domain.role;

import cloud.xcan.angus.api.commonlink.role.Role;
import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface RoleSearchRepo extends CustomBaseRepository<Role> {
  // 继承全文搜索能力
}
