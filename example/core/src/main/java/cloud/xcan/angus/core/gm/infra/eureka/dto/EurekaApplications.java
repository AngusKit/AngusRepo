package cloud.xcan.angus.core.gm.infra.eureka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

/**
 * Eureka应用列表响应
 */
@Data
public class EurekaApplications {

  @JsonProperty("applications")
  private Applications applications;

  @Data
  public static class Applications {

    @JsonProperty("application")
    private List<EurekaApplication> application;
  }
}
