package cloud.xcan.angus.core.repo.application.cmd.access.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.repo.application.cmd.access.AccessTokenCmd;
import cloud.xcan.angus.core.repo.domain.access.AccessToken;
import cloud.xcan.angus.core.repo.domain.access.AccessTokenRepo;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class AccessTokenCmdImpl extends CommCmd<AccessToken, Long> implements AccessTokenCmd {

  @Resource
  private AccessTokenRepo accessTokenRepo;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public AccessToken create(AccessToken accessToken) {
    return new BizTemplate<AccessToken>() {
      @Override
      protected void checkParams() {
        // Validate required fields
      }

      @Override
      protected AccessToken process() {
        accessToken.setCreatedDate(LocalDateTime.now());
        if (accessToken.getEnabled() == null) {
          accessToken.setEnabled(true);
        }
        if (accessToken.getUsageCount() == null) {
          accessToken.setUsageCount(0L);
        }
        if (accessToken.getTokenHash() == null) {
          accessToken.setTokenHash(UUID.randomUUID().toString().replace("-", ""));
        }
        insert0(accessToken);
        return accessToken;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void revoke(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        if (!accessTokenRepo.existsById(id)) {
          throw new RuntimeException("访问令牌不存在: " + id);
        }
      }

      @Override
      protected Void process() {
        accessTokenRepo.deleteById(id);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<AccessToken, Long> getRepository() {
    return this.accessTokenRepo;
  }
}
