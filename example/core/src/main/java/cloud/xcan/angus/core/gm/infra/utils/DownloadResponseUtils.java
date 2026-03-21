package cloud.xcan.angus.core.gm.infra.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;

/**
 * 下载响应构建工具类
 */
public final class DownloadResponseUtils {

  private DownloadResponseUtils() {
  }

  /**
   * 构建文件下载的 ResponseEntity
   *
   * @param cacheAge  缓存时间（秒），&lt;=0 表示不启用缓存
   * @param mediaType 媒体类型
   * @param filename  文件名
   * @param filesize  文件大小（字节），&lt;=0 表示不设置 Content-Length
   * @param resource  资源
   * @return ResponseEntity
   */
  public static ResponseEntity<Resource> buildDownloadResourceResponseEntity(
      int cacheAge, MediaType mediaType, String filename, long filesize,
      Resource resource) {
    BodyBuilder bodyBuilder = ResponseEntity.ok();
    if (cacheAge > 0) {
      CacheControl cacheControl = CacheControl.maxAge(cacheAge, TimeUnit.SECONDS)
          .noTransform().mustRevalidate();
      bodyBuilder.cacheControl(cacheControl);
    }
    if (filesize > 0) {
      bodyBuilder.contentLength(filesize);
    }
    return bodyBuilder.contentType(mediaType)
        .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
            + URLEncoder.encode(filename, StandardCharsets.UTF_8) + "\"")
        .body(resource);
  }
}
