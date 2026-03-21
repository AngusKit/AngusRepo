package cloud.xcan.angus.core.gm.infra.persistence.mysql.department;

import cloud.xcan.angus.api.commonlink.department.DepartmentUserRepo;
import org.springframework.stereotype.Repository;

@Repository("departmentUserRepo")
public interface DepartmentUserRepoMysql extends DepartmentUserRepo {

}
