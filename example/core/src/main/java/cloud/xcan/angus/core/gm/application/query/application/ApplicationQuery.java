package cloud.xcan.angus.core.gm.application.query.application;

import cloud.xcan.angus.api.commonlink.application.Application;
import cloud.xcan.angus.api.commonlink.application.enums.ApplicationSource;
import cloud.xcan.angus.core.gm.interfaces.application.facade.vo.ApplicationStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ApplicationQuery {

  /**
   * 根据ID查找应用并检查存在性
   */
  Application findAndCheck(Long id);

  /**
   * 分页查询应用
   */
  Page<Application> find(GenericSpecification<Application> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  /**
   * 获取日志保留配置列表
   */
  List<Application> findAll();

  /**
   * 获取应用统计数据
   */
  ApplicationStatsVo getStats();

  /**
   * 根据编码和版本类型查找应用
   */
  Optional<Application> findByCodeAndEditionType(String code, String editionType);

  /**
   * 根据编码查找应用
   */
  Optional<Application> findByCode(String code);

  /**
   * 校验应用是否可以改变
   */
  Application checkCanModify(Long id);

  /**
   * 校验应用是否可以改变
   */
  Application checkCanModify(Application application);

  /**
   * 根据ID列表查找应用列表
   */
  List<Application> findAllById(Collection<Long> ids);

  /**
   * 根据应用来源查找应用列表
   */
  List<Application> findAllByType(ApplicationSource source);

  /**
   * 根据ID查找应用
   */
  Application findById(Long id);

}
