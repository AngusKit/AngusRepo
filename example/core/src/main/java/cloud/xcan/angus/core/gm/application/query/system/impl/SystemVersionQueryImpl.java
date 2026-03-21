package cloud.xcan.angus.core.gm.application.query.system.impl;

import static cloud.xcan.angus.api.commonlink.GMConstant.GM_APP_CODE;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.system.SystemVersionQuery;
import cloud.xcan.angus.core.gm.domain.system.SystemVersion;
import cloud.xcan.angus.core.gm.domain.system.SystemVersionRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SystemVersionQueryImpl implements SystemVersionQuery {

  @Resource
  private SystemVersionRepo systemVersionRepo;

  @Resource
  private ApplicationInfo applicationInfo;

  @Override
  public Optional<SystemVersion> findById(Long id) {
    return new BizTemplate<Optional<SystemVersion>>() {
      @Override
      protected Optional<SystemVersion> process() {
        return systemVersionRepo.findById(id);
      }
    }.execute();
  }

  @Override
  public Optional<SystemVersion> findCurrent() {
    return new BizTemplate<Optional<SystemVersion>>() {
      @Override
      protected Optional<SystemVersion> process() {
        return systemVersionRepo.findByAppCodeAndVersionAndEditionType(GM_APP_CODE,
            applicationInfo.getVersion(), EditionType.valueOf(applicationInfo.getEditionType()));
      }
    }.execute();
  }

  @Override
  public Optional<SystemVersion> findVersion(String appCode, String fromVersion) {
    return new BizTemplate<Optional<SystemVersion>>() {
      @Override
      protected Optional<SystemVersion> process() {
        return systemVersionRepo.findByAppCodeAndVersionAndEditionType(appCode, fromVersion,
            EditionType.valueOf(applicationInfo.getEditionType()));
      }
    }.execute();
  }

  @Override
  public Page<SystemVersion> findVersions(GenericSpecification<SystemVersion> spec,
      PageRequest pageable, boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<SystemVersion>>() {
      @Override
      protected Page<SystemVersion> process() {
        return systemVersionRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public List<SystemVersion> findVersion(String appCode, EditionType editionType) {
    return new BizTemplate<List<SystemVersion>>() {
      @Override
      protected List<SystemVersion> process() {
        return systemVersionRepo.findTopByAppCodeAndEditionType(appCode, editionType);
      }
    }.execute();
  }

  @Override
  public Optional<SystemVersion> findLatestVersion(String appCode, EditionType editionType) {
    return new BizTemplate<Optional<SystemVersion>>() {
      @Override
      protected Optional<SystemVersion> process() {
        return systemVersionRepo.findTop1ByAppCodeAndEditionTypeOrderByReleaseDateDesc(appCode,
            editionType);
      }
    }.execute();
  }
}
