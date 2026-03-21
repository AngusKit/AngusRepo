package cloud.xcan.angus.api.commonlink;

import cloud.xcan.angus.spec.experimental.Value;
import lombok.Getter;


@Getter
public enum OrgType implements Value<String> {
  /*TENANT*/
  USER,
  DEPT,
  GROUP;

  @Override
  public String getValue() {
    return this.name();
  }
}
