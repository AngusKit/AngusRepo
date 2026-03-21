package cloud.xcan.angus.core.gm.application.query.interfaces.impl;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.interfaces.InterfaceQuery;
import cloud.xcan.angus.core.gm.domain.interfaces.Interface;
import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRepo;
import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceSearchRepo;
import cloud.xcan.angus.core.gm.domain.interfaces.TagCount;
import cloud.xcan.angus.core.gm.domain.interfaces.enums.InterfaceSyncAction;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.remote.search.SearchCriteria;
import cloud.xcan.angus.remote.search.SearchOperation;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class InterfaceQueryImpl implements InterfaceQuery {

  @Resource
  private InterfaceRepo interfaceRepo;

  @Resource
  private InterfaceSearchRepo interfaceSearchRepo;

  @Override
  public Interface findAndCheck(Long id) {
    return new BizTemplate<Interface>() {
      @Override
      protected Interface process() {
        return interfaceRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("接口「{0}」不存在", new Object[]{id}));
      }
    }.execute();
  }

  @Override
  public Page<Interface> find(GenericSpecification<Interface> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Interface>>() {
      @Override
      protected Page<Interface> process() {
        return fullTextSearch
            ? interfaceSearchRepo.find(spec.getCriteria(), pageable, Interface.class, match)
            : interfaceRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public Page<Interface> findByServiceName(String serviceName, GenericSpecification<Interface> spec,
      PageRequest pageable, boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Interface>>() {
      @Override
      protected Page<Interface> process() {
        // Add serviceId filter to specification
        Set<SearchCriteria> filters = new HashSet<>(spec.getCriteria());
        filters.add(new SearchCriteria("serviceName", serviceName, SearchOperation.EQUAL));
        GenericSpecification<Interface> finalSpec = new GenericSpecification<>(filters);

        // Use unified find method
        return find(finalSpec, pageable, fullTextSearch, match);
      }
    }.execute();
  }

  @Override
  public Page<Interface> findByTag(String tag, GenericSpecification<Interface> spec,
      PageRequest pageable, boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Interface>>() {
      @Override
      protected Page<Interface> process() {
        Set<SearchCriteria> filters = new HashSet<>(spec.getCriteria());
        filters.add(new SearchCriteria("tag", tag, SearchOperation.EQUAL));
        GenericSpecification<Interface> finalSpec = new GenericSpecification<>(filters);
        return find(finalSpec, pageable, fullTextSearch, match);
      }
    }.execute();
  }

  @Override
  public long countTotal() {
    return interfaceRepo.count();
  }

  @Override
  public long countByStatus(EnabledStatus status) {
    return interfaceRepo.countByStatus(status);
  }

  @Override
  public long countByServiceName(String serviceName) {
    return interfaceRepo.countByServiceName(serviceName);
  }

  @Override
  public long countByServiceNameAndDeprecated(String serviceName, Boolean deprecated) {
    return interfaceRepo.countByServiceNameAndDeprecated(serviceName, deprecated);
  }

  @Override
  public long countByServiceNameAndLastSyncAction(String serviceName,
      InterfaceSyncAction lastSyncAction) {
    return interfaceRepo.countByServiceNameAndLastSyncAction(serviceName, lastSyncAction);
  }

  @Override
  public List<String> findDistinctServiceNames() {
    return new BizTemplate<List<String>>() {
      @Override
      protected List<String> process() {
        return interfaceRepo.findDistinctServiceNames();
      }
    }.execute();
  }

  @Override
  public List<TagCount> countGroupByTag() {
    return new BizTemplate<List<TagCount>>() {
      @Override
      protected List<TagCount> process() {
        return interfaceRepo.countGroupByTag();
      }
    }.execute();
  }

}
