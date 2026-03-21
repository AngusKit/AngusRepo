package cloud.xcan.angus.core.gm.application.query.log.impl;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.log.UserOperationLogQuery;
import cloud.xcan.angus.core.gm.domain.log.UserOperationLog;
import cloud.xcan.angus.core.gm.domain.log.UserOperationLogRepo;
import cloud.xcan.angus.core.gm.domain.log.UserOperationLogSearchRepo;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.gm.domain.log.enums.ResponseStatus;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.UserOperationLogStatisticsDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.UserOperationLogStatisticsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户操作日志查询服务实现
 */
@Service
@Transactional(readOnly = true)
public class UserOperationLogQueryImpl implements UserOperationLogQuery {

  @Resource
  private UserOperationLogRepo userOperationLogRepo;

  @Resource
  private UserOperationLogSearchRepo userOperationLogSearchRepo;

  @Override
  public UserOperationLog findAndCheck(Long id) {
    return new BizTemplate<UserOperationLog>() {
      @Override
      protected UserOperationLog process() {
        return userOperationLogRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("用户操作日志「{0}」不存在", new Object[]{id}));
      }
    }.execute();
  }

  @Override
  public Page<UserOperationLog> find(GenericSpecification<UserOperationLog> spec,
      PageRequest pageable, boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<UserOperationLog>>() {
      @Override
      protected Page<UserOperationLog> process() {
        return fullTextSearch
            ? userOperationLogSearchRepo.find(
            spec.getCriteria(), pageable, UserOperationLog.class, match)
            : userOperationLogRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public UserOperationLogStatisticsVo getStatistics(UserOperationLogStatisticsDto dto) {
    return new BizTemplate<UserOperationLogStatisticsVo>() {
      @Override
      protected UserOperationLogStatisticsVo process() {
        UserOperationLogStatisticsVo stats = new UserOperationLogStatisticsVo();

        // 设置时间范围，如果没有指定则使用默认值
        LocalDateTime startDate = dto.getStartDate();
        LocalDateTime endDate = dto.getEndDate();
        if (endDate == null) {
          endDate = LocalDateTime.now();
        }
        if (startDate == null) {
          startDate = endDate.minusDays(30); // 默认查询最近30天
        }

        // 计算上周对应的时间范围（往前推7天）
        LocalDateTime lastWeekStartDate = startDate.minusDays(7);
        LocalDateTime lastWeekEndDate = endDate.minusDays(7);

        // 统计总操作次数（根据时间范围筛选）
        long totalCount = userOperationLogRepo.countByDateRange(startDate, endDate);
        stats.setTotalCount(totalCount);

        // 统计上周总操作次数
        long lastWeekTotalCount = userOperationLogRepo.countByDateRange(
            lastWeekStartDate, lastWeekEndDate);
        // 计算总操作次数较上周增长率
        if (lastWeekTotalCount > 0) {
          double totalCountGrowthRate = ((double) (totalCount - lastWeekTotalCount)
              / lastWeekTotalCount) * 100;
          stats.setTotalCountGrowthRate(Math.round(totalCountGrowthRate * 10.0) / 10.0);
        } else {
          stats.setTotalCountGrowthRate(totalCount > 0 ? 100.0 : 0.0);
        }

        // 统计成功操作次数
        long successCount = userOperationLogRepo.countByResponseStatusAndDateRange(
            startDate, endDate, ResponseStatus.SUCCESS);
        stats.setSuccessCount(successCount);

        // 统计上周成功操作次数
        long lastWeekSuccessCount = userOperationLogRepo.countByResponseStatusAndDateRange(
            lastWeekStartDate, lastWeekEndDate, ResponseStatus.SUCCESS);
        // 计算成功操作次数较上周增长率
        if (lastWeekSuccessCount > 0) {
          double successCountGrowthRate = ((double) (successCount - lastWeekSuccessCount)
              / lastWeekSuccessCount) * 100;
          stats.setSuccessCountGrowthRate(Math.round(successCountGrowthRate * 10.0) / 10.0);
        } else {
          stats.setSuccessCountGrowthRate(successCount > 0 ? 100.0 : 0.0);
        }

        // 统计失败操作次数
        long errorCount = userOperationLogRepo.countByResponseStatusAndDateRange(
            startDate, endDate, ResponseStatus.FAILURE);
        stats.setErrorCount(errorCount);

        // 统计上周失败操作次数
        long lastWeekErrorCount = userOperationLogRepo.countByResponseStatusAndDateRange(
            lastWeekStartDate, lastWeekEndDate, ResponseStatus.FAILURE);
        // 计算失败操作次数较上周增长率
        if (lastWeekErrorCount > 0) {
          double errorCountGrowthRate = ((double) (errorCount - lastWeekErrorCount)
              / lastWeekErrorCount) * 100;
          stats.setErrorCountGrowthRate(Math.round(errorCountGrowthRate * 10.0) / 10.0);
        } else {
          stats.setErrorCountGrowthRate(errorCount > 0 ? 100.0 : 0.0);
        }

        // 计算成功率
        double successRate = 0.0;
        if (totalCount > 0) {
          successRate = ((double) successCount / totalCount) * 100;
          stats.setSuccessRate(Math.round(successRate * 10.0) / 10.0);
        } else {
          stats.setSuccessRate(0.0);
        }

        // 计算上周成功率
        double lastWeekSuccessRate = 0.0;
        if (lastWeekTotalCount > 0) {
          lastWeekSuccessRate = ((double) lastWeekSuccessCount / lastWeekTotalCount) * 100;
        }
        // 计算成功率较上周增长率
        if (lastWeekSuccessRate > 0) {
          double successRateGrowthRate = ((successRate - lastWeekSuccessRate)
              / lastWeekSuccessRate) * 100;
          stats.setSuccessRateGrowthRate(Math.round(successRateGrowthRate * 10.0) / 10.0);
        } else {
          stats.setSuccessRateGrowthRate(successRate > 0 ? 100.0 : 0.0);
        }

        // 统计各操作类型的数量
        List<Object[]> actionStats = userOperationLogRepo.countByActionAndDateRange(
            startDate, endDate);
        Map<OperationAction, Long> actionStatistics = new HashMap<>();
        for (Object[] result : actionStats) {
          OperationAction action = (OperationAction) result[0];
          Long count = (Long) result[1];
          actionStatistics.put(action, count);
        }
        stats.setActionStatistics(actionStatistics);

        // 统计各资源类型的数量
        List<Object[]> resourceStats = userOperationLogRepo.countByResourceTypeAndDateRange(
            startDate, endDate);
        Map<ResourceType, Long> resourceStatistics = new HashMap<>();
        for (Object[] result : resourceStats) {
          ResourceType resourceType = (ResourceType) result[0];
          Long count = (Long) result[1];
          resourceStatistics.put(resourceType, count);
        }
        stats.setResourceStatistics(resourceStatistics);

        // 查询操作最频繁的用户TOP10
        List<Object[]> topUsersData = userOperationLogRepo.findTopUsersByOperationCount(
            startDate, endDate);
        List<UserOperationLogStatisticsVo.TopUserVo> topUsers = topUsersData.stream()
            .limit(10)
            .map(result -> {
              UserOperationLogStatisticsVo.TopUserVo topUser =
                  new UserOperationLogStatisticsVo.TopUserVo();
              topUser.setUserId((Long) result[0]);
              topUser.setUserName((String) result[1]);
              topUser.setOperationCount((Long) result[2]);
              return topUser;
            })
            .collect(Collectors.toList());
        stats.setTopUsers(topUsers);

        return stats;
      }
    }.execute();
  }

}
