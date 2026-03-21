package cloud.xcan.angus.core.gm.interfaces.system.facade.vo;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class MemoryUsageVo {

  private String total;
  private String used;
  private String free;
  private Double usagePercent;
  private String swapTotal;
  private String swapUsed;
  private String swapFree;
  private List<MemoryHistory> history;

  @Data
  public static class MemoryHistory {

    private LocalDateTime time;
    private String used;
    private Double usagePercent;
  }
}
