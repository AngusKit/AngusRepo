package cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo;

import cloud.xcan.angus.api.commonlink.GMConstant;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class AccountVo {

  private Long tenantId;

  private String tenantName;

  private Long userId;

  private String linkSecret;

  private boolean hasPassword;

  /**
   * Valid within 5 minute by default
   *
   * @see GMConstant#LINK_SECRET_VALID_SECOND
   */
  private Integer expiresIn = GMConstant.LINK_SECRET_VALID_SECOND;

}
