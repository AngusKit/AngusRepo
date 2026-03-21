package cloud.xcan.angus.core.gm.infra.eureka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Data;

/**
 * Eureka实例信息
 */
@Data
public class EurekaInstance {

  @JsonProperty("instanceId")
  private String instanceId;

  @JsonProperty("hostName")
  private String hostName;

  @JsonProperty("ipAddr")
  private String ipAddr;

  @JsonProperty("port")
  private Port port;

  @JsonProperty("securePort")
  private Port securePort;

  @JsonProperty("status")
  private String status;

  @JsonProperty("healthCheckUrl")
  private String healthCheckUrl;

  @JsonProperty("statusPageUrl")
  private String statusPageUrl;

  @JsonProperty("homePageUrl")
  private String homePageUrl;

  @JsonProperty("lastUpdatedTimestamp")
  private Long lastUpdatedTimestamp;

  @JsonProperty("leaseInfo")
  private LeaseInfo leaseInfo;

  @JsonProperty("metadata")
  private Map<String, String> metadata;

  @Data
  public static class Port {

    @JsonProperty("$")
    private Integer value;

    @JsonProperty("@enabled")
    private Boolean enabled;
  }

  @Data
  public static class LeaseInfo {

    @JsonProperty("renewalIntervalInSecs")
    private Integer renewalIntervalInSecs;

    @JsonProperty("durationInSecs")
    private Integer durationInSecs;

    @JsonProperty("registrationTimestamp")
    private Long registrationTimestamp;

    @JsonProperty("lastRenewalTimestamp")
    private Long lastRenewalTimestamp;
  }
}
