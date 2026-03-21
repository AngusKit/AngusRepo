package cloud.xcan.angus.core.gm.infra.eureka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

/**
 * Eureka应用信息
 */
@Data
public class EurekaApplication {

  @JsonProperty("name")
  private String name;

  @JsonProperty("instance")
  private List<EurekaInstance> instance;
}
