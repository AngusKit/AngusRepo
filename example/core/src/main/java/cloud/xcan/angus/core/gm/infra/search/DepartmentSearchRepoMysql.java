package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.api.commonlink.department.Department;
import cloud.xcan.angus.core.gm.domain.department.DepartmentSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public class DepartmentSearchRepoMysql extends SimpleSearchRepository<Department>
    implements DepartmentSearchRepo {

}
