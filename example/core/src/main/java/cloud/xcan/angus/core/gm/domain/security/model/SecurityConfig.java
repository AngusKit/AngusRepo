package cloud.xcan.angus.core.gm.domain.security.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 安全配置基类
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = PasswordPolicyConfig.class, name = "PASSWORD_POLICY"),
    @JsonSubTypes.Type(value = LoginSecurityConfig.class, name = "TWO_FACTOR"),
    @JsonSubTypes.Type(value = IpWhitelistConfig.class, name = "IP_WHITELIST"),
    @JsonSubTypes.Type(value = SecurityNotificationConfig.class, name = "NOTIFICATION_CONFIG")
})
@Schema(description = "安全配置基类")
public abstract class SecurityConfig {

}
