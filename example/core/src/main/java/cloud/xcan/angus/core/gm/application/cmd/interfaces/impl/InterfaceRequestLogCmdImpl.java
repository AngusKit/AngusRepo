package cloud.xcan.angus.core.gm.application.cmd.interfaces.impl;

import static cloud.xcan.angus.core.gm.application.query.service.impl.ServiceConfigQueryImpl.getApplicationUpperCaseCodeByServiceName;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.interfaces.InterfaceRequestLogCmd;
import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRequestLog;
import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRequestLogRepo;
import cloud.xcan.angus.core.gm.infra.utils.RequestLogDesensitizer;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InterfaceRequestLogCmdImpl extends CommCmd<InterfaceRequestLog, Long>
    implements InterfaceRequestLogCmd {

  @Resource
  private InterfaceRequestLogRepo interfaceRequestLogRepo;

  @Resource
  private ApplicationInfo applicationInfo;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void batchCreate(List<InterfaceRequestLog> logs) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        for (InterfaceRequestLog log : logs) {
          String appCode = getApplicationUpperCaseCodeByServiceName(log.getServiceCode());
          log.setEditionType(applicationInfo.getEditionType());
          log.setApplicationCode(appCode);
          RequestLogDesensitizer.desensitize(log);
        }

        batchInsert0(logs);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<InterfaceRequestLog, Long> getRepository() {
    return interfaceRequestLogRepo;
  }
}
