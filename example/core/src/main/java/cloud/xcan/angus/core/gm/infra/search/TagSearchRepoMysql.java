package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.tag.Tag;
import cloud.xcan.angus.core.gm.domain.tag.TagSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public class TagSearchRepoMysql extends SimpleSearchRepository<Tag>
    implements TagSearchRepo {

}
