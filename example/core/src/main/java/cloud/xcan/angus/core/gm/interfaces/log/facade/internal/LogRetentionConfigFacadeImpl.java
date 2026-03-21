package cloud.xcan.angus.core.gm.interfaces.log.facade.internal;

import cloud.xcan.angus.api.commonlink.setting.logretention.LogRetentionConfig;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.log.LogRetentionConfigCmd;
import cloud.xcan.angus.core.gm.application.query.log.LogRetentionConfigQuery;
import cloud.xcan.angus.core.gm.interfaces.log.facade.LogRetentionConfigFacade;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.LogRetentionConfigCleanupDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.LogRetentionConfigFindDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.LogRetentionConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.internal.assembler.LogRetentionConfigAssembler;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.LogRetentionConfigCleanupVo;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.LogRetentionConfigDetailVo;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 日志清理配置门面实现
 */
@Component
public class LogRetentionConfigFacadeImpl implements LogRetentionConfigFacade {

  @Resource
  private LogRetentionConfigCmd logRetentionConfigCmd;

  @Resource
  private LogRetentionConfigQuery logRetentionConfigQuery;

  @NameJoin
  @Override
  public LogRetentionConfigDetailVo update(Long applicationId,
      LogRetentionConfigUpdateDto dto) {
    LogRetentionConfig config = LogRetentionConfigAssembler.toLogRetentionConfig(dto);
    LogRetentionConfig updatedConfig = logRetentionConfigCmd.update(config);
    return LogRetentionConfigAssembler.toDetailVo(updatedConfig);
  }

  @NameJoin
  @Override
  public List<LogRetentionConfigDetailVo> batchUpdate(List<LogRetentionConfigUpdateDto> dto) {
    List<LogRetentionConfig> configs = LogRetentionConfigAssembler.toDomainList(dto);
    List<LogRetentionConfig> updatedConfigs = logRetentionConfigCmd.batchUpdate(configs);
    return updatedConfigs.stream().map(LogRetentionConfigAssembler::toDetailVo)
        .collect(Collectors.toList());
  }

  @NameJoin
  @Override
  public List<LogRetentionConfigDetailVo> findList(LogRetentionConfigFindDto dto) {
    return logRetentionConfigQuery.findList(dto.getApplicationId()).stream()
        .map(LogRetentionConfigAssembler::toDetailVo)
        .collect(Collectors.toList());
  }

  @Override
  public LogRetentionConfigCleanupVo cleanup(Long applicationId, LogRetentionConfigCleanupDto dto) {
    Boolean dryRun = dto != null ? dto.getDryRun() : false;
    return LogRetentionConfigAssembler.toCleanupVo(
        logRetentionConfigCmd.cleanup(applicationId, dryRun));
  }
}
