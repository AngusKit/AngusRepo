package cloud.xcan.angus.core.gm.application.cmd.system.impl;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.system.SystemVersionCmd;
import cloud.xcan.angus.core.gm.application.query.system.SystemVersionQuery;
import cloud.xcan.angus.core.gm.domain.system.SystemVersionRepo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SystemVersionCmdImpl implements SystemVersionCmd {

  @Resource
  private SystemVersionRepo systemVersionRepo;

  @Resource
  private SystemVersionQuery systemVersionQuery;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void syncVersions() {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        // Add validation if needed
      }

      @Override
      protected Void process() {
        // TODO 从ESS应用版本库同步版本信息
        return null;
      }
    }.execute();
  }

}
