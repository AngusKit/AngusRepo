package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.gm.application.cmd.interfaces.InterfaceRequestLogCmd;
import cloud.xcan.angus.core.gm.application.query.interfaces.InterfaceRequestLogQuery;
import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRequestLog;
import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRequestLogInfo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.InterfaceRequestLogFacade;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceRequestLogCreateDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceRequestLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceRequestLogStatisticsDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.internal.assembler.InterfaceRequestLogAssembler;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceRequestLogDetailVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceRequestLogListVo;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.InterfaceRequestLogStatisticsVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class InterfaceRequestLogFacadeImpl implements InterfaceRequestLogFacade {

  @Resource
  private InterfaceRequestLogCmd interfaceRequestLogCmd;

  @Resource
  private InterfaceRequestLogQuery interfaceRequestLogQuery;

  @Override
  public void batchCreate(List<InterfaceRequestLogCreateDto> dto) {
    List<InterfaceRequestLog> logs = InterfaceRequestLogAssembler.toCreateDomainList(dto);
    interfaceRequestLogCmd.batchCreate(logs);
  }

  @Override
  public InterfaceRequestLogDetailVo getDetail(Long id) {
    InterfaceRequestLog log = interfaceRequestLogQuery.findAndCheck(id);
    return InterfaceRequestLogAssembler.toDetailVo(log);
  }

  @Override
  public PageResult<InterfaceRequestLogListVo> list(InterfaceRequestLogFindDto dto) {
    Page<InterfaceRequestLogInfo> page = interfaceRequestLogQuery.find(
        InterfaceRequestLogAssembler.getSpecification(dto), dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, InterfaceRequestLogAssembler::toListVo);
  }

  @Override
  public InterfaceRequestLogStatisticsVo getStatistics(InterfaceRequestLogStatisticsDto dto) {
    return interfaceRequestLogQuery.getStatistics(dto);
  }
}
