# Dashboard统计接口文档

## 概述
本文档定义了Dashboard仪表盘相关的RESTful API接口，包括总览统计、趋势图表、最近活动等功能。

---

## 1. 查询Dashboard总览统计

### 接口信息
- **接口路径**: `GET /api/v1/dashboard/overview`
- **接口描述**: 查询仪表盘总览统计数据
- **权限要求**: `DASHBOARD_VIEW`

### 请求参数
无

### 响应参数

#### DashboardOverviewVo
```java
public class DashboardOverviewVo {
    private StatCardVo repositories;        // 仓库统计
    private StatCardVo artifacts;           // 制品统计
    private StatCardVo downloads;           // 下载统计
    private StatCardVo storage;             // 存储统计
}
```

#### StatCardVo
```java
public class StatCardVo {
    private String title;
    private String value;                   // 当前值
    private String change;                  // 变化量
    private String trend;                   // up/down/stable
    private String icon;                    // 图标名称
}
```

### 响应示例
```json
{
  "code": "S",
  "message": "成功",
  "data": {
    "repositories": {
      "title": "总仓库数",
      "value": "18",
      "change": "+3",
      "trend": "up",
      "icon": "Package"
    },
    "artifacts": {
      "title": "总制品数",
      "value": "2,847",
      "change": "+124",
      "trend": "up",
      "icon": "Package2"
    },
    "downloads": {
      "title": "总下载量",
      "value": "45.2K",
      "change": "+18%",
      "trend": "up",
      "icon": "Download"
    },
    "storage": {
      "title": "存储使用",
      "value": "128.5 GB",
      "change": "+12.4 GB",
      "trend": "up",
      "icon": "HardDrive"
    }
  },
  "timestamp": "1737270600000"
}
```

---

## 2. 查询热门仓库

### 接口信息
- **接口路径**: `GET /api/v1/dashboard/top-repositories`
- **接口描述**: 查询最活跃的仓库（按下载量排序）
- **权限要求**: `DASHBOARD_VIEW`

### 请求参数

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| limit | Integer | 否 | 返回数量 | 5 |

### 响应参数

#### TopRepositoryListVo
```java
public class TopRepositoryListVo {
    private List<TopRepositoryVo> repositories;
}
```

#### TopRepositoryVo
```java
public class TopRepositoryVo {
    private String name;
    private String format;
    private String type;
    private Integer artifacts;
    private String size;
    private String status;
}
```

### 响应示例
```json
{
  "code": "S",
  "message": "成功",
  "data": {
    "repositories": [
      {
        "name": "maven-releases",
        "format": "Maven",
        "type": "hosted",
        "artifacts": 234,
        "size": "24.5 GB",
        "status": "online"
      }
    ]
  },
  "timestamp": "1737270600000"
}
```

---

## 3. 查询最近活动

### 接口信息
- **接口路径**: `GET /api/v1/dashboard/recent-activity`
- **接口描述**: 查询最近的系统活动
- **权限要求**: `DASHBOARD_VIEW`

### 请求参数

| 参数名 | 类型 | 必填 | 说明 | 默认值 |
|--------|------|------|------|--------|
| limit | Integer | 否 | 返回数量 | 5 |

### 响应参数

#### RecentActivityListVo
```java
public class RecentActivityListVo {
    private List<RecentActivityVo> activities;
}
```

#### RecentActivityVo
```java
public class RecentActivityVo {
    private String action;              // upload/download
    private String user;
    private String artifact;
    private String repository;
    private String time;                // 相对时间
    private String icon;
}
```

### 响应示例
```json
{
  "code": "S",
  "message": "成功",
  "data": {
    "activities": [
      {
        "action": "upload",
        "user": "Alex Chen",
        "artifact": "spring-boot-starter:2.7.0",
        "repository": "maven-releases",
        "time": "2 min ago",
        "icon": "Upload"
      }
    ]
  },
  "timestamp": "1737270600000"
}
```

---

## 4. 查询存储分布

### 接口信息
- **接口路径**: `GET /api/v1/dashboard/storage-distribution`
- **接口描述**: 查询各格式的存储使用分布
- **权限要求**: `DASHBOARD_VIEW`

### 请求参数
无

### 响应参数

#### StorageDistributionVo
```java
public class StorageDistributionVo {
    private List<StorageByFormatVo> distribution;
}
```

#### StorageByFormatVo
```java
public class StorageByFormatVo {
    private String format;              // Maven/Docker/NPM等
    private Double used;                // 已使用（GB）
    private Double total;               // 总配额（GB）
    private Integer percentage;         // 使用百分比
    private String color;               // 图表颜色
}
```

### 响应示例
```json
{
  "code": "S",
  "message": "成功",
  "data": {
    "distribution": [
      {
        "format": "Maven",
        "used": 55.7,
        "total": 100,
        "percentage": 56,
        "color": "bg-purple-500"
      },
      {
        "format": "Docker",
        "used": 42.3,
        "total": 100,
        "percentage": 42,
        "color": "bg-blue-500"
      }
    ]
  },
  "timestamp": "1737270600000"
}
```

---

## 5. 查询系统指标

### 接口信息
- **接口路径**: `GET /api/v1/dashboard/system-metrics`
- **接口描述**: 查询系统资源使用指标
- **权限要求**: `SYSTEM_VIEW`

### 请求参数
无

### 响应参数

#### SystemMetricsVo
```java
public class SystemMetricsVo {
    private List<MetricVo> metrics;
}
```

#### MetricVo
```java
public class MetricVo {
    private String name;                // CPU/Memory/Disk/Network
    private Integer value;              // 使用百分比
    private String status;              // normal/warning/critical
    private String icon;
    private String color;
    private String bgColor;
    private String barColor;
}
```

### 响应示例
```json
{
  "code": "S",
  "message": "成功",
  "data": {
    "metrics": [
      {
        "name": "CPU",
        "value": 45,
        "status": "normal",
        "icon": "Cpu",
        "color": "text-blue-600",
        "bgColor": "bg-blue-100",
        "barColor": "bg-blue-500"
      },
      {
        "name": "Memory",
        "value": 68,
        "status": "warning",
        "icon": "MemoryStick",
        "color": "text-orange-600",
        "bgColor": "bg-orange-100",
        "barColor": "bg-orange-500"
      }
    ]
  },
  "timestamp": "1737270600000"
}
```

---

## 数据库表设计

Dashboard接口主要聚合已有数据，不需要独立表。数据来源：
- 仓库统计 → repository表聚合
- 制品统计 → artifact表聚合
- 活动记录 → activity_log表查询
- 存储分布 → repository表按format分组统计
- 系统指标 → 系统监控API

---

## 错误码

| 错误码 | 说明 |
|--------|------|
| DASHBOARD_001 | Dashboard数据查询失败 |

---

## 接口遗漏检查

### 已实现功能
- ✅ 查询总览统计
- ✅ 查询热门仓库
- ✅ 查询最近活动
- ✅ 查询存储分布
- ✅ 查询系统指标

### 页面功能映射
- ✅ 统计卡片 → 查询总览统计
- ✅ 热门仓库列表 → 查询热门仓库
- ✅ 最近活动列表 → 查询最近活动
- ✅ 存储分布图表 → 查询存储分布
- ✅ 系统指标卡片 → 查询系统指标

---

## 注意事项

1. **缓存策略**: Dashboard数据访问频繁，建议缓存5分钟
2. **数据聚合**: 统计数据通过定时任务预聚合，避免实时查询
3. **权限控制**: 不同用户看到的统计数据范围不同
4. **性能优化**: 使用Redis缓存热点数据
