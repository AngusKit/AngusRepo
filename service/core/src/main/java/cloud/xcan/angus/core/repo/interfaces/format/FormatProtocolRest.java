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
 * Generic protocol-specific REST controller.
 * Routes format-specific protocol requests (Maven, Docker, NPM, etc.) to the
 * appropriate format handler via the FormatHandlerRegistry.
 *
 * <p>This controller provides ID-based and name-based repository routing as a fallback
 * for formats that don't have dedicated protocol controllers. For format-specific
 * protocol endpoints, see the dedicated controllers in the protocol package:
 * <ul>
 *   <li>{@code /maven/{name}/**} - MavenProtocolRest</li>
 *   <li>{@code /v2/} - DockerProtocolRest (Docker Registry V2)</li>
 *   <li>{@code /npm/{name}/**} - NpmProtocolRest</li>
 *   <li>{@code /pypi/{name}/**} - PyPIProtocolRest</li>
 *   <li>{@code /nuget/{name}/**} - NuGetProtocolRest</li>
 *   <li>{@code /helm/{name}/**} - HelmProtocolRest</li>
 *   <li>{@code /go/{name}/**} - GoProtocolRest</li>
 *   <li>{@code /apt/{name}/**} - AptProtocolRest</li>
 *   <li>{@code /yum/{name}/**} - YumProtocolRest</li>
 *   <li>{@code /raw/{name}/**} - RawProtocolRest</li>
 * </ul>
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

  @Operation(summary = "通过仓库ID处理协议请求", description = "通过仓库ID路由到对应格式处理器",
      operationId = "format:handleProtocolById")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "请求处理成功"),
      @ApiResponse(responseCode = "404", description = "仓库或制品不存在")
  })
  @RequestMapping(value = "/id/{repositoryId}/**", method = {
      RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST,
      RequestMethod.DELETE, RequestMethod.HEAD, RequestMethod.PATCH
  })
  public ResponseEntity<?> handleProtocolRequestById(
      @PathVariable Long repositoryId, HttpServletRequest request) {
    RepoEntity repository = repositoryQuery.findAndCheck(repositoryId);
    ArtifactFormatHandler handler = formatHandlerRegistry.getHandler(repository.getFormat());
    return handler.handleFormatSpecificRequest(request, repository);
  }

  @Operation(summary = "通过仓库名称处理协议请求", description = "通过仓库名称路由到对应格式处理器",
      operationId = "format:handleProtocolByName")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "请求处理成功"),
      @ApiResponse(responseCode = "404", description = "仓库或制品不存在")
  })
  @RequestMapping(value = "/name/{repositoryName}/**", method = {
      RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST,
      RequestMethod.DELETE, RequestMethod.HEAD, RequestMethod.PATCH
  })
  public ResponseEntity<?> handleProtocolRequestByName(
      @PathVariable String repositoryName, HttpServletRequest request) {
    RepoEntity repository = repositoryQuery.findByNameAndCheck(repositoryName);
    ArtifactFormatHandler handler = formatHandlerRegistry.getHandler(repository.getFormat());
    return handler.handleFormatSpecificRequest(request, repository);
  }
}
