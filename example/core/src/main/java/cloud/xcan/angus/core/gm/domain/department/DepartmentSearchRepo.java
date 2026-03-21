package cloud.xcan.angus.core.gm.domain.department;

import cloud.xcan.angus.api.commonlink.department.Department;
import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface DepartmentSearchRepo extends CustomBaseRepository<Department> {

}
