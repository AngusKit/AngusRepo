package cloud.xcan.angus.core.repo.interfaces.activitylog.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.repo.interfaces.activitylog.facade.internal.assembler.ActivityLogAssembler.getSpecification;
import static cloud.xcan.angus.core.repo.interfaces.activitylog.facade.internal.assembler.ActivityLogAssembler.toEntity;
import static cloud.xcan.angus.core.repo.interfaces.activitylog.facade.internal.assembler.ActivityLogAssembler.toVo;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.repo.application.cmd.activitylog.ActivityLogCmd;
import cloud.xcan.angus.core.repo.application.query.activitylog.ActivityLogQuery;
import cloud.xcan.angus.core.repo.domain.activitylog.ActivityLog;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.ActivityLogFacade;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.dto.ActivityLogBatchDeleteDto;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.dto.ActivityLogCreateDto;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.dto.ActivityLogExportDto;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.dto.ActivityLogFindDto;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.internal.assembler.ActivityLogAssembler;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.vo.ActivityLogStatisticsVo;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.vo.ActivityLogVo;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.vo.ActivityUserListVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 * 活动日志Facade实现
 */
@Slf4j
@Component
public class ActivityLogFacadeImpl implements ActivityLogFacade {

  @Resource
  private ActivityLogCmd activityLogCmd;

  @Resource
  private ActivityLogQuery activityLogQuery;

  @Override
  public ActivityLogVo create(ActivityLogCreateDto dto) {
    ActivityLog activityLog = toEntity(dto);
    ActivityLog created = activityLogCmd.create(activityLog);
    return toVo(created);
  }

  @Override
  public void delete(String id) {
    activityLogCmd.delete(id);
  }

  @Override
  public void deleteBatch(ActivityLogBatchDeleteDto dto) {
    if (dto.getIds() != null && !dto.getIds().isEmpty()) {
      activityLogCmd.deleteBatch(dto.getIds());
    } else {
      activityLogCmd.deleteByCondition(dto.getBeforeDate(),
          dto.getCategory() != null ? dto.getCategory().name() : null);
    }
  }

  @Override
  public ActivityLogVo getById(String id) {
    ActivityLog activityLog = activityLogQuery.findById(id)
        .orElseThrow(() -> new RuntimeException("Activity log not found: " + id));
    return toVo(activityLog);
  }

  @Override
  public PageResult<ActivityLogVo> list(ActivityLogFindDto dto) {
    Page<ActivityLog> page = activityLogQuery.find(
        getSpecification(dto),
        dto.tranPage(),
        dto.fullTextSearch,
        getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, ActivityLogAssembler::toVo);
  }

  @Override
  public ActivityLogStatisticsVo getStatistics(ActivityLogFindDto dto) {
    return activityLogQuery.getStatistics(dto.getStartDate(), dto.getEndDate());
  }

  @Override
  public void export(ActivityLogExportDto dto, HttpServletResponse response) throws IOException {
    // 构建查询条件
    ActivityLogFindDto findDto = new ActivityLogFindDto();
    findDto.setAction(dto.getAction());
    findDto.setUser(dto.getUser());
    findDto.setRepository(dto.getRepository());
    findDto.setStartDate(dto.getStartDate());
    findDto.setEndDate(dto.getEndDate());

    // 查询所有匹配的记录
    List<ActivityLog> logs = activityLogQuery.findForExport(getSpecification(findDto));

    // 设置响应头
    String format = dto.getFormat() != null ? dto.getFormat().toLowerCase() : "csv";
    String fileName = "activity-logs-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "."
        + format;
    String contentType = "csv".equals(format) ? "text/csv" :
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    response.setContentType(contentType);
    response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

    // 导出CSV格式
    if ("csv".equals(format)) {
      exportCsv(logs, response);
    } else {
      // Excel格式需要引入 Apache POI 或 EasyExcel 库支持，当前降级为 CSV 导出
      log.warn("Excel export not yet supported, falling back to CSV format");
      exportCsv(logs, response);
    }
  }

  /**
   * 导出CSV格式
   */
  private void exportCsv(List<ActivityLog> logs, HttpServletResponse response) throws IOException {
    PrintWriter writer = response.getWriter();
    // 写入CSV头部
    writer.println("ID,操作类型,用户,操作对象,仓库,时间戳,IP地址,User Agent,详细信息,分类");
    // 写入数据
    for (ActivityLog log : logs) {
      writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
          log.getId(),
          log.getAction() != null ? log.getAction().name() : "",
          escapeCsv(log.getUser()),
          escapeCsv(log.getArtifact()),
          escapeCsv(log.getRepository()),
          log.getTimestamp() != null ? log.getTimestamp().toString() : "",
          escapeCsv(log.getIpAddress()),
          escapeCsv(log.getUserAgent()),
          escapeCsv(log.getDetails()),
          log.getCategory() != null ? log.getCategory().name() : "");
    }
    writer.flush();
  }

  /**
   * CSV字段转义
   */
  private String escapeCsv(String value) {
    if (value == null) {
      return "";
    }
    // 如果包含逗号、引号或换行符，需要用引号包裹并转义引号
    if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
      return "\"" + value.replace("\"", "\"\"") + "\"";
    }
    return value;
  }

  @Override
  public ActivityUserListVo getUniqueUsers() {
    return activityLogQuery.getUniqueUsers();
  }
}
