package cloud.xcan.angus.core.gm.application.query.email.impl;

import cloud.xcan.angus.api.commonlink.email.EmailStatus;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.email.EmailQuery;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.email.EmailRepo;
import cloud.xcan.angus.core.gm.domain.email.EmailSearchRepo;
import cloud.xcan.angus.core.gm.domain.email.EmailTracking;
import cloud.xcan.angus.core.gm.domain.email.EmailTrackingRepo;
import cloud.xcan.angus.core.gm.domain.email.enums.EmailType;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class EmailQueryImpl implements EmailQuery {

  @Resource
  private EmailRepo emailRepo;

  @Resource
  private EmailSearchRepo emailSearchRepo;

  @Resource
  private EmailTrackingRepo emailTrackingRepo;

  @Override
  public Optional<Email> findById(Long id) {
    return new BizTemplate<Optional<Email>>() {
      @Override
      protected Optional<Email> process() {
        return emailRepo.findById(id);
      }
    }.execute();
  }

  @Override
  public Email findAndCheck(Long id) {
    return new BizTemplate<Email>() {
      @Override
      protected Email process() {
        return emailRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("邮件记录「{0}」不存在", new Object[]{id}));
      }
    }.execute();
  }

  @Override
  public Page<Email> find(GenericSpecification<Email> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Email>>() {
      @Override
      protected Page<Email> process() {
        return fullTextSearch
            ? emailSearchRepo.find(spec.getCriteria(), pageable, Email.class, match)
            : emailRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public List<Email> findAll() {
    return new BizTemplate<List<Email>>() {
      @Override
      protected List<Email> process() {
        return emailRepo.findAll();
      }
    }.execute();
  }

  @Override
  public List<Email> findByStatus(EmailStatus status) {
    return new BizTemplate<List<Email>>() {
      @Override
      protected List<Email> process() {
        return emailRepo.findByStatus(status);
      }
    }.execute();
  }

  @Override
  public List<Email> findByType(EmailType type) {
    return new BizTemplate<List<Email>>() {
      @Override
      protected List<Email> process() {
        return emailRepo.findByType(type);
      }
    }.execute();
  }

  @Override
  public EmailStatsVo getStatistics() {
    return new BizTemplate<EmailStatsVo>() {
      @Override
      protected EmailStatsVo process() {
        EmailStatsVo vo = new EmailStatsVo();

        // 总发送数
        long total = emailRepo.count();
        vo.setTotalSent(total);

        // 成功数量
        long successCount = emailRepo.countByStatus(EmailStatus.SENT);
        vo.setSuccessCount(successCount);

        // 失败数量
        long failedCount = emailRepo.countByStatus(EmailStatus.FAILED);
        vo.setFailedCount(failedCount);

        // 今日发送数（使用SQL COUNT查询，性能更好）
        LocalDateTime todayStart = LocalDateTime.now()
            .withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime todayEnd = LocalDateTime.now()
            .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        long todaySent = emailRepo.countByStatusAndSendTimeBetween(
            EmailStatus.SENT, todayStart, todayEnd);
        vo.setTodaySent(todaySent);

        // 本月发送数（使用SQL COUNT查询，性能更好）
        LocalDateTime firstDayOfMonth = LocalDateTime.now()
            .with(TemporalAdjusters.firstDayOfMonth())
            .withHour(0).withMinute(0).withSecond(0).withNano(0);
        long thisMonthSent = emailRepo.countByStatusAndSendTimeBetween(
            EmailStatus.SENT, firstDayOfMonth, todayEnd);
        vo.setThisMonthSent(thisMonthSent);

        // 计算打开率和点击率（只统计已发送的邮件）
        List<Email> sentEmails = emailRepo.findByStatus(EmailStatus.SENT);
        if (!sentEmails.isEmpty()) {
          Set<Long> emailIds = sentEmails.stream()
              .map(Email::getId)
              .collect(Collectors.toSet());

          // 批量查询跟踪记录（一次SQL查询，性能更好）
          List<EmailTracking> trackings = emailTrackingRepo.findByEmailIdIn(
              emailIds.stream().collect(Collectors.toList()));
          Map<Long, EmailTracking> trackingMap = trackings.stream()
              .collect(Collectors.toMap(EmailTracking::getEmailId, t -> t));

          // 统计打开和点击数量
          long openedCount = 0;
          long clickedCount = 0;
          for (Email email : sentEmails) {
            EmailTracking tracking = trackingMap.get(email.getId());
            if (tracking != null) {
              if (Boolean.TRUE.equals(tracking.getOpened())) {
                openedCount++;
              }
              if (Boolean.TRUE.equals(tracking.getClicked())) {
                clickedCount++;
              }
            }
          }

          // 计算打开率和点击率（百分比，保留2位小数）
          long totalSent = sentEmails.size();
          if (totalSent > 0) {
            double openRate = openedCount * 100.0 / totalSent;
            double clickRate = clickedCount * 100.0 / totalSent;
            vo.setOpenRate(Math.round(openRate * 100.0) / 100.0);
            vo.setClickRate(Math.round(clickRate * 100.0) / 100.0);
          } else {
            vo.setOpenRate(0.0);
            vo.setClickRate(0.0);
          }
        } else {
          vo.setOpenRate(0.0);
          vo.setClickRate(0.0);
        }

        return vo;
      }
    }.execute();
  }
}
