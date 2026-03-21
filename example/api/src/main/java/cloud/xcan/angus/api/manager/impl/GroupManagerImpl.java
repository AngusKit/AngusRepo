package cloud.xcan.angus.api.manager.impl;

import static cloud.xcan.angus.api.manager.ManagerMessage.GROUP_IS_DISABLED_CODE;
import static cloud.xcan.angus.api.manager.ManagerMessage.GROUP_IS_DISABLED_T;
import static cloud.xcan.angus.core.biz.BizAssert.assertTrue;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.group.GroupRepo;
import cloud.xcan.angus.api.manager.GroupManager;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public class GroupManagerImpl implements GroupManager {

  @Resource
  @Qualifier("commonGroupRepo")
  private GroupRepo groupRepo;

  @Override
  public List<Group> find(Collection<Long> ids) {
    return groupRepo.findAllById(ids);
  }

  @Override
  public Page<Group> findAll(@Nullable Specification<Group> spec, Pageable pageable) {
    return groupRepo.findAll(spec, pageable);
  }

  @Override
  public Group findAndCheck(Long id) {
    return groupRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "Group"));
  }

  @Override
  public List<Group> findAndCheck(Collection<Long> ids) {
    List<Group> groups = groupRepo.findAllById(ids);
    assertResourceNotFound(isNotEmpty(groups), ids.iterator().next(), "Group");

    if (ids.size() != groups.size()) {
      for (Group group : groups) {
        assertResourceNotFound(ids.contains(group.getId()), group.getId(), "Group");
      }
    }
    return groups;
  }

  @Override
  public void checkExists(Collection<Long> ids) {
    List<Long> groupIdsDb = groupRepo.findIdsByIdIn(ids);
    assertResourceNotFound(isNotEmpty(groupIdsDb), ids.iterator().next(), "Group");

    if (ids.size() != groupIdsDb.size()) {
      for (Long groupId : groupIdsDb) {
        assertResourceNotFound(ids.contains(groupId), groupId, "Group");
      }
    }
  }

  @Override
  public Group checkValid(Long id) {
    Group groupDb = groupRepo.findById(id).orElseThrow(() -> ResourceNotFound.of(id, "Group"));
    assertTrue(groupDb.getStatus().isEnabled(), GROUP_IS_DISABLED_CODE, GROUP_IS_DISABLED_T,
        new Object[]{groupDb.getName()});
    return groupDb;
  }

  @Override
  public List<Group> checkValid(Collection<Long> ids) {
    List<Group> groups = groupRepo.findAllById(ids);
    assertResourceNotFound(isNotEmpty(groups), ids.iterator().next(), "Group");

    if (ids.size() != groups.size()) {
      for (Group group : groups) {
        assertResourceNotFound(ids.contains(group.getId()), group.getId(), "Group");
      }
    }

    for (Group group : groups) {
      assertTrue(group.getStatus().isEnabled(), GROUP_IS_DISABLED_CODE, GROUP_IS_DISABLED_T
          , new Object[]{group.getName()});
    }
    return groups;
  }

  @Override
  public List<Group> findByTenantId(Long tenantId) {
    return groupRepo.findAllByTenantId(tenantId);
  }

}
