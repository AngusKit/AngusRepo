package cloud.xcan.angus.core.repo.application.query.user.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.repo.application.query.user.UserProfileQuery;
import cloud.xcan.angus.core.repo.domain.user.UserApiToken;
import cloud.xcan.angus.core.repo.domain.user.UserApiTokenRepo;
import cloud.xcan.angus.core.repo.domain.user.UserProfile;
import cloud.xcan.angus.core.repo.domain.user.UserProfileRepo;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Biz
@Transactional(readOnly = true)
public class UserProfileQueryImpl implements UserProfileQuery {

  @Resource
  private UserProfileRepo userProfileRepo;

  @Resource
  private UserApiTokenRepo userApiTokenRepo;

  @Override
  public Optional<UserProfile> findById(Long id) {
    return userProfileRepo.findById(id);
  }

  @Override
  public UserProfile findAndCheck(Long id) {
    return userProfileRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("用户不存在: " + id));
  }

  @Override
  public List<UserApiToken> findTokensByUserId(Long userId) {
    return userApiTokenRepo.findByUserId(userId);
  }
}
