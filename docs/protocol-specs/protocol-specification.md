# 制品仓库协议规范参考文档

本文档为 AngusRepo 制品仓库所实现的10种协议接口提供规范参考，包括每种协议的标准规范来源、所实现的API端点以及协议合规性评审结果。

---

## 目录
1. [Maven Protocol](#1-maven-protocol)
2. [Docker Protocol](#2-docker-protocol)
3. [NPM Protocol](#3-npm-protocol)
4. [PyPI Protocol](#4-pypi-protocol)
5. [NuGet Protocol](#5-nuget-protocol)
6. [Helm Protocol](#6-helm-protocol)
7. [Go Protocol](#7-go-protocol)
8. [APT Protocol](#8-apt-protocol)
9. [YUM Protocol](#9-yum-protocol)
10. [Raw Protocol](#10-raw-protocol)

---

## 1. Maven Protocol

### 规范参考
- [Maven Repository Layout](https://maven.apache.org/repository/layout.html)
- [Maven Repository Metadata](https://maven.apache.org/ref/3.9.6/maven-repository-metadata/)
- [POM Reference](https://maven.apache.org/pom.html)

### 路径格式
```
/maven/{repositoryName}/{groupId-as-path}/{artifactId}/{version}/{artifactId}-{version}.{extension}
```
示例: `/maven/central/org/springframework/spring-core/6.1.0/spring-core-6.1.0.jar`

### 已实现的端点

| 方法 | 路径 | 描述 | 仓库类型 |
|------|------|------|----------|
| GET | `/{repositoryName}/**` | 下载制品（.jar/.pom/.war/.ear/.xml/checksum） | 全部 |
| PUT | `/{repositoryName}/**` | 上传制品 | hosted |
| HEAD | `/{repositoryName}/**` | 检查制品是否存在 | 全部 |
| DELETE | `/{repositoryName}/**` | 删除制品 | hosted |

### 协议合规性评审

**合规项：**
- ✅ 支持GAV坐标路径解析
- ✅ 支持maven-metadata.xml生成
- ✅ 支持校验和文件（.sha1/.md5/.sha256/.sha512）
- ✅ 正确区分Content-Type（XML/OCTET_STREAM/TEXT_PLAIN）
- ✅ hosted仓库写操作限制

**改进建议：**
- ⚠️ 未实现snapshot版本元数据处理（SNAPSHOT版本的maven-metadata.xml包含时间戳）
- ⚠️ 未实现.asc签名文件支持（GPG签名验证）

---

## 2. Docker Protocol

### 规范参考
- [Docker Registry HTTP API V2](https://docs.docker.com/registry/spec/api/)
- [OCI Distribution Specification](https://github.com/opencontainers/distribution-spec/blob/main/spec.md)
- [Docker Manifest V2 Schema 2](https://docs.docker.com/registry/spec/manifest-v2-2/)

### 路径格式
```
/v2/{name}/manifests/{reference}
/v2/{name}/blobs/{digest}
/v2/{name}/blobs/uploads/{uuid}
```

### 已实现的端点

| 方法 | 路径 | 描述 | 仓库类型 |
|------|------|------|----------|
| GET | `/` 或 `/{repositoryName}/` | API版本检查 | 全部 |
| GET | `/_catalog` | 列出仓库 | 全部 |
| GET | `/{name}/tags/list` | 列出镜像标签 | 全部 |
| HEAD | `/{name}/manifests/{reference}` | 检查Manifest | 全部 |
| GET | `/{name}/manifests/{reference}` | 获取Manifest | 全部 |
| PUT | `/{name}/manifests/{reference}` | 上传Manifest | hosted |
| DELETE | `/{name}/manifests/{reference}` | 删除Manifest | hosted |
| HEAD | `/{name}/blobs/{digest}` | 检查Blob | 全部 |
| GET | `/{name}/blobs/{digest}` | 下载Blob | 全部 |
| DELETE | `/{name}/blobs/{digest}` | 删除Blob | hosted |
| POST | `/{name}/blobs/uploads` | 开始上传 | hosted |
| PATCH | `/{name}/blobs/uploads/{uuid}` | 上传数据块 | hosted |
| PUT | `/{name}/blobs/uploads/{uuid}?digest=` | 完成上传 | hosted |

### 协议合规性评审

**合规项：**
- ✅ Docker-Distribution-Api-Version头正确返回
- ✅ Docker-Content-Digest头支持
- ✅ Docker-Upload-UUID头支持
- ✅ 分块上传流程（POST→PATCH→PUT）
- ✅ Manifest V2 JSON内容类型
- ✅ Location头重定向

**改进建议：**
- ⚠️ `_catalog`端点返回硬编码空结果，应查询实际仓库列表
- ⚠️ `tags/list`返回硬编码空结果，应查询实际标签
- ⚠️ 未实现OCI Image Index/Manifest List支持（多架构镜像）
- ⚠️ 未实现content negotiation（Accept头处理）
- ⚠️ 完成上传时未处理已上传的chunk数据合并

---

## 3. NPM Protocol

### 规范参考
- [NPM Registry API](https://github.com/npm/registry/blob/master/docs/REGISTRY-API.md)
- [CommonJS Package Registry](https://wiki.commonjs.org/wiki/Packages/Registry)
- [npm CLI Documentation](https://docs.npmjs.com/cli)

### 路径格式
```
/npm/{repositoryName}/{packageName}
/npm/{repositoryName}/{packageName}/-/{filename}
/npm/{repositoryName}/-/v1/search
```

### 已实现的端点

| 方法 | 路径 | 描述 | 仓库类型 |
|------|------|------|----------|
| GET | `/{repositoryName}/{packageName}` | 获取包文档 | 全部 |
| GET | `/{repositoryName}/{packageName}/{version}` | 获取版本信息 | 全部 |
| GET | `/{repositoryName}/{packageName}/-/{filename}` | 下载tarball | 全部 |
| PUT | `/{repositoryName}/{packageName}` | 发布包 | hosted |
| DELETE | `/{repositoryName}/{packageName}/-rev/{rev}` | 取消发布 | hosted |
| PUT | `/{repositoryName}/-/package/{packageName}/dist-tags/{tag}` | 设置dist-tag | hosted |
| GET | `/{repositoryName}/-/v1/search` | 搜索包 | 全部 |

### 协议合规性评审

**合规项：**
- ✅ 包文档JSON结构
- ✅ tarball下载路径规范
- ✅ dist-tag管理
- ✅ search端点
- ✅ unpublish支持

**改进建议：**
- ⚠️ 未实现scoped package路径（`@scope%2Fpackage`，需URL编码）
- ⚠️ search端点返回硬编码空结果
- ⚠️ publish未解析请求体中的attachments（npm publish发送包含base64编码tarball的JSON）

---

## 4. PyPI Protocol

### 规范参考
- [PEP 503 - Simple Repository API](https://peps.python.org/pep-0503/)
- [PEP 691 - JSON Simple API](https://peps.python.org/pep-0691/)
- [PyPI Legacy Upload API](https://warehouse.pypa.io/api-reference/legacy.html)
- [PyPI JSON API](https://warehouse.pypa.io/api-reference/json.html)

### 路径格式
```
/pypi/{repositoryName}/simple/{packageName}/
/pypi/{repositoryName}/packages/{packageName}/{version}/{filename}
/pypi/{repositoryName}/pypi/{packageName}/json
```

### 已实现的端点

| 方法 | 路径 | 描述 | 仓库类型 |
|------|------|------|----------|
| GET | `/{repositoryName}/simple/` | 根索引页面 | 全部 |
| GET | `/{repositoryName}/simple/{packageName}/` | 包页面 | 全部 |
| POST | `/{repositoryName}/legacy/` | 上传包（twine） | hosted |
| GET | `/{repositoryName}/packages/{packageName}/{version}/{filename}` | 下载包 | 全部 |
| GET | `/{repositoryName}/pypi/{packageName}/json` | 包JSON元数据 | 全部 |
| GET | `/{repositoryName}/pypi/{packageName}/{version}/json` | 版本JSON元数据 | 全部 |

### 协议合规性评审

**合规项：**
- ✅ PEP 503 Simple Repository API
- ✅ 包名标准化（PEP 503 `re.sub(r"[-_.]+", "-", name).lower()`）
- ✅ Legacy上传API（multipart/form-data）
- ✅ JSON元数据API
- ✅ 文件名解析提取包名和版本

**改进建议：**
- ⚠️ 未实现PEP 691 JSON格式的Simple API
- ⚠️ Simple页面HTML应包含`data-requires-python`属性
- ⚠️ 上传缺少`:action`字段验证（twine发送`file_upload`）

---

## 5. NuGet Protocol

### 规范参考
- [NuGet V3 API](https://docs.microsoft.com/en-us/nuget/api/overview)
- [NuGet Service Index](https://docs.microsoft.com/en-us/nuget/api/service-index)
- [NuGet Search Query Service](https://docs.microsoft.com/en-us/nuget/api/search-query-service-resource)
- [NuGet Package Content](https://docs.microsoft.com/en-us/nuget/api/package-base-address-resource)

### 路径格式
```
/nuget/{repositoryName}/v3/index.json
/nuget/{repositoryName}/v3/flatcontainer/{id}/{version}/{id}.{version}.nupkg
```

### 已实现的端点

| 方法 | 路径 | 描述 | 仓库类型 |
|------|------|------|----------|
| GET | `/{repositoryName}/v3/index.json` | 服务索引 | 全部 |
| GET | `/{repositoryName}/v3/search` | 搜索包 | 全部 |
| GET | `/{repositoryName}/v3/registration/{id}/index.json` | 注册信息 | 全部 |
| GET | `/{repositoryName}/v3/flatcontainer/{id}/index.json` | 版本列表 | 全部 |
| GET | `/{repositoryName}/v3/flatcontainer/{id}/{version}/**` | 下载.nupkg | 全部 |
| PUT | `/{repositoryName}/api/v2/package` | 推送包 | hosted |
| DELETE | `/{repositoryName}/api/v2/package/{id}/{version}` | 删除包 | hosted |

### 协议合规性评审

**合规项：**
- ✅ V3 Service Index生成
- ✅ 包ID大小写不敏感（toLowerCase）
- ✅ Registration和Flat Container端点
- ✅ 动态baseUrl构建
- ✅ API Key认证支持（X-NuGet-ApiKey）

**改进建议：**
- ⚠️ search端点返回硬编码空结果
- ⚠️ push端点存储路径使用时间戳而非解析.nupkg元数据
- ⚠️ Service Index缺少`SearchAutocompleteService`和`SymbolPackagePublish`资源类型

---

## 6. Helm Protocol

### 规范参考
- [Helm Chart Repository](https://helm.sh/docs/topics/chart_repository/)
- [Chart Repository API](https://helm.sh/docs/topics/chart_repository/#the-chart-repository-structure)
- [ChartMuseum API](https://github.com/helm/chartmuseum#api)

### 路径格式
```
/helm/{repositoryName}/index.yaml
/helm/{repositoryName}/charts/{filename}
/helm/{repositoryName}/api/charts
```

### 已实现的端点

| 方法 | 路径 | 描述 | 仓库类型 |
|------|------|------|----------|
| GET | `/{repositoryName}/index.yaml` | 获取索引 | 全部 |
| GET | `/{repositoryName}/charts/{filename}` | 下载Chart | 全部 |
| POST | `/{repositoryName}/api/charts` | 上传Chart | hosted |
| DELETE | `/{repositoryName}/api/charts/{name}/{version}` | 删除Chart | hosted |

### 协议合规性评审

**合规项：**
- ✅ index.yaml索引文件生成
- ✅ Chart包下载（.tgz格式）
- ✅ ChartMuseum兼容的上传API
- ✅ 制品格式验证

**改进建议：**
- ⚠️ 未实现`GET /api/charts`（列出所有charts）
- ⚠️ 未实现`GET /api/charts/{name}`（获取chart所有版本）
- ⚠️ 未实现provenance文件支持（.prov）

---

## 7. Go Protocol

### 规范参考
- [Go Module Proxy Protocol](https://go.dev/ref/mod#goproxy-protocol)
- [Module Proxy](https://pkg.go.dev/cmd/go#hdr-Module_proxy_protocol)

### 路径格式
```
/go/{repositoryName}/{module}/@v/list
/go/{repositoryName}/{module}/@v/{version}.info
/go/{repositoryName}/{module}/@v/{version}.mod
/go/{repositoryName}/{module}/@v/{version}.zip
/go/{repositoryName}/{module}/@latest
```

### 已实现的端点

| 方法 | 路径 | 描述 | 仓库类型 |
|------|------|------|----------|
| GET | `/{repositoryName}/**/@v/list` | 版本列表 | 全部 |
| GET | `/{repositoryName}/**/@v/{version}.info` | 版本信息 | 全部 |
| GET | `/{repositoryName}/**/@v/{version}.mod` | go.mod文件 | 全部 |
| GET | `/{repositoryName}/**/@v/{version}.zip` | 模块源码 | 全部 |
| GET | `/{repositoryName}/**/@latest` | 最新版本 | 全部 |

### 协议合规性评审

**合规项：**
- ✅ 完整的GOPROXY协议端点
- ✅ 动态模块路径提取
- ✅ 正确的Content-Type（text/plain、application/json、application/zip）
- ✅ @latest端点支持

**改进建议：**
- ⚠️ 未实现大写字母编码（Go规范要求大写字母编码为`!{lowercase}`）
- ⚠️ 未实现`$GONOSUMCHECK`和`$GONOSUMDB`支持
- ⚠️ Go模块代理协议为只读，当前实现正确地只提供了GET端点

---

## 8. APT Protocol

### 规范参考
- [Debian Repository Format](https://wiki.debian.org/DebianRepository/Format)
- [APT Transport Protocol](https://manpages.debian.org/testing/apt/apt-transport-http.1.en.html)
- [Debian Policy Manual - Archives](https://www.debian.org/doc/debian-policy/ch-archive.html)

### 路径格式
```
/apt/{repositoryName}/dists/{distribution}/Release
/apt/{repositoryName}/dists/{distribution}/{component}/binary-{arch}/Packages
/apt/{repositoryName}/pool/{component}/{prefix}/{packageName}/{filename}
```

### 已实现的端点

| 方法 | 路径 | 描述 | 仓库类型 |
|------|------|------|----------|
| GET | `/{repositoryName}/dists/{distribution}/Release` | Release文件 | 全部 |
| GET | `/{repositoryName}/dists/{distribution}/InRelease` | 签名Release | 全部 |
| GET | `/{repositoryName}/dists/{distribution}/{component}/binary-{arch}/Packages` | 包索引 | 全部 |
| GET | `/{repositoryName}/dists/{distribution}/{component}/binary-{arch}/Packages.gz` | 压缩包索引 | 全部 |
| GET | `/{repositoryName}/pool/{component}/{prefix}/{packageName}/{filename}` | 下载.deb | 全部 |
| PUT | `/{repositoryName}/pool/{filename}` | 上传.deb | hosted |

### 协议合规性评审

**合规项：**
- ✅ 标准的Debian仓库目录结构（dists/pool）
- ✅ Release和InRelease文件
- ✅ Packages和Packages.gz索引
- ✅ 制品格式验证
- ✅ 正确的Content-Type（application/vnd.debian.binary-package）

**改进建议：**
- ⚠️ 未实现Release.gpg签名文件
- ⚠️ 未实现Sources索引（源码包支持）
- ⚠️ 上传路径未包含component/prefix/packageName结构化存储
- ⚠️ 缺少`DELETE`端点用于删除包

---

## 9. YUM Protocol

### 规范参考
- [YUM Repository](http://yum.baseurl.org/wiki/RepoCreate.html)
- [RPM Repository Metadata](https://linux.die.net/man/8/createrepo)
- [DNF Documentation](https://dnf.readthedocs.io/)

### 路径格式
```
/yum/{repositoryName}/repodata/repomd.xml
/yum/{repositoryName}/repodata/{filename}
/yum/{repositoryName}/Packages/{letter}/{filename}
```

### 已实现的端点

| 方法 | 路径 | 描述 | 仓库类型 |
|------|------|------|----------|
| GET | `/{repositoryName}/repodata/repomd.xml` | 元数据索引 | 全部 |
| GET | `/{repositoryName}/repodata/{filename}` | 元数据文件 | 全部 |
| GET | `/{repositoryName}/Packages/{letter}/{filename}` | 下载RPM | 全部 |
| PUT | `/{repositoryName}/upload` | 上传RPM | hosted |
| DELETE | `/{repositoryName}/Packages/{letter}/{filename}` | 删除RPM | hosted |

### 协议合规性评审

**合规项：**
- ✅ repomd.xml索引（支持生成和存储两种模式）
- ✅ repodata元数据目录
- ✅ 按首字母组织的Packages目录结构
- ✅ RPM文件格式验证
- ✅ 正确的Content-Type（application/x-rpm、application/gzip）

**改进建议：**
- ⚠️ 上传使用multipart而非PUT body（不影响功能但与Maven等协议不一致）
- ⚠️ 上传后未自动触发repodata重建（createrepo_c）
- ⚠️ 未实现GPG签名验证

---

## 10. Raw Protocol

### 规范参考

Raw协议没有标准规范参考，属于自定义通用文件存储协议。设计参考:
- [Sonatype Nexus Raw Repositories](https://help.sonatype.com/repomanager3/nexus-repository-administration/formats/raw-repositories)
- [JFrog Generic Repositories](https://jfrog.com/help/r/jfrog-artifactory-documentation/generic-repositories)

### 路径格式
```
/raw/{repositoryName}/{arbitrary-path}/{filename}
```

### 已实现的端点

| 方法 | 路径 | 描述 | 仓库类型 |
|------|------|------|----------|
| GET | `/{repositoryName}/**` | 下载文件 | 全部 |
| PUT | `/{repositoryName}/**` | 上传文件 | hosted |
| DELETE | `/{repositoryName}/**` | 删除文件 | hosted |
| HEAD | `/{repositoryName}/**` | 检查文件是否存在 | 全部 |

### 协议合规性评审

**合规项：**
- ✅ 支持任意路径和文件类型
- ✅ 丰富的Content-Type解析（17种文件类型）
- ✅ HEAD方法检查文件存在性
- ✅ hosted仓库写操作限制

**改进建议：**
- ⚠️ 缺少目录列表功能（类似Nexus的browse功能）
- ⚠️ 缺少Content-Disposition头设置（下载文件名提示）

---

## 通用评审结论

### 共性优点
1. **统一的架构模式** - 所有10个协议控制器遵循一致的架构：依赖注入、格式验证、仓库类型检查
2. **清晰的职责分离** - BlobStore负责存储抽象，FormatHandler负责格式处理，Controller负责协议路由
3. **完整的API文档** - 所有端点都包含Swagger注解（@Operation, @ApiResponse, @Parameter）
4. **安全的写操作控制** - 所有写操作（PUT/POST/DELETE）都执行hosted仓库类型校验

### 共性改进建议
1. **异常处理** - 应统一使用自定义业务异常替代`IllegalArgumentException`和`IllegalStateException`
2. **认证与授权** - 协议端点未集成认证中间件（如Docker的Bearer Token认证、NuGet的X-NuGet-ApiKey）
3. **审计日志** - 上传/删除操作未记录审计日志
4. **proxy仓库代理** - 未实现proxy仓库的远程代理和缓存功能
5. **group仓库聚合** - 未实现group仓库的多仓库聚合查询
