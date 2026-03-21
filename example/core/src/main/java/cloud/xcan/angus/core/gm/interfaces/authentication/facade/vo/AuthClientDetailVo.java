package cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo;

import cloud.xcan.angus.api.commonlink.client.enums.ClientSource;
import cloud.xcan.angus.api.enums.Platform;
import cloud.xcan.angus.remote.vo.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class AuthClientDetailVo extends TenantAuditingVo {

  /**
   * OAuth2客户端字段
   */
  @Schema(description = "Client primary key id. Automatically generate a UUID string value if not specified")
  private String id;

  @Schema(description =
      "OAuth2 registered client identifier. The clientId uniquely identifies an application "
          + "to the OAuth2 server, enabling authorization and token requests")
  private String clientId;

  @Schema(description = "The time at which the client identifier was issued")
  private LocalDateTime clientIdIssuedAt;

  @Schema(description =
      "OAuth2 registered client secret or null if not available. The client secret securely "
          + "authenticates the application's identity, ensuring only trusted clients access protected resources")
  private String clientSecret;

  @Schema(description = "The time at which the client secret expires or null if it does not expire.")
  private LocalDateTime clientSecretExpiresAt;

  @Schema(description = "OAuth2 registered client name")
  private String clientName;

  @Schema(description = "The authentication method(s) that the client may use")
  private Set<String> clientAuthenticationMethods;

  @Schema(description = "The authorization grant type(s) that the client may use")
  private Set<String> authorizationGrantTypes;

  @Schema(description = "The redirect URI(s) that the client may use in redirect-based flows")
  private Set<String> redirectUris;

  @Schema(description = "The post logout redirect URI(s) that the client may use for logout. "
      + "The post_logout_redirect_uri parameter is used by the client when requesting that the End-User's "
      + "User Agent be redirected to after a logout has been performed")
  private Set<String> postLogoutRedirectUris;

  @Schema(description = "The scope(s) that the client may use")
  private Set<String> scopes;

  @Schema(description = "The client configuration settings")
  private Map<String, Object> clientSettings;

  @Schema(description = "The token configuration settings")
  private Map<String, Object> tokenSettings;

  /**
   * AngusGM客户端信息
   */
  @Schema(description = "Client description")
  private String description;

  @Schema(description = "Client enabled or disabled status", defaultValue = "true")
  private boolean enabled;

  @Schema(description = "The platform used by the client")
  private Platform platform;

  @Schema(description = "The registered source of client")
  private ClientSource source;

  @Schema(description = "The business tag of client")
  private String bizTag;

}
