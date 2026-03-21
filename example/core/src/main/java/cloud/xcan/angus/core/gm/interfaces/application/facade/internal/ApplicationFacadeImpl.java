package cloud.xcan.angus.core.gm.interfaces.application.facade.internal;

import static cloud.xcan.angus.core.gm.domain.CommonConstant.APPLICATION_TAG_CATEGORY_CODE;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.application.Application;
import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.application.ApplicationCmd;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationMenuQuery;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationQuery;
import cloud.xcan.angus.core.gm.application.query.authorization.AuthorizationQuery;
import cloud.xcan.angus.core.gm.application.query.role.RoleQuery;
import cloud.xcan.angus.core.gm.interfaces.application.facade.ApplicationFacade;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationFindDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.internal.assembler.ApplicationAssembler;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationListVo;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationStatsVo;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.TagCategoryFacade;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagListVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ApplicationFacadeImpl implements ApplicationFacade {

  @Resource
  private ApplicationCmd applicationCmd;

  @Resource
  private ApplicationQuery applicationQuery;

  @Resource
  private ApplicationMenuQuery applicationMenuQuery;

  @Resource
  private AuthorizationQuery authorizationQuery;

  @Resource
  private RoleQuery roleQuery;

  @Resource
  private TagCategoryFacade tagCategoryFacade;

  @NameJoin
  @Override
  public ApplicationDetailVo create(ApplicationCreateDto dto) {
    Application app = ApplicationAssembler.toDomain(dto);
    Application saved = applicationCmd.create(app);
    return ApplicationAssembler.toDetailVo(saved);
  }

  @NameJoin
  @Override
  public ApplicationDetailVo update(Long id, ApplicationUpdateDto dto) {
    Application app = ApplicationAssembler.toDomain(id, dto);
    Application saved = applicationCmd.update(app);
    // 设置应用统计
    assembleCountStats(saved);
    return ApplicationAssembler.toDetailVo(saved);
  }

  @Override
  public ApplicationDetailVo updateStatus(Long id, EnabledStatusUpdateDto dto) {
    Application app = applicationCmd.updateStatus(id, dto.getStatus());
    // 设置应用统计
    assembleCountStats(app);
    return ApplicationAssembler.toDetailVo(app);
  }

  @Override
  public void delete(Long id) {
    applicationCmd.delete(id);
  }

  @NameJoin
  @Override
  public ApplicationDetailVo getDetail(Long id) {
    Application app = applicationQuery.findAndCheck(id);
    // 设置应用统计
    assembleCountStats(app);
    return ApplicationAssembler.toDetailVo(app);
  }

  @NameJoin
  @Override
  public PageResult<ApplicationListVo> find(ApplicationFindDto dto) {
    Page<Application> page = applicationQuery.find(ApplicationAssembler.getSpecification(dto),
        dto.tranPage(), dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, ApplicationAssembler::toListVo);
  }

  @Override
  public ApplicationStatsVo getStats() {
    return applicationQuery.getStats();
  }

  @Override
  public List<TagListVo> getAvailableTags() {
    return tagCategoryFacade.getTagListByCategoryCode(APPLICATION_TAG_CATEGORY_CODE);
  }

  private void assembleCountStats(Application saved) {
    long menuCount = applicationMenuQuery.countByApplicationId(saved.getId());
    saved.setMenuCount((int) menuCount);
    long roleCount = roleQuery.countRolesByApplicationId(saved.getId());
    saved.setRoleCount((int) roleCount);
    long userCount = authorizationQuery.countWideUsersByApplicationId(saved.getId());
    saved.setUserCount((int) userCount);
  }
}
