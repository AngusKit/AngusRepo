package cloud.xcan.angus.core.gm.interfaces.department.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.department.facade.internal.assembler.DepartmentAssembler.toDepartmentManagerUpdateVo;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.department.Department;
import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.gm.application.cmd.department.DepartmentCmd;
import cloud.xcan.angus.core.gm.application.query.department.DepartmentQuery;
import cloud.xcan.angus.core.gm.interfaces.department.facade.DepartmentFacade;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentCreateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentFindDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentManagerUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.internal.assembler.DepartmentAssembler;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentDetailVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentManagerUpdateVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentPathVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class DepartmentFacadeImpl implements DepartmentFacade {

  @Resource
  private DepartmentCmd departmentCmd;

  @Resource
  private DepartmentQuery departmentQuery;

  @Override
  public DepartmentDetailVo create(DepartmentCreateDto dto) {
    Department department = DepartmentAssembler.toCreateDomain(dto);
    Department saved = departmentCmd.create(department);
    return DepartmentAssembler.toDetailVo(saved);
  }

  @Override
  public DepartmentDetailVo update(Long id, DepartmentUpdateDto dto) {
    Department department = DepartmentAssembler.toUpdateDomain(id, dto);
    Department saved = departmentCmd.update(department);
    return DepartmentAssembler.toDetailVo(saved);
  }

  @Override
  public DepartmentDetailVo updateStatus(Long id, EnabledStatusUpdateDto dto) {
    Department saved = departmentCmd.updateStatus(id, dto.getStatus());
    return DepartmentAssembler.toDetailVo(saved);
  }

  @Override
  public void delete(Long id) {
    departmentCmd.delete(id);
  }

  @Override
  public DepartmentDetailVo getDetail(Long id) {
    Department department = departmentQuery.findAndCheck(id);
    return DepartmentAssembler.toDetailVo(department);
  }

  @Override
  public PageResult<DepartmentDetailVo> list(DepartmentFindDto dto) {
    GenericSpecification<Department> spec = DepartmentAssembler.getSpecification(dto);
    Page<Department> page = departmentQuery.find(spec, dto.tranPage(),
        dto.isFullTextSearch(), getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, DepartmentAssembler::toDetailVo);
  }

  @Override
  public DepartmentStatsVo getStats() {
    return departmentQuery.getStats();
  }

  @Override
  public List<DepartmentDetailVo> getTree(Long parentId, EnabledStatus status,
      Boolean includeUsers, String keyword) {
    List<Department> departments = departmentQuery.findTree(parentId, status, keyword);
    // 构建部门树形结构
    return DepartmentAssembler.buildDepartmentTree(departments);
  }

  @Override
  public DepartmentManagerUpdateVo updateManager(Long id, DepartmentManagerUpdateDto dto) {
    Department department = departmentCmd.updateLeader(id, dto.getLeaderId());
    return toDepartmentManagerUpdateVo(id, dto, department);
  }

  @Override
  public DepartmentPathVo getPath(Long id) {
    List<Department> path = departmentQuery.getPath(id);
    DepartmentPathVo vo = new DepartmentPathVo();

    String pathStr = path.stream()
        .map(Department::getName)
        .collect(Collectors.joining("/"));
    vo.setPath(pathStr);

    List<DepartmentPathVo.PathItemVo> pathArray = path.stream()
        .map(dept -> {
          DepartmentPathVo.PathItemVo item = new DepartmentPathVo.PathItemVo();
          item.setId(dept.getId());
          item.setName(dept.getName());
          return item;
        })
        .collect(Collectors.toList());
    vo.setPathArray(pathArray);

    return vo;
  }

  @Override
  public List<DepartmentDetailVo> getChildren(Long id, Boolean recursive) {
    List<Department> children = departmentQuery.findChildren(id, recursive);
    return DepartmentAssembler.buildDepartmentTree(children);
  }
}
