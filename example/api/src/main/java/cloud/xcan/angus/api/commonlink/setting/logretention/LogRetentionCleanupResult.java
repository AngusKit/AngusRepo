package cloud.xcan.angus.api.commonlink.setting.logretention;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 日志清理结果
 */
@Data
public class LogRetentionCleanupResult implements Serializable {

  /**
   * 清理任务ID
   */
  private String jobId;

  /**
   * 任务状态
   */
  private String status;

  /**
   * 开始时间
   */
  private LocalDateTime startTime;

  /**
   * 结束时间
   */
  private LocalDateTime endTime;

  /**
   * 执行时长
   */
  private String duration;

  /**
   * 删除的用户日志数
   */
  private Long userLogsDeleted;

  /**
   * 删除的系统日志数
   */
  private Long systemLogsDeleted;

  /**
   * 删除的API日志数
   */
  private Long apiLogsDeleted;

  /**
   * 总删除记录数
   */
  private Long totalRecordsDeleted;

  /**
   * 释放空间大小（字节）
   */
  private Long totalSizeFreed;

  /**
   * 错误列表
   */
  private List<String> errors = new ArrayList<>();

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("jobId", jobId)
        .append("status", status)
        .append("startTime", startTime)
        .append("endTime", endTime)
        .append("duration", duration)
        .append("userLogsDeleted", userLogsDeleted)
        .append("systemLogsDeleted", systemLogsDeleted)
        .append("apiLogsDeleted", apiLogsDeleted)
        .append("totalRecordsDeleted", totalRecordsDeleted)
        .append("totalSizeFreed", totalSizeFreed)
        .append("errors", errors)
        .toString();
  }
}
