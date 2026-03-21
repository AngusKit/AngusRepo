package cloud.xcan.angus.core.gm.infra.log;

import cloud.xcan.angus.core.gm.domain.log.SystemLog;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.SystemLogContentDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.SystemLogContentVo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 系统日志文件读取服务
 */
@Slf4j
@Service
public class SystemLogFileService {

  /**
   * 读取日志文件内容
   */
  public SystemLogContentVo readLogContent(SystemLog systemLog, SystemLogContentDto dto)
      throws IOException {
    SystemLogContentVo vo = new SystemLogContentVo();
    vo.setId(systemLog.getId());
    vo.setFilename(systemLog.getFilename());

    Path filePath = Paths.get(systemLog.getFilePath());
    if (!Files.exists(filePath)) {
      throw new IOException("日志文件不存在: " + systemLog.getFilePath());
    }

    // 读取总行数
    long totalLines = Files.lines(filePath).count();
    vo.setTotalLines(totalLines);

    // 确定读取范围
    int pageSize = dto.getSize() != null ? dto.getSize() : 100;
    int currentPage = dto.getPage() != null ? dto.getPage() : 1;
    int startLine = 1;
    int endLine;

    // 检查是否使用tail模式
    boolean useTail = Boolean.TRUE.equals(dto.getTail());
    if (useTail) {
      // tail模式：从文件末尾读取指定行数
      int tailLines = dto.getTailLines() != null ? dto.getTailLines() : pageSize;
      if (tailLines > 0) {
        startLine = Math.max(1, (int) totalLines - tailLines + 1);
        endLine = (int) totalLines;
        currentPage = 1; // tail模式不使用分页
      } else {
        startLine = 1;
        endLine = (int) totalLines;
      }
    } else if (dto.getStartLine() != null && dto.getEndLine() != null) {
      // 使用行号范围
      startLine = dto.getStartLine();
      endLine = dto.getEndLine();
      currentPage = 1;
    } else {
      // 使用分页
      startLine = (currentPage - 1) * pageSize + 1;
      endLine = Math.min(startLine + pageSize - 1, (int) totalLines);
    }

    vo.setCurrentPage(currentPage);
    vo.setPageSize(pageSize);
    vo.setTotalPages((int) Math.ceil((double) totalLines / pageSize));

    // 读取日志行（优化：对于tail模式或大文件，使用流式读取）
    List<SystemLogContentVo.LogLineVo> lines = readLogLines(filePath, startLine, endLine, dto);
    vo.setLines(lines);
    return vo;
  }

  /**
   * 读取日志行（优化版本，支持大文件）
   */
  private List<SystemLogContentVo.LogLineVo> readLogLines(Path filePath, int startLine, int endLine,
      SystemLogContentDto dto) throws IOException {
    List<SystemLogContentVo.LogLineVo> lines = new ArrayList<>();

    // 构建过滤模式
    Pattern keywordPattern = dto.getKeyword() != null && !dto.getKeyword().isEmpty()
        ? Pattern.compile(Pattern.quote(dto.getKeyword()), Pattern.CASE_INSENSITIVE)
        : null;
    Pattern levelFilterPattern = dto.getLevel() != null
        ? Pattern.compile("\\b" + dto.getLevel().name() + "\\b", Pattern.CASE_INSENSITIVE)
        : null;

    // 对于大文件，使用流式读取避免加载整个文件到内存
    try (Stream<String> stream = Files.lines(filePath)) {
      int currentLineNumber = 0;
      int matchedCount = 0;

      for (String line : (Iterable<String>) stream::iterator) {
        currentLineNumber++;

        // 只处理指定范围内的行
        if (currentLineNumber < startLine) {
          continue;
        }
        if (currentLineNumber > endLine) {
          break;
        }

        // 关键词过滤
        if (keywordPattern != null && !keywordPattern.matcher(line).find()) {
          continue;
        }

        // 日志级别过滤
        if (levelFilterPattern != null && !levelFilterPattern.matcher(line).find()) {
          continue;
        }

        SystemLogContentVo.LogLineVo logLine = parseLogLine(line, currentLineNumber);
        logLine.setRawLine(line);
        lines.add(logLine);
        matchedCount++;

        // 限制返回的行数，避免内存溢出
        if (matchedCount >= 10000) {
          log.warn("日志文件过滤后行数过多，已限制返回前10000行");
          break;
        }
      }
    }
    return lines;
  }

  /**
   * 解析日志行，支持 spring-logback.xml 配置的格式： dev: [app] :: yyyy-MM-dd HH:mm:ss.SSS level pid :: [thread]
   * logger -> message prod: [app] :: yyyy-MM-dd HH:mm:ss.SSS level pid -> message
   */
  private SystemLogContentVo.LogLineVo parseLogLine(String line, int lineNumber) {
    SystemLogContentVo.LogLineVo logLine = new SystemLogContentVo.LogLineVo();
    logLine.setLineNumber(lineNumber);

    try {
      String trimmed = line.trim();
      if (trimmed.isEmpty()) {
        logLine.setMessage(line);
        return logLine;
      }

      // dev 格式: [app] :: timestamp level pid :: [thread] logger -> message
      java.util.regex.Pattern devPattern = java.util.regex.Pattern.compile(
          "^\\[[^\\]]*\\]\\s*::\\s*(\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\s+(\\w+)\\s+\\S*\\s*::\\s*\\[([^\\]]*)\\]\\s+(.+)\\s+->\\s*(.*)$",
          java.util.regex.Pattern.DOTALL);
      java.util.regex.Matcher devMatcher = devPattern.matcher(trimmed);
      if (devMatcher.matches()) {
        logLine.setTimestamp(devMatcher.group(1));
        logLine.setLevel(devMatcher.group(2));
        logLine.setThread(devMatcher.group(3).trim());
        logLine.setLogger(devMatcher.group(4).trim());
        logLine.setMessage(devMatcher.group(5).trim());
        return logLine;
      }

      // prod 格式: [app] :: timestamp level pid -> message
      java.util.regex.Pattern prodPattern = java.util.regex.Pattern.compile(
          "^\\[[^\\]]*\\]\\s*::\\s*(\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\s+(\\w+)\\s+\\S*\\s*->\\s*(.*)$",
          java.util.regex.Pattern.DOTALL);
      java.util.regex.Matcher prodMatcher = prodPattern.matcher(trimmed);
      if (prodMatcher.matches()) {
        logLine.setTimestamp(prodMatcher.group(1));
        logLine.setLevel(prodMatcher.group(2));
        logLine.setMessage(prodMatcher.group(3).trim());
        return logLine;
      }

      // 简单格式: timestamp level [thread] logger - message
      java.util.regex.Pattern simplePattern = java.util.regex.Pattern.compile(
          "^(\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}(?:\\.\\d{3})?)\\s+(\\w+)\\s+\\[([^\\]]*)\\]\\s+(.+?)\\s+-\\s+(.*)$",
          java.util.regex.Pattern.DOTALL);
      java.util.regex.Matcher simpleMatcher = simplePattern.matcher(trimmed);
      if (simpleMatcher.matches()) {
        logLine.setTimestamp(simpleMatcher.group(1));
        logLine.setLevel(simpleMatcher.group(2));
        logLine.setThread(simpleMatcher.group(3).trim());
        logLine.setLogger(simpleMatcher.group(4) != null ? simpleMatcher.group(4).trim() : null);
        logLine.setMessage(simpleMatcher.group(5) != null ? simpleMatcher.group(5).trim() : "");
        return logLine;
      }

      logLine.setMessage(line);
    } catch (Exception e) {
      log.warn("解析日志行失败: {}", line, e);
      logLine.setMessage(line);
    }

    return logLine;
  }
}
