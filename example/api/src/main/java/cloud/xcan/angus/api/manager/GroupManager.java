package cloud.xcan.angus.api.manager;

import cloud.xcan.angus.api.commonlink.group.Group;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

public interface GroupManager {

  List<Group> find(Collection<Long> ids);

  Page<Group> findAll(@Nullable Specification<Group> spec, Pageable pageable);

  Group findAndCheck(Long id);

  List<Group> findAndCheck(Collection<Long> ids);

  void checkExists(Collection<Long> ids);

  Group checkValid(Long id);

  List<Group> checkValid(Collection<Long> ids);

  List<Group> findByTenantId(Long tenantId);

}
