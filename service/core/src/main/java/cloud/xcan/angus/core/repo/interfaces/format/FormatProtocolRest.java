package cloud.xcan.angus.core.repo.interfaces.format;

import cloud.xcan.angus.core.repo.application.query.repository.RepositoryQuery;
import cloud.xcan.angus.core.repo.domain.format.ArtifactFormatHandler;
import cloud.xcan.angus.core.repo.domain.format.FormatHandlerRegistry;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Protocol-specific REST controller.
 * Routes format-specific protocol requests (Maven, Docker, NPM, etc.) to the
 * appropriate format handler via the FormatHandlerRegistry.
 */
@Tag(name = "Format Protocol", description = "格式协议 - 处理各种包管理器的协议请求（Maven、Docker、NPM等）")
@Validated
@RestController
@RequestMapping("/repository")
public class FormatProtocolRest {

  @Resource
  private FormatHandlerRegistry formatHandlerRegistry;

  @Resource
  private RepositoryQuery repositoryQuery;

  @Operation(summary = "处理格式协议请求", description = "处理各种包管理器客户端的协议请求",
      operationId = "format:handleProtocol")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "请求处理成功"),
      @ApiResponse(responseCode = "404", description = "仓库或制品不存在")
  })
  @RequestMapping(value = "/{repositoryId}/**", method = {
      RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST,
      RequestMethod.DELETE, RequestMethod.HEAD, RequestMethod.PATCH
  })
  public ResponseEntity<?> handleProtocolRequest(
      @PathVariable Long repositoryId, HttpServletRequest request) {
    RepoEntity repository = repositoryQuery.findAndCheck(repositoryId);
    ArtifactFormatHandler handler = formatHandlerRegistry.getHandler(repository.getFormat());
    return handler.handleFormatSpecificRequest(request, repository);
  }
}
