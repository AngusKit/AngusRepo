package cloud.xcan.angus.core.gm.infra.persistence.postgres.department;

import cloud.xcan.angus.api.commonlink.department.DepartmentRepo;
import org.springframework.stereotype.Repository;

@Repository("departmentRepo")
public interface DepartmentRepoPostgres extends DepartmentRepo {

}
