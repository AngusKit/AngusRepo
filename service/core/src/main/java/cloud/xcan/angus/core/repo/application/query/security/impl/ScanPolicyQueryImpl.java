package cloud.xcan.angus.core.repo.application.query.security.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.repo.application.query.security.ScanPolicyQuery;
import cloud.xcan.angus.core.repo.domain.security.ScanPolicy;
import cloud.xcan.angus.core.repo.domain.security.ScanPolicyListRepo;
import cloud.xcan.angus.core.repo.domain.security.ScanPolicyRepo;
import cloud.xcan.angus.core.repo.domain.security.ScanPolicySearchRepo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@Biz
@Transactional(readOnly = true)
public class ScanPolicyQueryImpl implements ScanPolicyQuery {

  @Resource
  private ScanPolicyRepo scanPolicyRepo;

  @Resource
  private ScanPolicyListRepo scanPolicyListRepo;

  @Resource
  private ScanPolicySearchRepo scanPolicySearchRepo;

  @Override
  public Page<ScanPolicy> find(GenericSpecification<ScanPolicy> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<ScanPolicy>>() {
      @Override
      protected Page<ScanPolicy> process() {
        return fullTextSearch
            ? scanPolicySearchRepo.find(spec.getCriteria(), pageable, ScanPolicy.class, match)
            : scanPolicyListRepo.find(spec.getCriteria(), pageable, ScanPolicy.class, null);
      }
    }.execute();
  }

  @Override
  public Optional<ScanPolicy> findById(String id) {
    return new BizTemplate<Optional<ScanPolicy>>() {
      @Override
      protected Optional<ScanPolicy> process() {
        return scanPolicyRepo.findById(id);
      }
    }.execute();
  }

  @Override
  public ScanPolicy findAndCheck(String id) {
    return new BizTemplate<ScanPolicy>() {
      @Override
      protected ScanPolicy process() {
        return scanPolicyRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of(id, "ScanPolicy"));
      }
    }.execute();
  }
}
