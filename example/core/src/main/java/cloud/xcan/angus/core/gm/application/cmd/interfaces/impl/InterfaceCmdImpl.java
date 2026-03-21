package cloud.xcan.angus.core.gm.application.cmd.interfaces.impl;

import static cloud.xcan.angus.api.commonlink.GMConstant.SWAGGER_API_URL;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.PermissionCheck;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.interfaces.InterfaceCmd;
import cloud.xcan.angus.core.gm.application.query.interfaces.InterfaceQuery;
import cloud.xcan.angus.core.gm.application.query.service.ServiceConfigQuery;
import cloud.xcan.angus.core.gm.domain.interfaces.Interface;
import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRepo;
import cloud.xcan.angus.core.gm.domain.interfaces.enums.InterfaceSyncAction;
import cloud.xcan.angus.core.gm.infra.eureka.EurekaClientService;
import cloud.xcan.angus.core.gm.infra.eureka.dto.EurekaApplication;
import cloud.xcan.angus.core.gm.infra.eureka.dto.EurekaApplications;
import cloud.xcan.angus.core.gm.infra.eureka.dto.EurekaInstance;
import cloud.xcan.angus.core.gm.infra.oas.OpenApiParser;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.spec.http.HttpMethod;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.tags.Tag;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class InterfaceCmdImpl extends CommCmd<Interface, Long> implements InterfaceCmd {

  @Resource
  private InterfaceRepo interfaceRepo;

  @Resource
  private InterfaceQuery interfaceQuery;

  @Resource
  private EurekaClientService eurekaClientService;

  @Resource
  private ServiceConfigQuery serviceConfigQuery;

  @Resource
  private InterfaceCmd interfaceCmd;

  @Override
  public void sync(String serviceName) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        PermissionCheck.checkCloudTenantSecurity();

        // 获取Eureka配置
        var eurekaConfig = serviceConfigQuery.getEurekaConfig();

        // 获取指定服务信息
        EurekaApplication application = eurekaClientService.getApplication(eurekaConfig,
            serviceName);
        if (application == null || application.getInstance() == null
            || application.getInstance().isEmpty()) {
          return null;
        }

        // 找到UP状态的实例
        Optional<EurekaInstance> upInstance = application.getInstance().stream()
            .filter(instance -> "UP".equals(instance.getStatus()))
            .findFirst();

        if (upInstance.isEmpty()) {
          return null;
        }

        // 同步该服务的接口
        interfaceCmd.syncServiceInterfaces(serviceName, upInstance.get());
        return null;
      }
    }.execute();
  }

  @Override
  public void syncAll() {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        // 获取Eureka配置
        var eurekaConfig = serviceConfigQuery.getEurekaConfig();

        // 获取所有应用列表
        EurekaApplications applications = eurekaClientService.getApplications(eurekaConfig);
        if (applications == null || applications.getApplications() == null
            || applications.getApplications().getApplication() == null) {
          return null;
        }

        // 遍历所有服务
        for (EurekaApplication app : applications.getApplications().getApplication()) {
          if (app.getInstance() == null || app.getInstance().isEmpty()) {
            continue;
          }

          // 找到UP状态的实例
          Optional<EurekaInstance> upInstance = app.getInstance().stream()
              .filter(instance -> "UP".equals(instance.getStatus()))
              .findFirst();

          // 同步该服务的接口
          upInstance.ifPresent(
              eurekaInstance -> interfaceCmd.syncServiceInterfaces(app.getName(), eurekaInstance));
        }

        return null;
      }
    }.execute();
  }

  /**
   * 同步指定服务的接口信息
   *
   * @param serviceName 服务名称
   * @param instance    Eureka实例信息
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void syncServiceInterfaces(String serviceName, EurekaInstance instance) {
    try {
      // 构建OpenAPI文档URL
      String openApiUrl = buildOpenApiUrl(instance);
      if (openApiUrl == null) {
        return;
      }

      // 解析OpenAPI文档
      OpenAPI openAPI = OpenApiParser.checkAndParseOpenApi(openApiUrl);
      if (openAPI == null || openAPI.getPaths() == null) {
        return;
      }

      // 获取版本信息
      String version = openAPI.getInfo() != null ? openAPI.getInfo().getVersion() : null;

      // 获取Tags映射（用于tag_description）
      Map<String, Tag> tagsMap = null;
      if (openAPI.getTags() != null) {
        tagsMap = openAPI.getTags().stream()
            .collect(java.util.stream.Collectors.toMap(Tag::getName, tag -> tag));
      }

      // 提取并保存接口信息
      LocalDateTime syncTime = LocalDateTime.now();
      List<Interface> interfacesToSave = new ArrayList<>();

      // 遍历所有路径
      for (Map.Entry<String, PathItem> pathEntry : openAPI.getPaths().entrySet()) {
        String path = pathEntry.getKey();
        PathItem pathItem = pathEntry.getValue();

        // 处理GET方法
        if (pathItem.getGet() != null) {
          Interface interfaceEntity = createInterfaceFromOperation(
              serviceName, path, HttpMethod.GET, pathItem.getGet(), version, tagsMap, syncTime);
          if (interfaceEntity != null) {
            interfacesToSave.add(interfaceEntity);
          }
        }

        // 处理POST方法
        if (pathItem.getPost() != null) {
          Interface interfaceEntity = createInterfaceFromOperation(
              serviceName, path, HttpMethod.POST, pathItem.getPost(), version, tagsMap, syncTime);
          if (interfaceEntity != null) {
            interfacesToSave.add(interfaceEntity);
          }
        }

        // 处理PUT方法
        if (pathItem.getPut() != null) {
          Interface interfaceEntity = createInterfaceFromOperation(
              serviceName, path, HttpMethod.PUT, pathItem.getPut(), version, tagsMap, syncTime);
          if (interfaceEntity != null) {
            interfacesToSave.add(interfaceEntity);
          }
        }

        // 处理DELETE方法
        if (pathItem.getDelete() != null) {
          Interface interfaceEntity = createInterfaceFromOperation(
              serviceName, path, HttpMethod.DELETE, pathItem.getDelete(), version, tagsMap,
              syncTime);
          if (interfaceEntity != null) {
            interfacesToSave.add(interfaceEntity);
          }
        }

        // 处理PATCH方法
        if (pathItem.getPatch() != null) {
          Interface interfaceEntity = createInterfaceFromOperation(
              serviceName, path, HttpMethod.PATCH, pathItem.getPatch(), version, tagsMap,
              syncTime);
          if (interfaceEntity != null) {
            interfacesToSave.add(interfaceEntity);
          }
        }
      }

      // 批量保存或更新接口
      if (interfacesToSave.isEmpty()) {
        return;
      }

      // 批量查询已存在的接口（通过code列表）
      List<String> codes = interfacesToSave.stream()
          .map(Interface::getCode)
          .toList();
      List<Interface> existingInterfaces = interfaceRepo.findByCodeIn(codes);

      // 创建code到已存在接口的映射
      Map<String, Interface> existingMap = existingInterfaces.stream()
          .collect(java.util.stream.Collectors.toMap(Interface::getCode, i -> i));

      // 分离需要更新和需要创建的接口
      List<Interface> toUpdate = new ArrayList<>();
      List<Interface> toCreate = new ArrayList<>();

      for (Interface interfaceEntity : interfacesToSave) {
        Interface existing = existingMap.get(interfaceEntity.getCode());
        if (existing != null) {
          // 更新现有接口
          existing.setServiceName(interfaceEntity.getServiceName());
          existing.setName(interfaceEntity.getName());
          existing.setPath(interfaceEntity.getPath());
          existing.setMethod(interfaceEntity.getMethod());
          existing.setSummary(interfaceEntity.getSummary());
          existing.setDescription(interfaceEntity.getDescription());
          existing.setTag(interfaceEntity.getTag());
          existing.setTagDescription(interfaceEntity.getTagDescription());
          existing.setVersion(interfaceEntity.getVersion());
          existing.setDeprecated(interfaceEntity.getDeprecated());
          existing.setLastSyncTime(interfaceEntity.getLastSyncTime());
          existing.setLastSyncAction(InterfaceSyncAction.UPDATE);
          // 保持原有状态，除非接口被标记为废弃
          if (Boolean.TRUE.equals(interfaceEntity.getDeprecated())) {
            existing.setStatus(EnabledStatus.DISABLED);
          }
          toUpdate.add(existing);
        } else {
          // 创建新接口
          interfaceEntity.setId(uidGenerator.getUID());
          interfaceEntity.setStatus(EnabledStatus.ENABLED);
          interfaceEntity.setLastSyncAction(InterfaceSyncAction.CREATE);
          toCreate.add(interfaceEntity);
        }
      }

      // 批量更新
      if (!toUpdate.isEmpty()) {
        batchUpdate0(toUpdate);
      }

      // 批量创建
      if (!toCreate.isEmpty()) {
        batchInsert(toCreate);
      }
    } catch (Exception e) {
      log.error("Execute syncServiceInterfaces exception", e);
    }
  }

  /**
   * 从OpenAPI Operation创建Interface实体
   */
  private Interface createInterfaceFromOperation(String serviceName, String path,
      HttpMethod method, Operation operation, String version, Map<String, Tag> tagsMap,
      LocalDateTime syncTime) {
    if (operation == null) {
      return null;
    }

    // 获取operationId作为code（必须唯一）
    String code = operation.getOperationId();
    if (!StringUtils.hasText(code)) {
      // 如果没有operationId，使用路径和方法生成
      code = serviceName + "_" + method.name() + "_" + path.replaceAll("[^a-zA-Z0-9]", "_");
      // 确保code长度不超过100
      if (code.length() > 100) {
        code = code.substring(0, 100);
      }
    }

    Interface entity = new Interface();
    entity.setServiceName(serviceName);
    entity.setCode(code);
    entity.setPath(path);
    entity.setMethod(method);
    entity.setSummary(operation.getSummary());
    entity.setDescription(operation.getDescription());
    entity.setVersion(version);
    entity.setDeprecated(operation.getDeprecated() != null && operation.getDeprecated());
    entity.setLastSyncTime(syncTime);

    // 设置name（优先使用summary，否则使用operationId）
    String name = operation.getSummary();
    if (!StringUtils.hasText(name)) {
      name = operation.getOperationId();
    }
    if (!StringUtils.hasText(name)) {
      name = method.name() + " " + path;
    }
    entity.setName(name.length() > 100 ? name.substring(0, 100) : name);

    // 处理Tags（Angus应用只允许一个Tag）
    if (operation.getTags() != null && !operation.getTags().isEmpty()) {
      String tag = operation.getTags().get(0);
      entity.setTag(tag);
      // 设置tag描述
      if (tagsMap != null && tagsMap.containsKey(tag)) {
        Tag tagObj = tagsMap.get(tag);
        if (tagObj != null && StringUtils.hasText(tagObj.getDescription())) {
          entity.setTagDescription(tagObj.getDescription());
        }
      }
    }

    return entity;
  }

  /**
   * 构建OpenAPI文档URL 支持多种格式： 1. http://{ip:port}/v3/api-docs (SpringDoc默认) 2.
   * http://{ip:port}/v3/api-docs/{serviceName} (指定服务名) 3. http://{ip:port}/swagger-ui.html (Swagger
   * UI，但通常不用于API文档)
   */
  private String buildOpenApiUrl(EurekaInstance instance) {
    if (instance == null || instance.getIpAddr() == null) {
      return null;
    }

    // 确定端口（优先使用port，如果port未启用则使用securePort）
    Integer port = null;
    boolean useHttps = false;
    if (instance.getPort() != null && instance.getPort().getEnabled() != null
        && instance.getPort().getEnabled() && instance.getPort().getValue() != null) {
      port = instance.getPort().getValue();
      useHttps = false;
    } else if (instance.getSecurePort() != null
        && instance.getSecurePort().getEnabled() != null
        && instance.getSecurePort().getEnabled()
        && instance.getSecurePort().getValue() != null) {
      port = instance.getSecurePort().getValue();
      useHttps = true;
    }

    if (port == null) {
      return null;
    }

    // 构建URL（默认使用HTTP，如果securePort启用则使用HTTPS）
    String protocol = useHttps ? "https" : "http";

    // SpringDoc默认路径是/v3/api-docs
    // 注意：某些服务可能使用/v3/api-docs/{serviceName}格式，这里先尝试默认路径
    return String.format("%s://%s:%d%s", protocol, instance.getIpAddr(), port, SWAGGER_API_URL);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Interface deprecate(Long id, Boolean deprecated, String deprecationNote) {
    return new BizTemplate<Interface>() {
      Interface interfaceDb;

      @Override
      protected void checkParams() {
        interfaceDb = interfaceQuery.findAndCheck(id);
      }

      @Override
      protected Interface process() {
        interfaceDb.setDeprecated(deprecated);
        interfaceDb.setDeprecationNote(deprecationNote);
        interfaceRepo.save(interfaceDb);
        return interfaceDb;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Interface, Long> getRepository() {
    return interfaceRepo;
  }
}

