package cloud.xcan.angus.core.gm.infra.job;

import static cloud.xcan.angus.security.principal.HoldPrincipalFilter.USER_REQUEST_TIME;

import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.core.job.JobTemplate;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户在线状态同步
 */
@Slf4j
@Component
public class UserOnlineSyncJob {

  private static final String LOCK_KEY = "gm:job:UserOnlineSyncJob";

  // 超过5分钟没有用户请求认为用户已离线
  private static final int MAX_LIVE_TIMEOUT = 2 * 60 * 1000;

  @Resource
  private JobTemplate jobTemplate;

  @Resource
  private UserRepo userRepo;

  @Resource
  private UserOnlineSyncJob self;

  @Scheduled(fixedDelay = MAX_LIVE_TIMEOUT, initialDelay = 15000)
  public void execute() {
    jobTemplate.execute(LOCK_KEY, 3, TimeUnit.MINUTES, () -> {
      try {
        log.info("开始执行用户在线状态同步");
        Map<Long, LocalDateTime> userRequestTimes = USER_REQUEST_TIME;

        if (userRequestTimes == null || userRequestTimes.isEmpty()) {
          log.debug("没有用户请求记录，跳过同步");
          return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timeoutThreshold = now.minusMinutes(5);
        List<Long> onlineUserIds = new ArrayList<>();
        List<Long> offlineUserIds = new ArrayList<>();

        // 遍历用户请求时间，分类在线和离线用户
        for (Map.Entry<Long, LocalDateTime> entry : userRequestTimes.entrySet()) {
          Long userId = entry.getKey();
          LocalDateTime lastRequestTime = entry.getValue();

          if (lastRequestTime == null) {
            // 如果最后请求时间为空，标记为离线
            offlineUserIds.add(userId);
          } else if (lastRequestTime.isBefore(timeoutThreshold)) {
            // 超过5分钟没有请求，标记为离线
            offlineUserIds.add(userId);
          } else {
            // 5分钟内有请求，标记为在线
            onlineUserIds.add(userId);
          }
        }

        // 批量更新在线状态
        if (!onlineUserIds.isEmpty()) {
          self.updateOnlineStatus(onlineUserIds);
          log.info("批量更新{}个用户为在线状态", onlineUserIds.size());
        }

        if (!offlineUserIds.isEmpty()) {
          self.updateOfflineStatus(offlineUserIds);
          log.info("批量更新{}个用户为离线状态", offlineUserIds.size());
        }

        log.info("用户在线状态同步完成，在线用户：{}，离线用户：{}", onlineUserIds.size(),
            offlineUserIds.size());

      } catch (Exception e) {
        log.error("用户在线状态同步执行失败", e);
      }
    });
  }

  @Transactional(rollbackFor = Exception.class)
  protected void updateOnlineStatus(List<Long> userIds) {
    if (userIds.isEmpty()) {
      return;
    }
    userRepo.updateOnlineStatus(userIds);
  }

  @Transactional(rollbackFor = Exception.class)
  protected void updateOfflineStatus(List<Long> userIds) {
    if (userIds.isEmpty()) {
      return;
    }
    userRepo.updateOfflineStatus(userIds);
  }
}
