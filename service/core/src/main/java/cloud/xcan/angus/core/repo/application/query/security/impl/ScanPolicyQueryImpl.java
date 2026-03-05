package cloud.xcan.angus.core.repo.application.query.security.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.repo.application.query.security.ScanPolicyQuery;
import cloud.xcan.angus.core.repo.domain.security.ScanPolicy;
import cloud.xcan.angus.core.repo.domain.security.ScanPolicyListRepo;
import cloud.xcan.angus.core.repo.domain.security.ScanPolicyRepo;
import cloud.xcan.angus.core.repo.domain.security.ScanPolicySearchRepo;
import jakarta.annotation.Resource;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Biz
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
    return scanPolicyRepo.findById(id);
  }

  @Override
  public ScanPolicy findAndCheck(String id) {
    return scanPolicyRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("扫描策略不存在: " + id));
  }
}
