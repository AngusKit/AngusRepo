package cloud.xcan.angus.core.gm.infra.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DockerDetector {

  /**
   * 判断当前 Java 进程是否运行在 Docker 容器内
   *
   * @return true 如果在容器内，否则 false
   */
  public static boolean isRunningInDocker() {
    // 方式1：检查 /.dockerenv 文件是否存在（Docker 默认创建）
    if (Files.exists(Paths.get("/.dockerenv"))) {
      return true;
    }

    // 方式2：检查 /proc/1/cgroup 是否包含容器相关标识
    try {
      String cgroup = Files.readString(Paths.get("/proc/1/cgroup"));
      // 常见容器运行时标识：docker、kubepods（Kubernetes）、containerd、podman 等
      return cgroup.contains("docker") ||
          cgroup.contains("kubepods") ||
          cgroup.contains("containerd") ||
          cgroup.contains("/docker/");
    } catch (IOException e) {
      // 文件不存在或无法读取，说明不在容器内（或非 Linux 环境）
      return false;
    }
  }
}
