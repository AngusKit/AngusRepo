package cloud.xcan.angus.api.commonlink.setting.model;

import cloud.xcan.angus.api.commonlink.user.model.SecurityValue;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 设置值基类
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = LocaleValue.class, name = "LOCALE"),
    @JsonSubTypes.Type(value = SecurityValue.class, name = "SECURITY"),
    @JsonSubTypes.Type(value = SocialValue.class, name = "SOCIAL"),
    @JsonSubTypes.Type(value = BackupSettingsValue.class, name = "BACKUP_SETTINGS"),
    @JsonSubTypes.Type(value = LogRetentionConfigsValue.class, name = "LOG_RETENTION_CONFIGS"),
    @JsonSubTypes.Type(value = EurekaConfigValue.class, name = "EUREKA_CONFIG"),
    @JsonSubTypes.Type(value = AlertRulesValue.class, name = "ALERT_RULES")
})
@Schema(description = "设置值基类")
public abstract class SettingValue {

}
