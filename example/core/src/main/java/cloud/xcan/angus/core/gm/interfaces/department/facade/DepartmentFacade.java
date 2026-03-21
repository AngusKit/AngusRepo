package cloud.xcan.angus.core.gm.interfaces.department.facade;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentCreateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentFindDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentManagerUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentDetailVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentManagerUpdateVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentPathVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentStatsVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.List;

public interface DepartmentFacade {

  /**
   * 创建部门
   */
  DepartmentDetailVo create(DepartmentCreateDto dto);

  /**
   * 更新部门
   */
  DepartmentDetailVo update(Long id, DepartmentUpdateDto dto);

  /**
   * 更新部门状态（启用/禁用）
   */
  DepartmentDetailVo updateStatus(Long id, EnabledStatusUpdateDto dto);

  /**
   * 设置部门负责人
   */
  DepartmentManagerUpdateVo updateManager(Long id, DepartmentManagerUpdateDto dto);

  /**
   * 删除部门
   */
  void delete(Long id);

  /**
   * 获取部门详情
   */
  DepartmentDetailVo getDetail(Long id);

  /**
   * 分页查询部门列表
   */
  PageResult<DepartmentDetailVo> list(DepartmentFindDto dto);

  /**
   * 获取部门树形结构
   */
  List<DepartmentDetailVo> getTree(Long parentId, EnabledStatus status, Boolean includeUsers,
      String keyword);

  /**
   * 获取部门路径
   */
  DepartmentPathVo getPath(Long id);

  /**
   * 获取子部门列表
   */
  List<DepartmentDetailVo> getChildren(Long id, Boolean recursive);

  /**
   * 获取部门统计数据
   */
  DepartmentStatsVo getStats();

}
