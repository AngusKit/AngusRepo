package cloud.xcan.angus.core.gm.interfaces.backup.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.biz.JoinSupplier;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.backup.BackupCmd;
import cloud.xcan.angus.core.gm.application.query.backup.BackupQuery;
import cloud.xcan.angus.core.gm.application.query.backup.BackupRestoreTaskQuery;
import cloud.xcan.angus.core.gm.domain.backup.Backup;
import cloud.xcan.angus.core.gm.domain.backup.enums.BackupStatus;
import cloud.xcan.angus.core.gm.infra.utils.DownloadResult;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.BackupFacade;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupCreateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupFindDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.internal.assembler.BackupAssembler;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupDetailVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupListVo;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.message.ProtocolException;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class BackupFacadeImpl implements BackupFacade {

  @Resource
  private BackupCmd backupCmd;

  @Resource
  private BackupQuery backupQuery;

  @Resource
  private BackupRestoreTaskQuery backupRestoreTaskQuery;

  @Resource
  private JoinSupplier joinSupplier;

  @NameJoin
  @Override
  public BackupDetailVo createBackup(BackupCreateDto dto) {
    Backup backup = BackupAssembler.toCreateDomain(dto);
    Backup created = backupCmd.create(backup);
    return BackupAssembler.toDetailVo(created);
  }

  @Override
  public void deleteBackup(Long id) {
    backupCmd.delete(id);
  }

  @Override
  public void runBackup(Long id) {
    backupCmd.runBackup(id);
  }

  @NameJoin
  @Override
  public BackupDetailVo getBackupDetail(Long id) {
    Backup backup = backupQuery.findAndCheck(id);
    BackupDetailVo vo = BackupAssembler.toDetailVo(backup);
    // 关联恢复历史
    var historyList = backupRestoreTaskQuery.findByBackupId(id);
    vo.setRestoreHistory(joinSupplier.execute(() -> BackupAssembler.toHistoryVo(historyList)));
    return vo;
  }

  @Override
  public DownloadResult downloadBackup(Long id) throws IOException {
    Backup backup = backupQuery.findAndCheck(id);
    if (backup.getStatus() != BackupStatus.COMPLETED) {
      throw ProtocolException.of("只能下载已完成的备份，当前状态: " + backup.getStatus());
    }
    Path filePath = Paths.get(backup.getBackupPath());
    if (!Files.exists(filePath)) {
      throw ProtocolException.of("备份文件不存在: " + backup.getBackupPath());
    }
    String filename = filePath.getFileName().toString();
    long filesize = backup.getFileSize() != null ? backup.getFileSize() : Files.size(filePath);
    InputStreamResource resource = new InputStreamResource(Files.newInputStream(filePath));
    return new DownloadResult(resource, filename, filesize, MediaType.APPLICATION_OCTET_STREAM);
  }

  @NameJoin
  @Override
  public PageResult<BackupListVo> listRecords(BackupFindDto dto) {
    GenericSpecification<Backup> spec = BackupAssembler.getSpecification(dto);
    Page<Backup> page = backupQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, BackupAssembler::toListVo);
  }

  @Override
  public BackupStatsVo getStats() {
    return backupQuery.getStats();
  }

}
