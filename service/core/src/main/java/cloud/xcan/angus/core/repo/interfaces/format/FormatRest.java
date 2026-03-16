package cloud.xcan.angus.core.repo.interfaces.format;

import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import cloud.xcan.angus.core.repo.interfaces.format.facade.FormatFacade;
import cloud.xcan.angus.core.repo.interfaces.format.facade.dto.FormatMetadataFindDto;
import cloud.xcan.angus.core.repo.interfaces.format.facade.dto.FormatSetupGuideDto;
import cloud.xcan.angus.core.repo.interfaces.format.facade.dto.FormatValidateDto;
import cloud.xcan.angus.core.repo.interfaces.format.facade.vo.FormatMetadataVo;
import cloud.xcan.angus.core.repo.interfaces.format.facade.vo.FormatSetupGuideVo;
import cloud.xcan.angus.core.repo.interfaces.format.facade.vo.FormatSupportedVo;
import cloud.xcan.angus.core.repo.interfaces.format.facade.vo.FormatValidationResultVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Formats", description = "格式服务 - 仓库格式管理、元数据查询、配置指南、格式验证")
@Validated
@RestController
@RequestMapping("/api/v1/formats")
public class FormatRest {

  @Resource
  private FormatFacade formatFacade;

  @Operation(summary = "查询支持的格式列表", description = "获取系统支持的所有仓库格式及其状态",
      operationId = "format:listSupported")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping
  public ApiLocaleResult<List<FormatSupportedVo>> listSupportedFormats() {
    return ApiLocaleResult.success(formatFacade.getSupportedFormats());
  }

  @Operation(summary = "获取客户端配置指南", description = "获取指定仓库的客户端配置说明",
      operationId = "format:getSetupGuide")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @PostMapping("/setup-guide")
  public ApiLocaleResult<FormatSetupGuideVo> getSetupGuide(
      @Valid @RequestBody FormatSetupGuideDto dto) {
    return ApiLocaleResult.success(formatFacade.getSetupGuide(dto));
  }

  @Operation(summary = "验证制品格式", description = "验证指定文件名是否符合仓库格式要求",
      operationId = "format:validate")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "验证完成")
  })
  @PostMapping("/validate")
  public ApiLocaleResult<FormatValidationResultVo> validateArtifact(
      @Valid @RequestBody FormatValidateDto dto) {
    return ApiLocaleResult.success(formatFacade.validateArtifact(dto));
  }

  @Operation(summary = "查询格式元数据列表", description = "查询指定仓库的格式特定元数据列表",
      operationId = "format:listMetadata")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @PostMapping("/metadata/list")
  public ApiLocaleResult<List<FormatMetadataVo>> listMetadata(
      @Valid @RequestBody FormatMetadataFindDto dto) {
    return ApiLocaleResult.success(formatFacade.listMetadata(dto));
  }

  @Operation(summary = "获取仓库索引", description = "获取指定仓库的格式索引文件（如maven-metadata.xml、index.yaml等）",
      operationId = "format:getIndex")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @GetMapping("/repositories/{repositoryId}/index")
  public ResponseEntity<byte[]> getIndex(@Parameter(name = "repositoryId", description = "repositoryId") @PathVariable Long repositoryId) {
    byte[] index = formatFacade.getIndex(repositoryId);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.setContentLength(index.length);
    return new ResponseEntity<>(index, headers, HttpStatus.OK);
  }

  @Operation(summary = "删除格式元数据", description = "删除指定格式的元数据记录",
      operationId = "format:deleteMetadata")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @DeleteMapping("/metadata/{format}/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteMetadata(@Parameter(name = "format", description = "format") @PathVariable RepositoryFormat format, @Parameter(name = "id", description = "id") @PathVariable Long id) {
    formatFacade.deleteMetadata(format, id);
  }
}
