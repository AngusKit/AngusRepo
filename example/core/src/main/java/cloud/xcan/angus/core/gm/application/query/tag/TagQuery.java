package cloud.xcan.angus.core.gm.application.query.tag;

import cloud.xcan.angus.core.gm.domain.tag.Tag;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagStatisticsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface TagQuery {

  Tag findAndCheck(Long id);

  Page<Tag> find(GenericSpecification<Tag> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  List<Tag> findAll();

  Tag findByName(String name);

  List<Tag> findByIds(List<Long> ids);

  TagStatisticsVo getStatistics();

  List<Tag> findByCategoryId(Long id);

}
