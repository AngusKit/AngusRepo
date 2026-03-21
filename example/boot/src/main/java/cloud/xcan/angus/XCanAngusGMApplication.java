package cloud.xcan.angus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableFeignClients(basePackages = {
    "cloud.xcan.angus.api",
    "cloud.xcan.angus.security",
    "cloud.xcan.angus.core.event.remote",
    "cloud.xcan.angus.core.gm.infra.remote"
})
@EnableEurekaServer
@SpringBootApplication(scanBasePackages = {"cloud.xcan.angus"})
public class XCanAngusGMApplication {

  public static void main(String[] args) {
    SpringApplication.run(XCanAngusGMApplication.class, args);
  }

}
