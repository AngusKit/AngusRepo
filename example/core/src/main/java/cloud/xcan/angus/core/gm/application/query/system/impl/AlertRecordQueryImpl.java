package cloud.xcan.angus.core.gm.application.query.system.impl;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.system.AlertRecordQuery;
import cloud.xcan.angus.core.gm.domain.system.AlertRecord;
import cloud.xcan.angus.core.gm.domain.system.AlertRecordRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 告警记录查询服务实现
 */
@Service
public class AlertRecordQueryImpl implements AlertRecordQuery {

  @Resource
  private AlertRecordRepo alertRecordRepo;

  @Override
  public AlertRecord findAndCheck(Long id) {
    return new BizTemplate<AlertRecord>() {
      @Override
      protected AlertRecord process() {
        return alertRecordRepo.findById(id).orElseThrow(
            () -> ResourceNotFound.of("告警记录「{0}」不存在", new Object[]{id}));
      }
    }.execute();
  }

  @Override
  public Page<AlertRecord> list(GenericSpecification<AlertRecord> spec, Pageable pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<AlertRecord>>() {
      @Override
      protected Page<AlertRecord> process() {
        // 告警记录暂不支持全文搜索
        return alertRecordRepo.findAll(spec, pageable);
      }
    }.execute();
  }

}
