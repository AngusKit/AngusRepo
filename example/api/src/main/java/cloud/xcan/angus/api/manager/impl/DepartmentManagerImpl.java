package cloud.xcan.angus.api.manager.impl;

import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.spec.experimental.BizConstant.DEFAULT_ROOT_PID;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.department.Department;
import cloud.xcan.angus.api.commonlink.department.DepartmentRepo;
import cloud.xcan.angus.api.manager.DepartmentManager;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public class DepartmentManagerImpl implements DepartmentManager {

  @Resource
  @Qualifier("commonDepartmentRepo")
  private DepartmentRepo departmentRepo;

  @Override
  public List<Department> find(Collection<Long> ids) {
    return departmentRepo.findAllById(ids);
  }

  @Override
  public Page<Department> findAll(@Nullable Specification<Department> spec, Pageable pageable) {
    return departmentRepo.findAll(spec, pageable);
  }

  @Override
  public Department findAndCheck(Long id) {
    return departmentRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "Department"));
  }

  @Override
  public List<Department> findAndCheck(Collection<Long> ids) {
    List<Department> depts = departmentRepo.findAllById(ids);
    assertResourceNotFound(isNotEmpty(depts), ids.iterator().next(), "Department");
    if (ids.size() != depts.size()) {
      for (Department dept : depts) {
        assertResourceNotFound(ids.contains(dept.getId()), dept.getId(), "Department");
      }
    }
    return depts;
  }

  @Override
  public void checkExists(Collection<Long> ids) {
    List<Long> deptIdsDb = departmentRepo.findIdsByIdIn(ids);
    assertResourceNotFound(isNotEmpty(deptIdsDb), ids.iterator().next(), "Department");
    if (ids.size() != deptIdsDb.size()) {
      for (Long deptId : deptIdsDb) {
        assertResourceNotFound(ids.contains(deptId), deptId, "Department");
      }
    }
  }

  @Override
  public List<Department> checkAndGetParent(Long tenantId, List<Department> depts) {
    List<Department> parentsDb = departmentRepo.findByTenantIdAndIdIn(tenantId,
        depts.stream().filter(Department::hasParent).map(Department::getParentId)
            .collect(Collectors.toList()));
    if (isEmpty(parentsDb)) {
      return null;
    }
    Set<Long> parentIds = parentsDb.stream().map(Department::getId).collect(Collectors.toSet());
    for (Department dept : depts) {
      assertResourceNotFound(dept.getParentId().equals(DEFAULT_ROOT_PID)
          || parentIds.contains(dept.getParentId()), dept.getParentId(), "Department");
    }
    return parentsDb;
  }

}
