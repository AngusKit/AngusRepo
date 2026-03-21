package cloud.xcan.angus.core.gm.infra.search;

import cloud.xcan.angus.core.gm.domain.interfaces.Interface;
import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceSearchRepo;
import cloud.xcan.angus.core.jpa.repository.SimpleSearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public class InterfaceSearchRepoMysql extends SimpleSearchRepository<Interface>
    implements InterfaceSearchRepo {

}
