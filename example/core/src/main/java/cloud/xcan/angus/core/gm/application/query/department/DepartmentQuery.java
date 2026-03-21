package cloud.xcan.angus.core.gm.application.query.department;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.department.Department;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface DepartmentQuery {

  /**
   * 根据ID查找部门并检查是否存在
   */
  Department findAndCheck(Long id);

  /**
   * 分页查询部门列表
   */
  Page<Department> find(GenericSpecification<Department> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  /**
   * 查找部门树形结构
   */
  List<Department> findTree(Long parentId, EnabledStatus status, String keyword);

  /**
   * 获取部门统计数据
   */
  DepartmentStatsVo getStats();

  /**
   * 获取部门路径
   */
  List<Department> getPath(Long id);

  /**
   * 根据用户ID查找所属部门列表
   */
  List<Department> findByUserId(Long userId);

  /**
   * 查找子部门列表
   */
  List<Department> findChildren(Long parentId, Boolean recursive);

  /**
   * 检查编码是否存在
   */
  boolean existsByCode(String code);

  /**
   * 统计租户下的部门数量
   */
  Long countByTenantId(Long tenantId);

  /**
   * 根据ID列表批量查找部门
   */
  List<Department> findAllById(List<Long> ids);
}
