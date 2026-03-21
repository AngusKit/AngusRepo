package cloud.xcan.angus.core.gm.infra.job;

import cloud.xcan.angus.api.commonlink.setting.logretention.LogRetentionCleanupResult;
import cloud.xcan.angus.api.commonlink.setting.logretention.LogRetentionConfig;
import cloud.xcan.angus.core.gm.application.cmd.log.LogRetentionConfigCmd;
import cloud.xcan.angus.core.gm.application.query.log.LogRetentionConfigQuery;
import cloud.xcan.angus.core.job.JobTemplate;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 系统日志、用户操作日志、API请求日志清理任务
 */
@Slf4j
@Component
public class LogClearJob {

  private static final String LOCK_KEY = "gm:job:LogClearJob";

  @Resource
  private JobTemplate jobTemplate;

  @Resource
  private LogRetentionConfigQuery logRetentionConfigQuery;

  @Resource
  private LogRetentionConfigCmd logRetentionConfigCmd;

  @Scheduled(fixedDelay = 6 * 60 * 60 * 1000, initialDelay = 5000)
  public void execute() {
    jobTemplate.execute(LOCK_KEY, 10, TimeUnit.MINUTES, () -> {
      try {
        log.info("开始执行日志清理任务");
        List<LogRetentionConfig> configs = logRetentionConfigQuery.findList();

        if (configs.isEmpty()) {
          log.info("未找到日志清理配置，跳过清理任务");
          return;
        }

        int processedConfigs = 0;
        LogRetentionCleanupResult result = new LogRetentionCleanupResult();
        for (LogRetentionConfig config : configs) {
          try {
            logRetentionConfigCmd.cleanupLogs(config, result);
            processedConfigs++;
          } catch (Exception e) {
            log.error("清理应用「{}」的日志失败", config.getApplicationId(), e);
          }
        }

        log.info("日志清理结果：{}", result);

        log.info("日志清理任务完成：处理了 {} 个配置", processedConfigs);
      } catch (Exception e) {
        log.error("日志清理任务执行失败", e);
      }
    });
  }

}
