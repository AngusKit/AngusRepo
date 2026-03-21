package cloud.xcan.angus.core.gm.interfaces.tag;

import cloud.xcan.angus.core.gm.interfaces.tag.facade.TagCategoryFacade;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.CreateTagCategoryDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.UpdateTagCategoryDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagCategoryVo;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagListVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "TagCategory", description = "标签分类管理 - 标签分类的创建、管理等功能")
@Validated
@RestController
@RequestMapping("/api/v1/tag/categories")
public class TagCategoryRest {

  @Resource
  private TagCategoryFacade tagCategoryFacade;

  @Operation(operationId = "createTagCategory", summary = "创建标签分类", description = "创建新的标签分类")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "创建成功"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<TagCategoryVo> create(
      @Valid @RequestBody CreateTagCategoryDto dto) {
    return ApiLocaleResult.success(tagCategoryFacade.create(dto));
  }

  @Operation(operationId = "updateTagCategory", summary = "更新标签分类", description = "更新标签分类信息（仅允许更新非系统分类）")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "404", description = "标签分类不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}")
  public ApiLocaleResult<TagCategoryVo> update(
      @Parameter(description = "分类ID") @PathVariable Long id,
      @Valid @RequestBody UpdateTagCategoryDto dto) {
    return ApiLocaleResult.success(tagCategoryFacade.update(id, dto));
  }

  @Operation(operationId = "deleteTagCategory", summary = "删除标签分类", description = "删除指定的标签分类（仅允许删除非系统分类且没有标签的分类）")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功"),
      @ApiResponse(responseCode = "404", description = "标签分类不存在")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void delete(
      @Parameter(description = "分类ID") @PathVariable Long id) {
    tagCategoryFacade.delete(id);
  }

  @Operation(operationId = "getTagCategoryDetail", summary = "获取标签分类详情",
      description = "根据ID查询标签分类详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "标签分类详情获取成功"),
      @ApiResponse(responseCode = "404", description = "标签分类不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}")
  public ApiLocaleResult<TagCategoryVo> getDetail(
      @Parameter(description = "分类ID") @PathVariable Long id) {
    return ApiLocaleResult.success(tagCategoryFacade.getById(id));
  }

  @Operation(operationId = "getTagListByCategoryCode", summary = "根据分类code查询所有标签列表",
      description = "根据分类code查询所有标签列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "标签列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{code}/tags")
  public ApiLocaleResult<List<TagListVo>> getTagListByCategoryCode(
      @Parameter(description = "分类编码") @PathVariable String code) {
    return ApiLocaleResult.success(tagCategoryFacade.getTagListByCategoryCode(code));
  }

  @Operation(operationId = "getTagCategoryList", summary = "获取标签分类列表",
      description = "查询标签分类列表（不分页，返回所有分类）")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "标签分类列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<List<TagCategoryVo>> list() {
    return ApiLocaleResult.success(tagCategoryFacade.list());
  }
}
