package cloud.xcan.angus.core.repo.interfaces.notification.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.repo.interfaces.notification.facade.internal.assembler.NotificationAssembler.getSpecification;
import static cloud.xcan.angus.core.repo.interfaces.notification.facade.internal.assembler.NotificationAssembler.toCreateEntity;
import static cloud.xcan.angus.core.repo.interfaces.notification.facade.internal.assembler.NotificationAssembler.toDetailVo;
import static cloud.xcan.angus.core.repo.interfaces.notification.facade.internal.assembler.NotificationAssembler.toUpdateEntity;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.repo.application.cmd.notification.NotificationCmd;
import cloud.xcan.angus.core.repo.application.query.notification.NotificationQuery;
import cloud.xcan.angus.core.repo.domain.notification.Notification;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.NotificationFacade;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.dto.NotificationBatchDeleteDto;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.dto.NotificationBatchReadDto;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.dto.NotificationCreateDto;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.dto.NotificationFindDto;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.dto.NotificationUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.internal.assembler.NotificationAssembler;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.vo.NotificationDetailVo;
import cloud.xcan.angus.core.repo.interfaces.notification.facade.vo.NotificationStatisticsVo;
import cloud.xcan.angus.remote.NameJoin;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class NotificationFacadeImpl implements NotificationFacade {

  @Resource
  private NotificationCmd notificationCmd;

  @Resource
  private NotificationQuery notificationQuery;

  @Override
  @NameJoin
  public NotificationDetailVo create(NotificationCreateDto dto) {
    Notification entity = toCreateEntity(dto);
    Notification created = notificationCmd.create(entity);
    return toDetailVo(created);
  }

  @Override
  @NameJoin
  public NotificationDetailVo update(String id, NotificationUpdateDto dto) {
    Notification entity = toUpdateEntity(dto, id);
    Notification updated = notificationCmd.update(entity);
    return toDetailVo(updated);
  }

  @Override
  public void markAsRead(String id) {
    notificationCmd.markAsRead(id);
  }

  @Override
  public void delete(String id) {
    notificationCmd.delete(id);
  }

  @Override
  @NameJoin
  public NotificationDetailVo getById(String id) {
    Notification entity = notificationQuery.findAndCheck(id);
    return toDetailVo(entity);
  }

  @Override
  @NameJoin
  public PageResult<NotificationDetailVo> list(NotificationFindDto dto) {
    Page<Notification> page = notificationQuery.find(
        getSpecification(dto),
        dto.tranPage(),
        dto.fullTextSearch,
        getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, NotificationAssembler::toDetailVo);
  }

  @Override
  public NotificationStatisticsVo getStatistics() {
    return notificationQuery.getStatistics();
  }

  @Override
  public void markBatchAsRead(NotificationBatchReadDto dto) {
    notificationCmd.markBatchAsRead(dto.getIds());
  }

  @Override
  public void deleteBatch(NotificationBatchDeleteDto dto) {
    notificationCmd.deleteBatch(dto.getIds());
  }

  @Override
  public void addStar(String id) {
    notificationCmd.addStar(id);
  }

  @Override
  public void removeStar(String id) {
    notificationCmd.removeStar(id);
  }

  @Override
  public void archive(String id) {
    notificationCmd.archive(id);
  }

  @Override
  public void unarchive(String id) {
    notificationCmd.unarchive(id);
  }
}
