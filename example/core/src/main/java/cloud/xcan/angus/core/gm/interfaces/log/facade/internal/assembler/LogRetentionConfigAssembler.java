package cloud.xcan.angus.core.gm.interfaces.log.facade.internal.assembler;

import static cloud.xcan.angus.core.gm.infra.utils.CommonUtils.formatFileSize;

import cloud.xcan.angus.api.commonlink.setting.logretention.LogRetentionCleanupResult;
import cloud.xcan.angus.api.commonlink.setting.logretention.LogRetentionConfig;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.LogRetentionConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.LogRetentionConfigCleanupVo;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.LogRetentionConfigDetailVo;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 日志清理配置数据组装器
 */
public class LogRetentionConfigAssembler {

  public static LogRetentionConfigDetailVo toDetailVo(LogRetentionConfig config) {
    LogRetentionConfigDetailVo vo = new LogRetentionConfigDetailVo();
    vo.setApplicationId(config.getApplicationId());
    vo.setUserLogRetentionDays(config.getUserLogRetentionDays());
    vo.setSystemLogRetentionDays(config.getSystemLogRetentionDays());
    vo.setApiLogRetentionDays(config.getApiLogRetentionDays());
    vo.setLastCleanupDate(config.getLastCleanupDate());
    return vo;
  }

  public static List<LogRetentionConfig> toDomainList(List<LogRetentionConfigUpdateDto> dto) {
    return dto.stream().map(LogRetentionConfigAssembler::toLogRetentionConfig)
        .collect(Collectors.toList());
  }

  public static LogRetentionConfig toLogRetentionConfig(LogRetentionConfigUpdateDto item) {
    LogRetentionConfig config = new LogRetentionConfig();
    config.setApplicationId(item.getApplicationId());
    config.setUserLogRetentionDays(item.getUserLogRetentionDays());
    config.setSystemLogRetentionDays(item.getSystemLogRetentionDays());
    config.setApiLogRetentionDays(item.getApiLogRetentionDays());
    return config;
  }

  public static LogRetentionConfigCleanupVo toCleanupVo(LogRetentionCleanupResult result) {
    LogRetentionConfigCleanupVo vo = new LogRetentionConfigCleanupVo();
    vo.setJobId(result.getJobId());
    vo.setStatus(result.getStatus());
    vo.setStartTime(result.getStartTime());
    vo.setEndTime(result.getEndTime());
    vo.setDuration(result.getDuration());
    vo.setErrors(result.getErrors());

    LogRetentionConfigCleanupVo.CleanupResultVo resultVo =
        new LogRetentionConfigCleanupVo.CleanupResultVo();
    resultVo.setUserLogsDeleted(result.getUserLogsDeleted());
    resultVo.setSystemLogsDeleted(result.getSystemLogsDeleted());
    resultVo.setApiLogsDeleted(result.getApiLogsDeleted());
    resultVo.setTotalRecordsDeleted(result.getTotalRecordsDeleted());
    resultVo.setTotalSizeFreed(result.getTotalSizeFreed());
    resultVo.setTotalSizeFreedFormatted(formatFileSize(result.getTotalSizeFreed()));
    vo.setResult(resultVo);
    return vo;
  }

}
