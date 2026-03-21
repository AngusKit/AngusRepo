package cloud.xcan.angus.api.commonlink.email;

import cloud.xcan.angus.spec.experimental.Value;

public enum EmailStatus implements Value<String> {
  PENDING,
  SENDING,
  SENT,
  DELIVERED,
  FAILED,
  BOUNCED,
  CANCELLED;

  @Override
  public String getValue() {
    return this.name();
  }

  public boolean isFailed() {
    return this.equals(FAILED);
  }
}
