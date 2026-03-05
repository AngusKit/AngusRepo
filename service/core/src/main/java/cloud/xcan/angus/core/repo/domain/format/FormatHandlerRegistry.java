package cloud.xcan.angus.core.repo.domain.format;

import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Format service registry center.
 * Collects all ArtifactFormatHandler implementations and provides lookup by format.
 */
@Component
public class FormatHandlerRegistry {

  private final Map<RepositoryFormat, ArtifactFormatHandler> handlers;

  public FormatHandlerRegistry(List<ArtifactFormatHandler> handlerList) {
    this.handlers = handlerList.stream()
        .collect(Collectors.toMap(ArtifactFormatHandler::getFormat, h -> h));
  }

  /**
   * Get the handler for the specified format.
   *
   * @param format the repository format
   * @return the corresponding format handler
   * @throws UnsupportedFormatException if no handler is registered for the format
   */
  public ArtifactFormatHandler getHandler(RepositoryFormat format) {
    ArtifactFormatHandler handler = handlers.get(format);
    if (handler == null) {
      throw new UnsupportedFormatException("Unsupported format: " + format);
    }
    return handler;
  }

  /**
   * Check if a handler is registered for the specified format.
   */
  public boolean hasHandler(RepositoryFormat format) {
    return handlers.containsKey(format);
  }

  /**
   * Get all registered formats.
   */
  public Map<RepositoryFormat, ArtifactFormatHandler> getAllHandlers() {
    return Map.copyOf(handlers);
  }
}
