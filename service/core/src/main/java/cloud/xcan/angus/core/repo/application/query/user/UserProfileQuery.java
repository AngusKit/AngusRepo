package cloud.xcan.angus.core.repo.application.query.user;

import cloud.xcan.angus.core.repo.domain.user.UserApiToken;
import cloud.xcan.angus.core.repo.domain.user.UserProfile;
import java.util.List;
import java.util.Optional;

public interface UserProfileQuery {

  Optional<UserProfile> findById(Long id);

  UserProfile findAndCheck(Long id);

  List<UserApiToken> findTokensByUserId(Long userId);
}
