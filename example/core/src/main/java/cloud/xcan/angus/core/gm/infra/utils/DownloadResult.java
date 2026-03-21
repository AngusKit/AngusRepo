package cloud.xcan.angus.core.gm.infra.utils;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

/**
 * 系统日志下载结果
 */
public record DownloadResult(
    Resource resource,
    String filename,
    long filesize,
    MediaType mediaType
) {
}
