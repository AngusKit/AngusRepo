package cloud.xcan.angus.core.gm.interfaces.department.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.department.facade.internal.assembler.DepartmentUserAssembler.toDepartmentUserAddVo;
import static cloud.xcan.angus.core.gm.interfaces.department.facade.internal.assembler.DepartmentUserAssembler.toDepartmentUserTransferVo;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.department.DepartmentUser;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserBase;
import cloud.xcan.angus.api.manager.UserManager;
import cloud.xcan.angus.core.gm.application.cmd.department.DepartmentUserCmd;
import cloud.xcan.angus.core.gm.application.query.department.DepartmentUserQuery;
import cloud.xcan.angus.core.gm.interfaces.department.facade.DepartmentUserFacade;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUserAddDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUserFindDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUserRemoveDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.dto.DepartmentUserTransferDto;
import cloud.xcan.angus.core.gm.interfaces.department.facade.internal.assembler.DepartmentUserAssembler;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentUserAddVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentUserTransferVo;
import cloud.xcan.angus.core.gm.interfaces.department.facade.vo.DepartmentUserVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class DepartmentUserFacadeImpl implements DepartmentUserFacade {

  @Resource
  private DepartmentUserCmd departmentUserCmd;

  @Resource
  private DepartmentUserQuery departmentUserQuery;

  @Resource
  private UserManager userManager;

  @Override
  public DepartmentUserAddVo addUsers(Long departmentId, DepartmentUserAddDto dto) {
    int addedCount = departmentUserCmd.addUsers(departmentId, dto.getUserIds());
    Map<Long, UserBase> userBaseMap = userManager.getUserBaseMap(dto.getUserIds());
    return toDepartmentUserAddVo(departmentId, dto, userBaseMap, addedCount);
  }

  @Override
  public DepartmentUserTransferVo transferUsers(Long departmentId, DepartmentUserTransferDto dto) {
    int transferredCount = departmentUserCmd.transferUsers(departmentId,
        dto.getTargetDepartmentId(), dto.getUserIds());
    return toDepartmentUserTransferVo(departmentId, dto, transferredCount);
  }

  @Override
  public void removeUser(Long departmentId, Long userId) {
    departmentUserCmd.removeUser(departmentId, userId);
  }

  @Override
  public void removeUsers(Long departmentId, DepartmentUserRemoveDto dto) {
    departmentUserCmd.removeUsers(departmentId, dto.getUserIds());
  }

  @Override
  public PageResult<DepartmentUserVo> listUsers(Long departmentId, DepartmentUserFindDto dto) {
    GenericSpecification<User> spec = DepartmentUserAssembler.getUserSpecification(dto);
    Page<User> page = departmentUserQuery.findUsers(departmentId, spec, dto.tranPage());

    // 获取部门用户关系映射，用于设置isManager等信息
    List<DepartmentUser> users = departmentUserQuery.findByDepartmentId(departmentId);
    Map<Long, DepartmentUser> userMap = users.stream()
        .collect(Collectors.toMap(DepartmentUser::getUserId, user -> user,
            (existing, replacement) -> existing));
    return buildVoPageResult(page, user -> {
      DepartmentUser departmentUser = userMap.get(user.getId());
      if (departmentUser != null) {
        return DepartmentUserAssembler.toUserVo(departmentUser, user);
      } else {
        return DepartmentUserAssembler.toUserVo(user);
      }
    });
  }

  @Override
  public PageResult<DepartmentUserVo> listUsersNotInDepartment(Long departmentId,
      DepartmentUserFindDto dto) {
    GenericSpecification<User> spec = DepartmentUserAssembler.getUserSpecification(dto);
    Page<User> page = departmentUserQuery.findUsersNotInDepartment(departmentId, spec,
        dto.tranPage());
    return buildVoPageResult(page, DepartmentUserAssembler::toUserVo);
  }

}
