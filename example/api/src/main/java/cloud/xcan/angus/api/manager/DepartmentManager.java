package cloud.xcan.angus.api.manager;

import cloud.xcan.angus.api.commonlink.department.Department;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

public interface DepartmentManager {

  List<Department> find(Collection<Long> ids);

  Page<Department> findAll(@Nullable Specification<Department> spec, Pageable pageable);

  Department findAndCheck(Long id);

  List<Department> findAndCheck(Collection<Long> ids);

  void checkExists(Collection<Long> ids);

  List<Department> checkAndGetParent(Long tenantId, List<Department> Departments);
}
