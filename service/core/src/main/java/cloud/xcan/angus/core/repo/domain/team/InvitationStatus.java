package cloud.xcan.angus.core.repo.domain.team;

import lombok.Getter;

@Getter
public enum InvitationStatus {
  PENDING("pending"),
  ACCEPTED("accepted"),
  DECLINED("declined"),
  EXPIRED("expired");

  private final String value;

  InvitationStatus(String value) {
    this.value = value;
  }
}
