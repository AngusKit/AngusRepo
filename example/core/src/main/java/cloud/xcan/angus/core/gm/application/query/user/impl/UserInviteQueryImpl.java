package cloud.xcan.angus.core.gm.application.query.user.impl;

import cloud.xcan.angus.api.commonlink.user.enums.InviteStatus;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.user.UserInviteQuery;
import cloud.xcan.angus.core.gm.domain.user.UserInvite;
import cloud.xcan.angus.core.gm.domain.user.UserInviteRepo;
import cloud.xcan.angus.core.gm.domain.user.UserInviteSearchRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class UserInviteQueryImpl implements UserInviteQuery {

  @Resource
  private UserInviteRepo userInviteRepo;

  @Resource
  private UserInviteSearchRepo userInviteSearchRepo;

  @Override
  public UserInvite findAndCheck(Long id) {
    return new BizTemplate<UserInvite>() {
      @Override
      protected UserInvite process() {
        return userInviteRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("邀请「{0}」不存在", new Object[]{id}));
      }
    }.execute();
  }

  @Override
  public Page<UserInvite> find(GenericSpecification<UserInvite> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<UserInvite>>() {
      @Override
      protected Page<UserInvite> process() {
        return fullTextSearch
            ? userInviteSearchRepo.find(spec.getCriteria(), pageable, UserInvite.class, match)
            : userInviteRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public UserInvite findAndCheck(String inviteCode) {
    return new BizTemplate<UserInvite>(false) {
      @Override
      protected UserInvite process() {
        // 查找邀请记录
        Optional<UserInvite> inviteOpt = userInviteRepo.findByInviteCode(inviteCode);
        if (inviteOpt.isEmpty()) {
          throw ProtocolException.of("邀请码未找到「{0}」", new Object[]{inviteCode});
        }

        UserInvite invite = inviteOpt.get();

        // 检查状态
        if (invite.getStatus() != InviteStatus.PENDING) {
          throw ProtocolException.of("邀请码「{0}」状态无效", new Object[]{inviteCode});
        }

        // 检查是否过期
        if (invite.getExpiryDate() != null
            && invite.getExpiryDate().isBefore(LocalDateTime.now())) {
          throw ProtocolException.of("邀请码「{0}」已过期", new Object[]{inviteCode});
        }
        return invite;
      }
    }.execute();
  }

  @Override
  public Optional<UserInvite> findByInviteCode(String inviteCode) {
    return userInviteRepo.findByInviteCode(inviteCode);
  }
}
