package cloud.xcan.angus.core.repo.domain.activity;

import cloud.xcan.angus.spec.ValueObject;

public enum ActivityType implements ValueObject<ActivityType> {
  ;

  public String getValue() {
    return this.name();
  }

  public String getDescMessageKey() {
    return "xcm.repo.activity." + this.name();
  }

  public String getDetailMessageKey() {
    return "xcm.repo.activity.detail." + this.name();
  }

}

