package cloud.xcan.angus.core.gm.interfaces.log.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.gm.application.query.log.UserOperationLogQuery;
import cloud.xcan.angus.core.gm.domain.log.UserOperationLog;
import cloud.xcan.angus.core.gm.interfaces.log.facade.UserOperationLogFacade;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.UserOperationLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.UserOperationLogStatisticsDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.internal.assembler.UserOperationLogAssembler;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.UserOperationLogDetailVo;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.UserOperationLogStatisticsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class UserOperationLogFacadeImpl implements UserOperationLogFacade {

  @Resource
  private UserOperationLogQuery userOperationLogQuery;

  @Override
  public UserOperationLogDetailVo getDetail(Long id) {
    UserOperationLog log = userOperationLogQuery.findAndCheck(id);
    return UserOperationLogAssembler.toDetailVo(log);
  }

  @Override
  public PageResult<UserOperationLogDetailVo> list(UserOperationLogFindDto dto) {
    GenericSpecification<UserOperationLog> spec = UserOperationLogAssembler.getSpecification(dto);
    Page<UserOperationLog> page = userOperationLogQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, UserOperationLogAssembler::toDetailVo);
  }

  @Override
  public UserOperationLogStatisticsVo getStatistics(UserOperationLogStatisticsDto dto) {
    return userOperationLogQuery.getStatistics(dto);
  }
}
