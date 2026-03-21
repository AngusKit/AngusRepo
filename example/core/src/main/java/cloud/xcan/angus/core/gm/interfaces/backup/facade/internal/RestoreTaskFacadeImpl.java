package cloud.xcan.angus.core.gm.interfaces.backup.facade.internal;

import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.backup.BackupRestoreTaskCmd;
import cloud.xcan.angus.core.gm.application.query.backup.BackupRestoreTaskQuery;
import cloud.xcan.angus.core.gm.domain.backup.RestoreTask;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.RestoreTaskFacade;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupValidateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.RestoreCreateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.RestoreFindDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.internal.assembler.RestoreTaskAssembler;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupValidationVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.RestoreTaskDetailVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.RestoreTaskListVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class RestoreTaskFacadeImpl implements RestoreTaskFacade {

  @Resource
  private BackupRestoreTaskCmd restoreTaskCmd;

  @Resource
  private BackupRestoreTaskQuery restoreTaskQuery;

  @NameJoin
  @Override
  public RestoreTaskDetailVo create(RestoreCreateDto dto) {
    RestoreTask task = RestoreTaskAssembler.toCreateDomain(dto);
    RestoreTask created = restoreTaskCmd.create(task);
    return RestoreTaskAssembler.toDetailVo(created);
  }

  @NameJoin
  @Override
  public RestoreTaskDetailVo getDetail(Long id) {
    RestoreTask task = restoreTaskQuery.findAndCheck(id);
    return RestoreTaskAssembler.toDetailVo(task);
  }

  @NameJoin
  @Override
  public PageResult<RestoreTaskListVo> list(RestoreFindDto dto) {
    GenericSpecification<RestoreTask> spec = RestoreTaskAssembler.getSpecification(dto);
    Page<RestoreTask> page = restoreTaskQuery.find(spec, dto.tranPage());
    return buildVoPageResult(page, RestoreTaskAssembler::toListVo);
  }

  @Override
  public BackupValidationVo validate(BackupValidateDto dto) {
    // TODO: 实现备份文件验证逻辑
    return RestoreTaskAssembler.toValidationVo(dto);
  }
}
