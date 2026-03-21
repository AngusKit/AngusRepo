package cloud.xcan.angus.api.commonlink.user.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户设置值基类
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AppearanceValue.class, name = "APPEARANCE"),
    @JsonSubTypes.Type(value = SecurityValue.class, name = "SECURITY"),
    @JsonSubTypes.Type(value = NotificationValue.class, name = "NOTIFICATION")
})
@Schema(description = "用户设置值基类")
public abstract class UserSettingValue {

}
