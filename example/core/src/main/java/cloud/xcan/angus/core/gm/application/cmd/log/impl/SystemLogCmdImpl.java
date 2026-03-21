package cloud.xcan.angus.core.gm.application.cmd.log.impl;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.SystemLogCmd;
import cloud.xcan.angus.core.gm.application.query.log.SystemLogQuery;
import cloud.xcan.angus.core.gm.domain.log.SystemLog;
import cloud.xcan.angus.core.gm.domain.log.SystemLogRepo;
import cloud.xcan.angus.core.gm.domain.log.enums.LogStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统日志命令服务实现
 */
@Service
public class SystemLogCmdImpl extends CommCmd<SystemLog, Long> implements SystemLogCmd {

  @Resource
  private SystemLogRepo systemLogRepo;

  @Resource
  private SystemLogQuery systemLogQuery;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void batchSaveOrUpdate(List<SystemLog> systemLogs) {
    if (systemLogs != null && !systemLogs.isEmpty()) {
      // 使用 saveAll 而不是 batchInsert0，saveAll 会根据 ID 是否存在自动判断插入或更新
      // 如果实体 ID 为 null 或不存在 -> INSERT
      // 如果实体 ID 存在 -> UPDATE (merge)
      systemLogRepo.saveAll(systemLogs);
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id, Boolean permanent) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        SystemLog systemLog = systemLogQuery.findAndCheck(id);

        if (Boolean.TRUE.equals(permanent)) {
          // 永久删除：删除数据库记录和物理文件
          try {
            Path filePath = Paths.get(systemLog.getFilePath());
            if (Files.exists(filePath)) {
              Files.delete(filePath);
            }
          } catch (IOException e) {
            throw new RuntimeException("删除日志文件失败: " + e.getMessage(), e);
          }
          systemLogRepo.delete(systemLog);
        } else {
          // 归档：更新状态为ARCHIVED
          systemLog.setStatus(LogStatus.ARCHIVED);
          systemLogRepo.save(systemLog);
        }
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void batchDelete(List<Long> ids, Boolean permanent) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        for (Long id : ids) {
          delete(id, permanent);
        }
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<SystemLog, Long> getRepository() {
    return systemLogRepo;
  }
}
