package cloud.xcan.angus.core.gm.application.query.sms.impl;

import cloud.xcan.angus.api.commonlink.sms.SmsStatus;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.sms.SmsQuery;
import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.core.gm.domain.sms.SmsRepo;
import cloud.xcan.angus.core.gm.domain.sms.SmsSearchRepo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class SmsQueryImpl implements SmsQuery {

  @Resource
  private SmsRepo smsRepo;

  @Resource
  private SmsSearchRepo smsSearchRepo;

  @Override
  public Page<Sms> findRecords(GenericSpecification<Sms> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Sms>>() {
      @Override
      protected Page<Sms> process() {
        // 根据是否全文搜索选择不同的仓储
        return fullTextSearch
            ? smsSearchRepo.find(spec.getCriteria(), pageable, Sms.class, match)
            : smsRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public SmsStatsVo getStats() {
    return new BizTemplate<SmsStatsVo>() {
      @Override
      protected SmsStatsVo process() {
        long totalSent = smsRepo.count();
        long successCount =
            smsRepo.countByStatus(SmsStatus.SENT) + smsRepo.countByStatus(SmsStatus.DELIVERED);
        long failedCount = smsRepo.countByStatus(SmsStatus.FAILED);

        // 今日发送数量
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(23, 59, 59);
        long todaySent =
            smsRepo.findByStatusAndSendTimeBetween(SmsStatus.SENT, todayStart, todayEnd).size()
                + smsRepo.findByStatusAndSendTimeBetween(SmsStatus.DELIVERED, todayStart, todayEnd)
                .size();

        // 本月发送数量
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        long thisMonthSent =
            smsRepo.findByStatusAndSendTimeBetween(SmsStatus.SENT, monthStart, todayEnd).size()
                + smsRepo.findByStatusAndSendTimeBetween(SmsStatus.DELIVERED, monthStart, todayEnd)
                .size();

        SmsStatsVo vo = new SmsStatsVo();
        vo.setTotalSent(totalSent);
        vo.setSuccessCount(successCount);
        vo.setFailedCount(failedCount);
        vo.setTodaySent(todaySent);
        vo.setThisMonthSent(thisMonthSent);
        return vo;
      }
    }.execute();
  }

}
