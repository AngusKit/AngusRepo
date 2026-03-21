package cloud.xcan.angus.api.commonlink.setting.logretention;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 日志清理配置
 */
@Data
@Accessors(chain = true)
public class LogRetentionConfig implements Serializable {

  /**
   * 应用ID（作为唯一标识）
   */
  private Long applicationId;

  /**
   * 用户日志保留天数
   */
  private int userLogRetentionDays = 90;

  /**
   * 系统日志保留天数
   */
  private int systemLogRetentionDays = 60;

  /**
   * API日志保留天数
   */
  private int apiLogRetentionDays = 30;

  /**
   * 上次清理时间
   */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime lastCleanupDate;

}
