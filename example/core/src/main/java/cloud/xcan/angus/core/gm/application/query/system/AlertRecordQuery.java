package cloud.xcan.angus.core.gm.application.query.system;

import cloud.xcan.angus.core.gm.domain.system.AlertRecord;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 告警记录查询服务接口
 */
public interface AlertRecordQuery {

  /**
   * 根据ID查询告警记录（带校验）
   */
  AlertRecord findAndCheck(Long id);

  /**
   * 通用分页查询
   */
  Page<AlertRecord> list(GenericSpecification<AlertRecord> spec, Pageable pageable,
      boolean fullTextSearch, String[] match);

}
