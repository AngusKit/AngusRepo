package cloud.xcan.angus.core.gm.interfaces.application.facade.internal;

import cloud.xcan.angus.api.commonlink.application.ApplicationMenu;
import cloud.xcan.angus.core.gm.application.cmd.application.ApplicationMenuCmd;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationMenuQuery;
import cloud.xcan.angus.core.gm.interfaces.application.facade.ApplicationMenuFacade;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationMenuCreateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.dto.ApplicationMenuUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.application.facade.internal.assembler.ApplicationMenuAssembler;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationMenuVo;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMenuFacadeImpl implements ApplicationMenuFacade {

  @Resource
  private ApplicationMenuCmd applicationMenuCmd;

  @Resource
  private ApplicationMenuQuery applicationMenuQuery;

  @Override
  public ApplicationMenuVo createMenu(Long appId, ApplicationMenuCreateDto dto) {
    ApplicationMenu menu = ApplicationMenuAssembler.toDomain(appId, dto);
    ApplicationMenu saved = applicationMenuCmd.create(menu);
    return ApplicationMenuAssembler.toVo(saved);
  }

  @Override
  public ApplicationMenuVo updateMenu(Long appId, Long menuId, ApplicationMenuUpdateDto dto) {
    ApplicationMenu menu = ApplicationMenuAssembler.toDomain(appId, menuId, dto);
    ApplicationMenu saved = applicationMenuCmd.update(menu);
    return ApplicationMenuAssembler.toVo(saved);
  }

  @Override
  public void deleteMenu(Long appId, Long menuId) {
    applicationMenuCmd.delete(appId, menuId);
  }

  @Override
  public List<ApplicationMenuVo> getMenus(Long appId) {
    List<ApplicationMenu> menus = applicationMenuQuery.findByAppId(appId);
    return ApplicationMenuAssembler.buildMenuTree(menus);
  }
}
