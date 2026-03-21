package cloud.xcan.angus.core.gm.infra.job;

import cloud.xcan.angus.api.commonlink.email.EmailStatus;
import cloud.xcan.angus.core.gm.application.query.email.EmailQuery;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.infra.mail.EmailSendService;
import cloud.xcan.angus.core.job.JobTemplate;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 邮件发送任务 定时查询待发送状态的邮件并发送
 */
@Slf4j
@Component
public class EmailSendJob {

  private static final String LOCK_KEY = "git:job:EmailSendJob";
  private static final int BATCH_SIZE = 50; // 每次处理的邮件数量

  @Resource
  private EmailQuery emailQuery;

  @Resource
  private EmailSendService emailSendService;

  @Resource
  private JobTemplate jobTemplate;

  @Scheduled(fixedDelay = 30 * 1000, initialDelay = 5000)
  public void execute() {
    jobTemplate.execute(LOCK_KEY, 10, TimeUnit.MINUTES, () -> {
      // 1. 查询待发送状态的邮件
      List<Email> pendingEmails = emailQuery.findByStatus(EmailStatus.PENDING);
      if (pendingEmails.isEmpty()) {
        log.debug("没有待发送的邮件");
        return;
      }

      // 限制每次处理的数量
      int processCount = Math.min(pendingEmails.size(), BATCH_SIZE);
      List<Email> emailsToProcess = pendingEmails.subList(0, processCount);

      log.info("开始处理待发送邮件，本次处理 {} 条", emailsToProcess.size());

      // 2. 批量发送邮件
      int successCount = 0;
      int failedCount = 0;
      for (Email email : emailsToProcess) {
        try {
          emailSendService.sendEmail(email);
          successCount++;
        } catch (Exception e) {
          log.error("发送邮件失败: emailId={}", email.getId(), e);
          failedCount++;
        }
      }

      log.info("邮件发送任务完成，成功: {}, 失败: {}", successCount, failedCount);
    });
  }
}
