package cloud.xcan.angus.core.gm.interfaces.log.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.log.SystemLogCmd;
import cloud.xcan.angus.core.gm.application.query.log.SystemLogQuery;
import cloud.xcan.angus.core.gm.domain.log.SystemLog;
import cloud.xcan.angus.core.gm.infra.utils.DownloadResult;
import cloud.xcan.angus.core.gm.interfaces.log.facade.SystemLogFacade;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.SystemLogBatchDeleteDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.SystemLogContentDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.SystemLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.SystemLogStatisticsDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.internal.assembler.SystemLogAssembler;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.SystemLogContentVo;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.SystemLogDetailVo;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.SystemLogStatisticsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.message.ProtocolException;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class SystemLogFacadeImpl implements SystemLogFacade {

  @Resource
  private SystemLogQuery systemLogQuery;

  @Resource
  private SystemLogCmd systemLogCmd;

  @NameJoin
  @Override
  public SystemLogDetailVo getDetail(Long id) {
    SystemLog log = systemLogQuery.findAndCheck(id);
    return SystemLogAssembler.toDetailVo(log);
  }

  @NameJoin
  @Override
  public PageResult<SystemLogDetailVo> list(SystemLogFindDto dto) {
    GenericSpecification<SystemLog> spec = SystemLogAssembler.getSpecification(dto);
    Page<SystemLog> page = systemLogQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, SystemLogAssembler::toDetailVo);
  }

  @Override
  public SystemLogContentVo getContent(Long id, SystemLogContentDto dto) {
    return systemLogQuery.getContent(id, dto);
  }

  @Override
  public DownloadResult download(Long id) throws IOException {
    SystemLog systemLog = systemLogQuery.findAndCheck(id);
    Path filePath = Paths.get(systemLog.getFilePath());
    if (!Files.exists(filePath)) {
      throw ProtocolException.of("日志文件不存在: " + systemLog.getFilePath());
    }
    MediaType mediaType = Boolean.TRUE.equals(systemLog.getCompressed())
        ? MediaType.parseMediaType("application/gzip")
        : MediaType.APPLICATION_OCTET_STREAM;
    long fileSize = systemLog.getSize() != null ? systemLog.getSize() : 0;
    InputStreamResource resource = new InputStreamResource(Files.newInputStream(filePath));
    return new DownloadResult(resource, systemLog.getFilename(), fileSize, mediaType);
  }

  @Override
  public void delete(Long id, Boolean permanent) {
    systemLogCmd.delete(id, permanent);
  }

  @Override
  public void batchDelete(SystemLogBatchDeleteDto dto) {
    List<Long> ids = dto.getIds().stream()
        .map(Long::parseLong)
        .collect(java.util.stream.Collectors.toList());
    systemLogCmd.batchDelete(ids, dto.getPermanent());
  }

  @Override
  public SystemLogStatisticsVo getStatistics(SystemLogStatisticsDto dto) {
    return systemLogQuery.getStatistics(dto);
  }
}
