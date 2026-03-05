# 清理策略接口功能设计文档

## 概述

清理策略接口功能采用DDD（领域驱动设计）分层架构，严格遵循接口开发规范，实现制品仓库的自动化清理管理。系统支持多种清理类型、灵活的调度配置、完整的执行历史记录和统计分析，同时提供多租户隔离、审计功能和国际化支持。

## 架构设计

### 分层架构

```
┌─────────────────────────────────────────┐
│  接口层（Interfaces Layer）              │
│  - CleanupPolicyRest                     │
│  - CleanupPolicyFacade                   │
│  - DTO/VO                                │
│  - Assembler                             │
└─────────────────┬───────────────────────┘
                  │ 依赖
┌─────────────────▼───────────────────────┐
│  应用层（Application Layer）             │
│  - CleanupPolicyCmd（写操作）            │
│  - CleanupPolicyQuery（读操作）          │
│  - CleanupExecutionCmd                   │
│  - CleanupExecutionQuery                 │
│  - Converter                             │
└─────────────────┬───────────────────────┘
                  │ 依赖
┌─────────────────▼───────────────────────┐
│  领域层（Domain Layer）                  │
│  - CleanupPolicy（清理策略实体）         │
│  - CleanupExecution（执行记录实体）      │
│  - CleanupPolicyRepo（仓储接口）         │
│  - CleanupExecutionRepo（仓储接口）      │
│  - 枚举类型和值对象                      │
└─────────────────┬───────────────────────┘
                  │ 实现
┌─────────────────▼───────────────────────┐
│  基础设施层（Infrastructure Layer）      │
│  - MySQL/PostgreSQL持久化实现            │
│  - 搜索实现                               │
│  - 定时任务调度器                         │
│  - 工具类                                 │
└─────────────────────────────────────────┘
```

### 技术栈

- **框架**: Spring Boot + JPA
- **数据库**: MySQL 8.0+ / PostgreSQL 12+
- **搜索**: 支持全文搜索
- **调度**: Spring Task Scheduler
- **多租户**: 基于租户ID的数据隔离
- **审计**: Spring Data JPA Auditing
- **国际化**: Spring MessageSource

## 组件和接口

### 领域层组件

#### 1. 清理策略实体（CleanupPolicy）

```java
@Entity
@Table(name = "cleanup_policy")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class CleanupPolicy extends TenantAuditingEntity<CleanupPolicy, String> {

    @Id
    @Column(length = MAX_ID_LENGTH)
    private String id;

    @NotBlank
    @Column(nullable = false, length = MAX_NAME_LENGTH)
    private String name;

    @Column(length = MAX_DESC_LENGTH)
    private String description;

    @NotBlank
    @Column(name = "repository_id", nullable = false, length = MAX_ID_LENGTH)
    private String repositoryId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CleanupType type;

    @NotNull
    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(name = "dry_run")
    private Boolean dryRun = false;

    @Column(name = "last_executed")
    private LocalDateTime lastExecuted;

    @Column(name = "next_execution")
    private LocalDateTime nextExecution;

    @Column(name = "execution_count")
    private Integer executionCount = 0;

    // JSON字段
    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private CleanupCondition condition;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private CleanupSchedule schedule;

    @Type(JsonType.class)
    @Column(name = "last_execution_stats", columnDefinition = "json")
    private CleanupStatistics lastExecutionStats;

    // 非持久化字段 - 用于临时存储关联数据
    @Transient
    private String repositoryName;

    @Transient
    private UserInfo creator;

    @Transient
    private UserInfo modifier;

    @Override
    public String identity() {
        return this.id;
    }
}
```

#### 2. 清理执行记录实体（CleanupExecution）

```java
@Entity
@Table(name = "cleanup_execution")
@EntityListeners({TenantListener.class})
@Setter
@Getter
@Accessors(chain = true)
public class CleanupExecution extends TenantEntity<CleanupExecution, String> {

    @Id
    @Column(length = MAX_ID_LENGTH)
    private String id;

    @NotBlank
    @Column(name = "policy_id", nullable = false, length = MAX_ID_LENGTH)
    private String policyId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CleanupStatus status = CleanupStatus.PENDING;

    @Column
    private Integer progress = 0;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "error_message", length = MAX_DESC_LENGTH_X4)
    private String errorMessage;

    // JSON字段
    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private CleanupStatistics statistics;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    // 非持久化字段
    @Transient
    private String policyName;

    @Transient
    private Long durationSeconds;

    @Override
    public String identity() {
        return this.id;
    }

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }
}
```

#### 3. 值对象和枚举

```java
// 清理类型枚举
public enum CleanupType {
    BY_AGE("by_age", "按时间清理"),
    BY_COUNT("by_count", "按数量清理"),
    BY_SIZE("by_size", "按大小清理"),
    BY_PATTERN("by_pattern", "按模式清理");

    private final String value;
    private final String description;
}

// 清理状态枚举
public enum CleanupStatus {
    PENDING("pending", "等待执行"),
    RUNNING("running", "执行中"),
    COMPLETED("completed", "执行完成"),
    FAILED("failed", "执行失败"),
    CANCELLED("cancelled", "已取消");

    private final String value;
    private final String description;
}

// 调度类型枚举
public enum ScheduleType {
    ONCE("once", "执行一次"),
    DAILY("daily", "每日执行"),
    WEEKLY("weekly", "每周执行"),
    MONTHLY("monthly", "每月执行"),
    CRON("cron", "CRON表达式");

    private final String value;
    private final String description;
}

// 清理条件值对象
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CleanupCondition {
    private Integer olderThanDays;          // 超过N天未使用
    private Integer keepLastVersions;       // 保留最新N个版本
    private Long maxSizeBytes;              // 最大存储字节
    private String namePattern;             // 名称匹配模式（正则）
    private Integer minDownloads;           // 最小下载次数
    private List<String> excludePatterns;   // 排除模式列表
}

// 调度配置值对象
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CleanupSchedule {
    private ScheduleType type;
    private String cronExpression;
    private Integer intervalHours;          // 间隔小时数
    private LocalTime executeTime;          // 执行时间
}

// 清理统计值对象
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CleanupStatistics {
    private Integer deletedArtifacts = 0;
    private Long freedSpaceBytes = 0L;
    private String freedSpace;              // 格式化的空间大小
    private LocalDateTime executedAt;
    private Long durationSeconds = 0L;
    private Integer scannedArtifacts = 0;
    private Integer skippedArtifacts = 0;
    private List<String> deletedArtifactNames;
    private String errorDetails;
}
```

#### 4. 仓储接口

```java
// 清理策略仓储接口
@NoRepositoryBean
public interface CleanupPolicyRepo extends BaseRepository<CleanupPolicy, String> {

    // 基础查询方法
    List<CleanupPolicy> findByRepositoryId(String repositoryId);
    List<CleanupPolicy> findByEnabledTrue();
    Optional<CleanupPolicy> findByNameAndRepositoryId(String name, String repositoryId);
    boolean existsByNameAndRepositoryIdAndIdNot(String name, String repositoryId, String id);

    // 调度相关查询
    @Query("SELECT cp FROM CleanupPolicy cp WHERE cp.enabled = true " +
           "AND cp.nextExecution IS NOT NULL AND cp.nextExecution <= :now")
    List<CleanupPolicy> findPendingExecutions(@Param("now") LocalDateTime now);

    // 统计查询
    @Query("SELECT COUNT(cp) FROM CleanupPolicy cp WHERE cp.enabled = true")
    Long countEnabledPolicies();

    @Query("SELECT COUNT(cp) FROM CleanupPolicy cp")
    Long countTotalPolicies();

    // 批量更新下次执行时间
    @Modifying
    @Query("UPDATE CleanupPolicy cp SET cp.nextExecution = :nextExecution " +
           "WHERE cp.id = :policyId")
    void updateNextExecution(@Param("policyId") String policyId, 
                           @Param("nextExecution") LocalDateTime nextExecution);

    // 批量更新执行统计
    @Modifying
    @Query("UPDATE CleanupPolicy cp SET cp.lastExecuted = :lastExecuted, " +
           "cp.executionCount = cp.executionCount + 1, " +
           "cp.lastExecutionStats = :stats WHERE cp.id = :policyId")
    void updateExecutionStats(@Param("policyId") String policyId,
                            @Param("lastExecuted") LocalDateTime lastExecuted,
                            @Param("stats") CleanupStatistics stats);
}

// 清理策略搜索仓储接口（支持全文搜索）
@NoRepositoryBean
public interface CleanupPolicySearchRepo extends CustomBaseRepository<CleanupPolicy> {
    // 继承全文搜索能力
}

// 清理执行记录仓储接口
@NoRepositoryBean
public interface CleanupExecutionRepo extends BaseRepository<CleanupExecution, String> {

    // 基础查询方法
    List<CleanupExecution> findByPolicyIdOrderByCreatedDateDesc(String policyId);
    Page<CleanupExecution> findByPolicyIdOrderByCreatedDateDesc(String policyId, Pageable pageable);
    
    // 状态查询
    List<CleanupExecution> findByStatus(CleanupStatus status);
    List<CleanupExecution> findByPolicyIdAndStatus(String policyId, CleanupStatus status);
    boolean existsByPolicyIdAndStatusIn(String policyId, List<CleanupStatus> statuses);

    // 统计查询
    @Query("SELECT COUNT(ce) FROM CleanupExecution ce")
    Long countTotalExecutions();

    @Query("SELECT SUM(COALESCE(JSON_EXTRACT(ce.statistics, '$.deletedArtifacts'), 0)) " +
           "FROM CleanupExecution ce WHERE ce.status = 'COMPLETED'")
    Long sumDeletedArtifacts();

    @Query("SELECT SUM(COALESCE(JSON_EXTRACT(ce.statistics, '$.freedSpaceBytes'), 0)) " +
           "FROM CleanupExecution ce WHERE ce.status = 'COMPLETED'")
    Long sumFreedSpaceBytes();

    // 清理历史数据
    @Modifying
    @Query("DELETE FROM CleanupExecution ce WHERE ce.createdDate < :cutoffDate")
    void deleteOldExecutions(@Param("cutoffDate") LocalDateTime cutoffDate);
}
```

### 应用层组件

#### 1. 命令服务接口和实现

```java
// 清理策略命令服务接口
public interface CleanupPolicyCmd {
    CleanupPolicy create(CleanupPolicy policy);
    CleanupPolicy update(CleanupPolicy policy);
    CleanupPolicy updateEnabled(String id, Boolean enabled);
    void delete(String id);
    CleanupExecution executeImmediately(String id, Boolean dryRun);
}

// 清理策略命令服务实现
@Service
public class CleanupPolicyCmdImpl extends CommCmd<CleanupPolicy, String> implements CleanupPolicyCmd {

    @Resource
    private CleanupPolicyRepo cleanupPolicyRepo;

    @Resource
    private CleanupPolicyQuery cleanupPolicyQuery;

    @Resource
    private CleanupExecutionCmd cleanupExecutionCmd;

    @Resource
    private CleanupScheduler cleanupScheduler;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CleanupPolicy create(CleanupPolicy policy) {
        return new BizTemplate<CleanupPolicy>() {
            @Override
            protected void checkParams() {
                // 验证策略名称唯一性
                if (cleanupPolicyRepo.existsByNameAndRepositoryIdAndIdNot(
                        policy.getName(), policy.getRepositoryId(), "")) {
                    throw ProtocolException.of("策略名称「{0}」已存在", new Object[]{policy.getName()});
                }
                
                // 验证清理条件
                validateCleanupCondition(policy.getType(), policy.getCondition());
                
                // 验证调度配置
                validateScheduleConfiguration(policy.getSchedule());
            }

            @Override
            protected CleanupPolicy process() {
                // 设置初始值
                policy.setId(IdGenerator.nextId());
                policy.setExecutionCount(0);
                
                // 计算下次执行时间
                if (policy.getEnabled() && policy.getSchedule() != null) {
                    LocalDateTime nextExecution = cleanupScheduler.calculateNextExecution(policy.getSchedule());
                    policy.setNextExecution(nextExecution);
                }
                
                // 保存策略
                CleanupPolicy saved = insert(policy);
                
                // 注册到调度器
                if (saved.getEnabled()) {
                    cleanupScheduler.schedulePolicy(saved);
                }
                
                return saved;
            }
        }.execute();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CleanupPolicy update(CleanupPolicy policy) {
        return new BizTemplate<CleanupPolicy>() {
            CleanupPolicy existing;

            @Override
            protected void checkParams() {
                existing = cleanupPolicyQuery.findAndCheck(policy.getId());
                
                // 验证策略名称唯一性
                if (!existing.getName().equals(policy.getName()) &&
                    cleanupPolicyRepo.existsByNameAndRepositoryIdAndIdNot(
                        policy.getName(), policy.getRepositoryId(), policy.getId())) {
                    throw ProtocolException.of("策略名称「{0}」已存在", new Object[]{policy.getName()});
                }
                
                // 验证清理条件
                validateCleanupCondition(policy.getType(), policy.getCondition());
                
                // 验证调度配置
                validateScheduleConfiguration(policy.getSchedule());
            }

            @Override
            protected CleanupPolicy process() {
                // 更新字段
                existing.setName(policy.getName());
                existing.setDescription(policy.getDescription());
                existing.setType(policy.getType());
                existing.setCondition(policy.getCondition());
                existing.setSchedule(policy.getSchedule());
                existing.setDryRun(policy.getDryRun());
                
                // 重新计算下次执行时间
                if (existing.getEnabled() && existing.getSchedule() != null) {
                    LocalDateTime nextExecution = cleanupScheduler.calculateNextExecution(existing.getSchedule());
                    existing.setNextExecution(nextExecution);
                }
                
                // 保存更新
                CleanupPolicy updated = cleanupPolicyRepo.save(existing);
                
                // 更新调度器
                cleanupScheduler.reschedulePolicy(updated);
                
                return updated;
            }
        }.execute();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CleanupExecution executeImmediately(String id, Boolean dryRun) {
        return new BizTemplate<CleanupExecution>() {
            CleanupPolicy policy;

            @Override
            protected void checkParams() {
                policy = cleanupPolicyQuery.findAndCheck(id);
                
                // 检查是否正在执行
                if (cleanupExecutionCmd.isExecuting(id)) {
                    throw ProtocolException.of("策略「{0}」正在执行中，请稍后再试", new Object[]{policy.getName()});
                }
            }

            @Override
            protected CleanupExecution process() {
                // 创建执行记录
                CleanupExecution execution = new CleanupExecution();
                execution.setId(IdGenerator.nextId());
                execution.setPolicyId(policy.getId());
                execution.setStatus(CleanupStatus.PENDING);
                execution.setProgress(0);
                
                // 保存执行记录
                execution = cleanupExecutionCmd.create(execution);
                
                // 异步执行清理
                cleanupScheduler.executePolicy(policy, execution, dryRun != null ? dryRun : policy.getDryRun());
                
                return execution;
            }
        }.execute();
    }

    private void validateCleanupCondition(CleanupType type, CleanupCondition condition) {
        if (condition == null) {
            throw ProtocolException.of("清理条件不能为空");
        }
        
        switch (type) {
            case BY_AGE:
                if (condition.getOlderThanDays() == null || condition.getOlderThanDays() <= 0) {
                    throw ProtocolException.of("按时间清理必须指定有效的天数");
                }
                break;
            case BY_COUNT:
                if (condition.getKeepLastVersions() == null || condition.getKeepLastVersions() <= 0) {
                    throw ProtocolException.of("按数量清理必须指定保留的版本数");
                }
                break;
            case BY_SIZE:
                if (condition.getMaxSizeBytes() == null || condition.getMaxSizeBytes() <= 0) {
                    throw ProtocolException.of("按大小清理必须指定最大存储大小");
                }
                break;
            case BY_PATTERN:
                if (StringUtils.isBlank(condition.getNamePattern())) {
                    throw ProtocolException.of("按模式清理必须指定名称匹配模式");
                }
                // 验证正则表达式
                try {
                    Pattern.compile(condition.getNamePattern());
                } catch (PatternSyntaxException e) {
                    throw ProtocolException.of("名称匹配模式格式错误：{0}", new Object[]{e.getMessage()});
                }
                break;
        }
    }

    private void validateScheduleConfiguration(CleanupSchedule schedule) {
        if (schedule == null || schedule.getType() == null) {
            throw ProtocolException.of("调度配置不能为空");
        }
        
        if (schedule.getType() == ScheduleType.CRON) {
            if (StringUtils.isBlank(schedule.getCronExpression())) {
                throw ProtocolException.of("CRON调度必须指定CRON表达式");
            }
            // 验证CRON表达式
            try {
                CronExpression.parse(schedule.getCronExpression());
            } catch (Exception e) {
                throw ProtocolException.of("CRON表达式格式错误：{0}", new Object[]{e.getMessage()});
            }
        }
    }

    @Override
    protected BaseRepository<CleanupPolicy, String> getRepository() {
        return this.cleanupPolicyRepo;
    }
}

// 清理执行命令服务接口
public interface CleanupExecutionCmd {
    CleanupExecution create(CleanupExecution execution);
    CleanupExecution updateStatus(String id, CleanupStatus status, Integer progress);
    CleanupExecution complete(String id, CleanupStatistics statistics);
    CleanupExecution fail(String id, String errorMessage);
    boolean isExecuting(String policyId);
    void cleanupOldExecutions(int keepDays);
}
```

#### 2. 查询服务接口和实现

```java
// 清理策略查询服务接口
public interface CleanupPolicyQuery {
    CleanupPolicy findAndCheck(String id);
    CleanupPolicy detail(String id);
    Page<CleanupPolicy> list(GenericSpecification<CleanupPolicy> spec, Pageable pageable,
                           boolean fullTextSearch, String[] match);
    CleanupOverallStatisticsVo getStatistics();
    void assembleDetailInfos(List<CleanupPolicy> policies);
}

// 清理策略查询服务实现
@Service
public class CleanupPolicyQueryImpl implements CleanupPolicyQuery {

    @Resource
    private CleanupPolicyRepo cleanupPolicyRepo;

    @Resource
    private CleanupPolicySearchRepo cleanupPolicySearchRepo;

    @Resource
    private CleanupExecutionRepo cleanupExecutionRepo;

    @Resource
    private UserManager userManager;

    @Resource
    private RepositoryQuery repositoryQuery;

    @Override
    public CleanupPolicy findAndCheck(String id) {
        return new BizTemplate<CleanupPolicy>() {
            @Override
            protected CleanupPolicy process() {
                return cleanupPolicyRepo.findById(id)
                    .orElseThrow(() -> ResourceNotFound.of("清理策略「{0}」不存在", new Object[]{id}));
            }
        }.execute();
    }

    @Override
    public CleanupPolicy detail(String id) {
        return new BizTemplate<CleanupPolicy>() {
            CleanupPolicy policy;

            @Override
            protected void checkParams() {
                policy = findAndCheck(id);
            }

            @Override
            protected CleanupPolicy process() {
                // 组装关联信息
                assembleDetailInfos(List.of(policy));
                return policy;
            }
        }.execute();
    }

    @Override
    public Page<CleanupPolicy> list(GenericSpecification<CleanupPolicy> spec, Pageable pageable,
                                  boolean fullTextSearch, String[] match) {
        return new BizTemplate<Page<CleanupPolicy>>() {
            @Override
            protected Page<CleanupPolicy> process() {
                // 根据是否全文搜索选择不同的仓储
                Page<CleanupPolicy> page = fullTextSearch
                    ? cleanupPolicySearchRepo.find(spec.getCriteria(), pageable, CleanupPolicy.class, match)
                    : cleanupPolicyRepo.findAll(spec, pageable);
                
                // 批量设置关联数据
                if (!page.isEmpty()) {
                    assembleDetailInfos(page.getContent());
                }
                
                return page;
            }
        }.execute();
    }

    @Override
    public CleanupOverallStatisticsVo getStatistics() {
        return new BizTemplate<CleanupOverallStatisticsVo>() {
            @Override
            protected CleanupOverallStatisticsVo process() {
                CleanupOverallStatisticsVo stats = new CleanupOverallStatisticsVo();
                
                // 策略统计
                stats.setTotalPolicies(cleanupPolicyRepo.countTotalPolicies());
                stats.setEnabledPolicies(cleanupPolicyRepo.countEnabledPolicies());
                
                // 执行统计
                stats.setTotalExecutions(cleanupExecutionRepo.countTotalExecutions());
                stats.setTotalDeletedArtifacts(cleanupExecutionRepo.sumDeletedArtifacts());
                
                // 释放空间统计
                Long freedSpaceBytes = cleanupExecutionRepo.sumFreedSpaceBytes();
                stats.setTotalFreedSpaceBytes(freedSpaceBytes);
                stats.setTotalFreedSpace(FileUtils.formatFileSize(freedSpaceBytes));
                
                // 清理趋势数据（最近30天）
                stats.setCleanupTrend(getCleanupTrend(30));
                
                return stats;
            }
        }.execute();
    }

    @Override
    public void assembleDetailInfos(List<CleanupPolicy> policies) {
        if (policies.isEmpty()) {
            return;
        }
        
        // 获取用户信息
        Set<Long> userIds = new HashSet<>();
        policies.forEach(policy -> {
            if (policy.getCreatedBy() != null) {
                userIds.add(policy.getCreatedBy());
            }
            if (policy.getModifiedBy() != null) {
                userIds.add(policy.getModifiedBy());
            }
        });
        
        Map<Long, UserInfo> userInfoMap = userManager.getUserInfoMapByIds(userIds);
        
        // 获取仓库信息
        Set<String> repositoryIds = policies.stream()
            .map(CleanupPolicy::getRepositoryId)
            .collect(Collectors.toSet());
        
        Map<String, String> repositoryNameMap = repositoryQuery.getRepositoryNameMapByIds(repositoryIds);
        
        // 批量组装详情信息
        for (CleanupPolicy policy : policies) {
            if (policy.getCreatedBy() != null) {
                policy.setCreator(userInfoMap.get(policy.getCreatedBy()));
            }
            if (policy.getModifiedBy() != null) {
                policy.setModifier(userInfoMap.get(policy.getModifiedBy()));
            }
            policy.setRepositoryName(repositoryNameMap.get(policy.getRepositoryId()));
        }
    }

    private List<CleanupTrendVo> getCleanupTrend(int days) {
        // 实现清理趋势统计逻辑
        // 返回最近N天的清理统计数据
        return Collections.emptyList(); // 简化实现
    }
}

// 清理执行查询服务接口
public interface CleanupExecutionQuery {
    CleanupExecution findAndCheck(String id);
    Page<CleanupExecution> listByPolicy(String policyId, Pageable pageable);
    void assembleDetailInfos(List<CleanupExecution> executions);
}
```

### 接口层组件

#### 1. DTO定义

```java
// 创建清理策略DTO
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "创建清理策略请求参数")
public class CleanupPolicyCreateDto {

    @NotBlank
    @Size(max = MAX_NAME_LENGTH)
    @Schema(description = "策略名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Size(max = MAX_DESC_LENGTH)
    @Schema(description = "策略描述")
    private String description;

    @NotBlank
    @Size(max = MAX_ID_LENGTH)
    @Schema(description = "仓库ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String repositoryId;

    @NotNull
    @Schema(description = "清理类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private CleanupType type;

    @NotNull
    @Valid
    @Schema(description = "清理条件", requiredMode = Schema.RequiredMode.REQUIRED)
    private CleanupConditionDto condition;

    @NotNull
    @Valid
    @Schema(description = "调度配置", requiredMode = Schema.RequiredMode.REQUIRED)
    private CleanupScheduleDto schedule;

    @Schema(description = "是否启用", defaultValue = "true")
    private Boolean enabled = true;

    @Schema(description = "是否试运行", defaultValue = "false")
    private Boolean dryRun = false;
}

// 更新清理策略DTO
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "更新清理策略请求参数")
public class CleanupPolicyUpdateDto {

    @NotBlank
    @Size(max = MAX_NAME_LENGTH)
    @Schema(description = "策略名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Size(max = MAX_DESC_LENGTH)
    @Schema(description = "策略描述")
    private String description;

    @NotNull
    @Schema(description = "清理类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private CleanupType type;

    @NotNull
    @Valid
    @Schema(description = "清理条件", requiredMode = Schema.RequiredMode.REQUIRED)
    private CleanupConditionDto condition;

    @NotNull
    @Valid
    @Schema(description = "调度配置", requiredMode = Schema.RequiredMode.REQUIRED)
    private CleanupScheduleDto schedule;

    @Schema(description = "是否试运行", defaultValue = "false")
    private Boolean dryRun = false;
}

// 查询清理策略DTO
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "查询清理策略请求参数")
public class CleanupPolicyFindDto extends PageQuery {

    @Schema(description = "策略名称")
    private String name;

    @Schema(description = "仓库ID")
    private String repositoryId;

    @Schema(description = "清理类型")
    private CleanupType type;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Override
    public String getDefaultOrderBy() {
        return "createdDate";
    }
}

// 清理条件DTO
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "清理条件")
public class CleanupConditionDto {

    @Min(1)
    @Max(3650)
    @Schema(description = "超过N天未使用（1-3650天）")
    private Integer olderThanDays;

    @Min(1)
    @Max(1000)
    @Schema(description = "保留最新N个版本（1-1000个）")
    private Integer keepLastVersions;

    @Min(1)
    @Schema(description = "最大存储字节数")
    private Long maxSizeBytes;

    @Size(max = MAX_DESC_LENGTH)
    @Schema(description = "名称匹配模式（正则表达式）")
    private String namePattern;

    @Min(0)
    @Schema(description = "最小下载次数")
    private Integer minDownloads;

    @Size(max = MAX_PARAM_SIZE)
    @Schema(description = "排除模式列表")
    private List<String> excludePatterns;
}

// 调度配置DTO
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "调度配置")
public class CleanupScheduleDto {

    @NotNull
    @Schema(description = "调度类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private ScheduleType type;

    @Size(max = MAX_CODE_LENGTH)
    @Schema(description = "CRON表达式（调度类型为CRON时必填）")
    private String cronExpression;

    @Min(1)
    @Max(8760)
    @Schema(description = "间隔小时数（1-8760小时）")
    private Integer intervalHours;

    @Schema(description = "执行时间")
    private LocalTime executeTime;
}

// 启用禁用DTO
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "启用禁用策略请求参数")
public class CleanupPolicyEnabledDto {

    @NotNull
    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean enabled;
}

// 立即执行DTO
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "立即执行策略请求参数")
public class CleanupPolicyExecuteDto {

    @Schema(description = "是否试运行", defaultValue = "false")
    private Boolean dryRun = false;
}

// 查询执行历史DTO
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "查询执行历史请求参数")
public class CleanupExecutionFindDto extends PageQuery {

    @Schema(description = "执行状态")
    private CleanupStatus status;

    @Override
    public String getDefaultOrderBy() {
        return "createdDate";
    }
}
```

#### 2. VO定义

```java
// 清理策略详情VO
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "清理策略详情")
public class CleanupPolicyDetailVo extends TenantAuditingVo {

    @Schema(description = "策略ID")
    private String id;

    @Schema(description = "策略名称")
    private String name;

    @Schema(description = "策略描述")
    private String description;

    @Schema(description = "仓库ID")
    private String repositoryId;

    @Schema(description = "仓库名称")
    private String repositoryName;

    @Schema(description = "清理类型")
    private CleanupType type;

    @Schema(description = "清理条件")
    private CleanupConditionVo condition;

    @Schema(description = "调度配置")
    private CleanupScheduleVo schedule;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "是否试运行")
    private Boolean dryRun;

    @Schema(description = "最后执行时间")
    private LocalDateTime lastExecuted;

    @Schema(description = "下次执行时间")
    private LocalDateTime nextExecution;

    @Schema(description = "执行次数")
    private Integer executionCount;

    @Schema(description = "最后执行统计")
    private CleanupStatisticsVo lastExecutionStats;
}

// 清理策略列表VO
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "清理策略列表项")
public class CleanupPolicyListVo extends TenantAuditingVo {

    @Schema(description = "策略ID")
    private String id;

    @Schema(description = "策略名称")
    private String name;

    @Schema(description = "策略描述")
    private String description;

    @Schema(description = "仓库名称")
    private String repositoryName;

    @Schema(description = "清理类型")
    private CleanupType type;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "最后执行时间")
    private LocalDateTime lastExecuted;

    @Schema(description = "下次执行时间")
    private LocalDateTime nextExecution;

    @Schema(description = "执行次数")
    private Integer executionCount;

    @Schema(description = "最后执行统计")
    private CleanupStatisticsVo lastExecutionStats;
}

// 清理执行记录VO
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "清理执行记录")
public class CleanupExecutionVo {

    @Schema(description = "执行ID")
    private String id;

    @Schema(description = "策略ID")
    private String policyId;

    @Schema(description = "策略名称")
    private String policyName;

    @Schema(description = "执行状态")
    private CleanupStatus status;

    @Schema(description = "执行进度（0-100）")
    private Integer progress;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "执行时长（秒）")
    private Long durationSeconds;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "执行统计")
    private CleanupStatisticsVo statistics;

    @Schema(description = "创建时间")
    private LocalDateTime createdDate;
}

// 清理统计VO
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "清理统计信息")
public class CleanupStatisticsVo {

    @Schema(description = "删除制品数量")
    private Integer deletedArtifacts;

    @Schema(description = "释放空间（格式化）")
    private String freedSpace;

    @Schema(description = "释放空间字节数")
    private Long freedSpaceBytes;

    @Schema(description = "执行时间")
    private LocalDateTime executedAt;

    @Schema(description = "执行时长（秒）")
    private Long durationSeconds;

    @Schema(description = "扫描制品数量")
    private Integer scannedArtifacts;

    @Schema(description = "跳过制品数量")
    private Integer skippedArtifacts;

    @Schema(description = "删除的制品名称列表")
    private List<String> deletedArtifactNames;
}

// 清理条件VO
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "清理条件")
public class CleanupConditionVo {

    @Schema(description = "超过N天未使用")
    private Integer olderThanDays;

    @Schema(description = "保留最新N个版本")
    private Integer keepLastVersions;

    @Schema(description = "最大存储字节数")
    private Long maxSizeBytes;

    @Schema(description = "名称匹配模式")
    private String namePattern;

    @Schema(description = "最小下载次数")
    private Integer minDownloads;

    @Schema(description = "排除模式列表")
    private List<String> excludePatterns;
}

// 调度配置VO
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "调度配置")
public class CleanupScheduleVo {

    @Schema(description = "调度类型")
    private ScheduleType type;

    @Schema(description = "CRON表达式")
    private String cronExpression;

    @Schema(description = "间隔小时数")
    private Integer intervalHours;

    @Schema(description = "执行时间")
    private LocalTime executeTime;
}

// 整体统计VO
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "清理整体统计")
public class CleanupOverallStatisticsVo {

    @Schema(description = "策略总数")
    private Long totalPolicies;

    @Schema(description = "启用策略数")
    private Long enabledPolicies;

    @Schema(description = "执行总次数")
    private Long totalExecutions;

    @Schema(description = "删除制品总数")
    private Long totalDeletedArtifacts;

    @Schema(description = "释放空间总计（格式化）")
    private String totalFreedSpace;

    @Schema(description = "释放空间总字节数")
    private Long totalFreedSpaceBytes;

    @Schema(description = "清理趋势数据")
    private List<CleanupTrendVo> cleanupTrend;
}

// 清理趋势VO
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "清理趋势数据")
public class CleanupTrendVo {

    @Schema(description = "日期")
    private LocalDate date;

    @Schema(description = "当日删除制品数")
    private Integer deletedArtifacts;

    @Schema(description = "当日释放空间字节数")
    private Long freedSpaceBytes;

    @Schema(description = "当日释放空间（格式化）")
    private String freedSpace;
}
```

#### 3. Assembler转换器

```java
public class CleanupPolicyAssembler {

    // DTO → Domain（创建）
    public static CleanupPolicy toCreateDomain(CleanupPolicyCreateDto dto) {
        CleanupPolicy policy = new CleanupPolicy();
        policy.setName(dto.getName());
        policy.setDescription(dto.getDescription());
        policy.setRepositoryId(dto.getRepositoryId());
        policy.setType(dto.getType());
        policy.setEnabled(dto.getEnabled() != null ? dto.getEnabled() : true);
        policy.setDryRun(dto.getDryRun() != null ? dto.getDryRun() : false);
        
        // 转换清理条件
        if (dto.getCondition() != null) {
            policy.setCondition(toCleanupCondition(dto.getCondition()));
        }
        
        // 转换调度配置
        if (dto.getSchedule() != null) {
            policy.setSchedule(toCleanupSchedule(dto.getSchedule()));
        }
        
        return policy;
    }

    // DTO → Domain（更新）
    public static CleanupPolicy toUpdateDomain(CleanupPolicyUpdateDto dto, String id) {
        CleanupPolicy policy = new CleanupPolicy();
        policy.setId(id);
        policy.setName(dto.getName());
        policy.setDescription(dto.getDescription());
        policy.setType(dto.getType());
        policy.setDryRun(dto.getDryRun() != null ? dto.getDryRun() : false);
        
        // 转换清理条件
        if (dto.getCondition() != null) {
            policy.setCondition(toCleanupCondition(dto.getCondition()));
        }
        
        // 转换调度配置
        if (dto.getSchedule() != null) {
            policy.setSchedule(toCleanupSchedule(dto.getSchedule()));
        }
        
        return policy;
    }

    // Domain → 详情VO
    public static CleanupPolicyDetailVo toDetailVo(CleanupPolicy policy) {
        CleanupPolicyDetailVo vo = new CleanupPolicyDetailVo();
        vo.setId(policy.getId());
        vo.setName(policy.getName());
        vo.setDescription(policy.getDescription());
        vo.setRepositoryId(policy.getRepositoryId());
        vo.setRepositoryName(policy.getRepositoryName());
        vo.setType(policy.getType());
        vo.setEnabled(policy.getEnabled());
        vo.setDryRun(policy.getDryRun());
        vo.setLastExecuted(policy.getLastExecuted());
        vo.setNextExecution(policy.getNextExecution());
        vo.setExecutionCount(policy.getExecutionCount());
        
        // 转换清理条件
        if (policy.getCondition() != null) {
            vo.setCondition(toCleanupConditionVo(policy.getCondition()));
        }
        
        // 转换调度配置
        if (policy.getSchedule() != null) {
            vo.setSchedule(toCleanupScheduleVo(policy.getSchedule()));
        }
        
        // 转换执行统计
        if (policy.getLastExecutionStats() != null) {
            vo.setLastExecutionStats(toCleanupStatisticsVo(policy.getLastExecutionStats()));
        }
        
        // 设置审计信息
        vo.setTenantId(policy.getTenantId());
        vo.setCreatedBy(policy.getCreatedBy());
        vo.setCreatedDate(policy.getCreatedDate());
        vo.setModifiedBy(policy.getModifiedBy());
        vo.setModifiedDate(policy.getModifiedDate());
        
        return vo;
    }

    // Domain → 列表VO
    public static CleanupPolicyListVo toListVo(CleanupPolicy policy) {
        CleanupPolicyListVo vo = new CleanupPolicyListVo();
        vo.setId(policy.getId());
        vo.setName(policy.getName());
        vo.setDescription(policy.getDescription());
        vo.setRepositoryName(policy.getRepositoryName());
        vo.setType(policy.getType());
        vo.setEnabled(policy.getEnabled());
        vo.setLastExecuted(policy.getLastExecuted());
        vo.setNextExecution(policy.getNextExecution());
        vo.setExecutionCount(policy.getExecutionCount());
        
        // 转换执行统计
        if (policy.getLastExecutionStats() != null) {
            vo.setLastExecutionStats(toCleanupStatisticsVo(policy.getLastExecutionStats()));
        }
        
        // 设置审计信息
        vo.setTenantId(policy.getTenantId());
        vo.setCreatedBy(policy.getCreatedBy());
        vo.setCreatedDate(policy.getCreatedDate());
        vo.setModifiedBy(policy.getModifiedBy());
        vo.setModifiedDate(policy.getModifiedDate());
        
        return vo;
    }

    // DTO → 查询条件
    public static GenericSpecification<CleanupPolicy> getSpecification(CleanupPolicyFindDto dto) {
        Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
            .matchSearchFields("name", "description")      // 全文搜索字段
            .rangeSearchFields("id", "createdDate")        // 范围查询字段
            .orderByFields("id", "createdDate", "name", "lastExecuted")  // 排序字段
            .build();
        return new GenericSpecification<>(filters);
    }

    // 辅助转换方法
    private static CleanupCondition toCleanupCondition(CleanupConditionDto dto) {
        CleanupCondition condition = new CleanupCondition();
        condition.setOlderThanDays(dto.getOlderThanDays());
        condition.setKeepLastVersions(dto.getKeepLastVersions());
        condition.setMaxSizeBytes(dto.getMaxSizeBytes());
        condition.setNamePattern(dto.getNamePattern());
        condition.setMinDownloads(dto.getMinDownloads());
        condition.setExcludePatterns(dto.getExcludePatterns());
        return condition;
    }

    private static CleanupSchedule toCleanupSchedule(CleanupScheduleDto dto) {
        CleanupSchedule schedule = new CleanupSchedule();
        schedule.setType(dto.getType());
        schedule.setCronExpression(dto.getCronExpression());
        schedule.setIntervalHours(dto.getIntervalHours());
        schedule.setExecuteTime(dto.getExecuteTime());
        return schedule;
    }

    private static CleanupConditionVo toCleanupConditionVo(CleanupCondition condition) {
        CleanupConditionVo vo = new CleanupConditionVo();
        vo.setOlderThanDays(condition.getOlderThanDays());
        vo.setKeepLastVersions(condition.getKeepLastVersions());
        vo.setMaxSizeBytes(condition.getMaxSizeBytes());
        vo.setNamePattern(condition.getNamePattern());
        vo.setMinDownloads(condition.getMinDownloads());
        vo.setExcludePatterns(condition.getExcludePatterns());
        return vo;
    }

    private static CleanupScheduleVo toCleanupScheduleVo(CleanupSchedule schedule) {
        CleanupScheduleVo vo = new CleanupScheduleVo();
        vo.setType(schedule.getType());
        vo.setCronExpression(schedule.getCronExpression());
        vo.setIntervalHours(schedule.getIntervalHours());
        vo.setExecuteTime(schedule.getExecuteTime());
        return vo;
    }

    private static CleanupStatisticsVo toCleanupStatisticsVo(CleanupStatistics statistics) {
        CleanupStatisticsVo vo = new CleanupStatisticsVo();
        vo.setDeletedArtifacts(statistics.getDeletedArtifacts());
        vo.setFreedSpace(statistics.getFreedSpace());
        vo.setFreedSpaceBytes(statistics.getFreedSpaceBytes());
        vo.setExecutedAt(statistics.getExecutedAt());
        vo.setDurationSeconds(statistics.getDurationSeconds());
        vo.setScannedArtifacts(statistics.getScannedArtifacts());
        vo.setSkippedArtifacts(statistics.getSkippedArtifacts());
        vo.setDeletedArtifactNames(statistics.getDeletedArtifactNames());
        return vo;
    }
}

// 清理执行记录转换器
public class CleanupExecutionAssembler {

    // Domain → VO
    public static CleanupExecutionVo toVo(CleanupExecution execution) {
        CleanupExecutionVo vo = new CleanupExecutionVo();
        vo.setId(execution.getId());
        vo.setPolicyId(execution.getPolicyId());
        vo.setPolicyName(execution.getPolicyName());
        vo.setStatus(execution.getStatus());
        vo.setProgress(execution.getProgress());
        vo.setStartTime(execution.getStartTime());
        vo.setEndTime(execution.getEndTime());
        vo.setErrorMessage(execution.getErrorMessage());
        vo.setCreatedDate(execution.getCreatedDate());
        
        // 计算执行时长
        if (execution.getStartTime() != null && execution.getEndTime() != null) {
            vo.setDurationSeconds(Duration.between(execution.getStartTime(), execution.getEndTime()).getSeconds());
        }
        
        // 转换执行统计
        if (execution.getStatistics() != null) {
            vo.setStatistics(CleanupPolicyAssembler.toCleanupStatisticsVo(execution.getStatistics()));
        }
        
        return vo;
    }

    // DTO → 查询条件
    public static GenericSpecification<CleanupExecution> getSpecification(CleanupExecutionFindDto dto) {
        Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
            .rangeSearchFields("id", "createdDate")        // 范围查询字段
            .orderByFields("id", "createdDate", "startTime")  // 排序字段
            .build();
        return new GenericSpecification<>(filters);
    }
}
```

## 数据模型

### 数据库表设计

#### 1. 清理策略表（cleanup_policy）

```sql
CREATE TABLE cleanup_policy (
    id VARCHAR(20) PRIMARY KEY COMMENT '策略ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    name VARCHAR(100) NOT NULL COMMENT '策略名称',
    description VARCHAR(200) COMMENT '策略描述',
    repository_id VARCHAR(20) NOT NULL COMMENT '仓库ID',
    type VARCHAR(20) NOT NULL COMMENT '清理类型：BY_AGE,BY_COUNT,BY_SIZE,BY_PATTERN',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    dry_run BOOLEAN DEFAULT FALSE COMMENT '是否试运行',
    last_executed TIMESTAMP COMMENT '最后执行时间',
    next_execution TIMESTAMP COMMENT '下次执行时间',
    execution_count INT DEFAULT 0 COMMENT '执行次数',
    
    -- JSON字段
    condition JSON COMMENT '清理条件配置',
    schedule JSON COMMENT '调度配置',
    last_execution_stats JSON COMMENT '最后执行统计',
    
    -- 审计字段
    created_by BIGINT NOT NULL COMMENT '创建人ID',
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    modified_by BIGINT NOT NULL COMMENT '修改人ID',
    modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    
    -- 索引
    INDEX idx_tenant_repository (tenant_id, repository_id),
    INDEX idx_enabled_next_execution (enabled, next_execution),
    INDEX idx_created_date (created_date DESC),
    
    -- 唯一约束
    UNIQUE KEY uk_tenant_repository_name (tenant_id, repository_id, name),
    
    -- 外键约束
    FOREIGN KEY (repository_id) REFERENCES repository(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='清理策略表';
```

#### 2. 清理执行记录表（cleanup_execution）

```sql
CREATE TABLE cleanup_execution (
    id VARCHAR(20) PRIMARY KEY COMMENT '执行ID',
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    policy_id VARCHAR(20) NOT NULL COMMENT '策略ID',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '执行状态：PENDING,RUNNING,COMPLETED,FAILED,CANCELLED',
    progress INT DEFAULT 0 COMMENT '执行进度(0-100)',
    start_time TIMESTAMP COMMENT '开始时间',
    end_time TIMESTAMP COMMENT '结束时间',
    error_message VARCHAR(800) COMMENT '错误信息',
    
    -- JSON字段
    statistics JSON COMMENT '执行统计信息',
    
    -- 创建时间
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    -- 索引
    INDEX idx_policy_created_date (policy_id, created_date DESC),
    INDEX idx_tenant_status (tenant_id, status),
    INDEX idx_start_time (start_time DESC),
    INDEX idx_created_date (created_date DESC),
    
    -- 外键约束
    FOREIGN KEY (policy_id) REFERENCES cleanup_policy(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='清理执行记录表';
```

#### 3. PostgreSQL表结构

```sql
-- 清理策略表（PostgreSQL版本）
CREATE TABLE cleanup_policy (
    id VARCHAR(20) PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    repository_id VARCHAR(20) NOT NULL,
    type VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    dry_run BOOLEAN DEFAULT FALSE,
    last_executed TIMESTAMP,
    next_execution TIMESTAMP,
    execution_count INTEGER DEFAULT 0,
    
    -- JSON字段
    condition JSONB,
    schedule JSONB,
    last_execution_stats JSONB,
    
    -- 审计字段
    created_by BIGINT NOT NULL,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_by BIGINT NOT NULL,
    modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- 约束
    CONSTRAINT uk_tenant_repository_name UNIQUE (tenant_id, repository_id, name)
);

-- 索引
CREATE INDEX idx_cleanup_policy_tenant_repository ON cleanup_policy (tenant_id, repository_id);
CREATE INDEX idx_cleanup_policy_enabled_next_execution ON cleanup_policy (enabled, next_execution);
CREATE INDEX idx_cleanup_policy_created_date ON cleanup_policy (created_date DESC);

-- 清理执行记录表（PostgreSQL版本）
CREATE TABLE cleanup_execution (
    id VARCHAR(20) PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    policy_id VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    progress INTEGER DEFAULT 0,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    error_message VARCHAR(800),
    
    -- JSON字段
    statistics JSONB,
    
    -- 创建时间
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- 外键约束
    FOREIGN KEY (policy_id) REFERENCES cleanup_policy(id) ON DELETE CASCADE
);

-- 索引
CREATE INDEX idx_cleanup_execution_policy_created_date ON cleanup_execution (policy_id, created_date DESC);
CREATE INDEX idx_cleanup_execution_tenant_status ON cleanup_execution (tenant_id, status);
CREATE INDEX idx_cleanup_execution_start_time ON cleanup_execution (start_time DESC);
CREATE INDEX idx_cleanup_execution_created_date ON cleanup_execution (created_date DESC);
```

### JSON字段结构

#### 1. 清理条件（condition）

```json
{
  "olderThanDays": 30,
  "keepLastVersions": 5,
  "maxSizeBytes": 1073741824,
  "namePattern": ".*-SNAPSHOT",
  "minDownloads": 0,
  "excludePatterns": [".*-release", ".*-final"]
}
```

#### 2. 调度配置（schedule）

```json
{
  "type": "CRON",
  "cronExpression": "0 3 * * *",
  "intervalHours": 24,
  "executeTime": "03:00:00"
}
```

#### 3. 执行统计（statistics）

```json
{
  "deletedArtifacts": 15,
  "freedSpaceBytes": 2147483648,
  "freedSpace": "2.0 GB",
  "executedAt": "2024-01-15T03:00:00",
  "durationSeconds": 120,
  "scannedArtifacts": 100,
  "skippedArtifacts": 85,
  "deletedArtifactNames": [
    "app-1.0.0-SNAPSHOT.jar",
    "app-1.1.0-SNAPSHOT.jar"
  ],
  "errorDetails": null
}
```

## 正确性属性

*属性是一个特征或行为，应该在系统的所有有效执行中保持为真——本质上是关于系统应该做什么的正式声明。属性作为人类可读规范和机器可验证正确性保证之间的桥梁。*

### 属性 1: 清理策略CRUD操作一致性
*对于任何* 有效的清理策略数据，创建策略后查询应该返回相同的策略信息，更新策略后查询应该返回更新后的信息，删除策略后查询应该返回不存在错误
**验证需求: 需求 1.1, 1.3, 1.4, 1.5**

### 属性 2: 策略名称唯一性约束
*对于任何* 租户和仓库组合，在同一仓库下创建具有相同名称的策略应该被拒绝，返回名称已存在的错误
**验证需求: 需求 1.1**

### 属性 3: 多租户数据隔离
*对于任何* 租户用户，查询操作应该只返回属于当前租户的数据，不能访问其他租户的策略或执行记录
**验证需求: 需求 8.1, 8.2, 8.5**

### 属性 4: 策略状态变更一致性
*对于任何* 清理策略，启用策略应该设置enabled为true并计算下次执行时间，禁用策略应该设置enabled为false并停止调度
**验证需求: 需求 2.1, 2.2, 2.4**

### 属性 5: 执行记录生命周期管理
*对于任何* 策略执行请求，立即执行应该创建PENDING状态的执行记录，执行完成应该更新为COMPLETED状态并记录统计信息，执行失败应该更新为FAILED状态并记录错误信息
**验证需求: 需求 3.1, 3.4, 3.5**

### 属性 6: 并发执行控制
*对于任何* 正在执行中的策略，重复的执行请求应该被拒绝并返回策略正在执行的错误信息
**验证需求: 需求 3.2**

### 属性 7: 试运行模式安全性
*对于任何* 设置为试运行模式的策略执行，应该模拟清理过程但不实际删除任何制品，执行统计应该显示模拟结果
**验证需求: 需求 3.3**

### 属性 8: 按时间清理逻辑正确性
*对于任何* 配置为按时间清理的策略，应该只识别和清理超过指定天数未使用的制品，未超过时间限制的制品应该被保留
**验证需求: 需求 7.1**

### 属性 9: 按数量清理逻辑正确性
*对于任何* 配置为按数量清理的策略，应该保留最新的指定数量版本，其余较旧的版本应该被清理
**验证需求: 需求 7.2**

### 属性 10: 按大小清理逻辑正确性
*对于任何* 配置为按大小清理的策略，当存储超过限制时应该按优先级清理制品直到存储大小符合限制
**验证需求: 需求 7.3**

### 属性 11: 按模式清理逻辑正确性
*对于任何* 配置为按模式清理的策略，应该使用正则表达式准确匹配制品名称，只清理匹配模式的制品
**验证需求: 需求 7.4**

### 属性 12: 多条件清理优先级处理
*对于任何* 满足多个清理条件的制品，系统应该按照策略配置的优先级规则决定是否清理，确保清理逻辑的一致性
**验证需求: 需求 7.5**

### 属性 13: 调度时间计算准确性
*对于任何* 配置了CRON表达式的策略，系统应该按照CRON规则准确计算下次执行时间；对于简单调度类型，应该按照预定义间隔计算下次执行时间
**验证需求: 需求 6.2, 6.3**

### 属性 14: 定时调度执行触发
*对于任何* 到达调度时间的启用策略，系统应该自动创建执行记录并触发清理操作
**验证需求: 需求 6.1**

### 属性 15: 调度执行失败处理
*对于任何* 调度执行失败的情况，系统应该记录失败信息并根据重试策略决定是否重试
**验证需求: 需求 6.4**

### 属性 16: 查询结果分页和筛选
*对于任何* 分页查询请求，系统应该返回正确的分页结果，支持按指定条件筛选，并且查询结果应该按指定顺序排列
**验证需求: 需求 1.2, 4.1, 4.4**

### 属性 17: 执行历史完整性
*对于任何* 策略的执行历史查询，应该返回该策略的所有执行记录，包含完整的统计数据和执行时长信息
**验证需求: 需求 4.2, 4.3**

### 属性 18: 统计数据计算准确性
*对于任何* 统计查询请求，系统应该准确计算策略数量、执行次数、删除制品总数和释放空间总量
**验证需求: 需求 5.1, 5.2**

### 属性 19: 趋势数据一致性
*对于任何* 趋势数据查询，系统应该返回指定时间段内的清理趋势数据，数据应该与实际执行记录保持一致
**验证需求: 需求 5.3**

### 属性 20: 权限验证有效性
*对于任何* 策略修改或删除操作，系统应该验证用户对该策略的相应权限，无权限用户的操作应该被拒绝
**验证需求: 需求 8.3, 8.4**

### 属性 21: 审计信息完整记录
*对于任何* 策略的创建、修改、删除操作以及执行开始、结束、失败等事件，系统应该记录完整的审计信息包括操作人、操作时间和操作内容
**验证需求: 需求 9.1, 9.2, 9.3, 9.4**

### 属性 22: 审计日志查询筛选
*对于任何* 审计日志查询请求，系统应该支持按时间、操作人、操作类型等条件进行筛选，返回符合条件的审计记录
**验证需求: 需求 9.5**

### 属性 23: 事务操作原子性
*对于任何* 策略配置变更操作，系统应该在单个事务中完成所有相关数据的更新，确保数据的一致性
**验证需求: 需求 10.1**

### 属性 24: 并发修改冲突检测
*对于任何* 并发修改同一策略的操作，系统应该使用乐观锁机制检测并防止数据冲突
**验证需求: 需求 10.3**

### 属性 25: 批量处理性能优化
*对于任何* 大批量清理操作，系统应该分批处理避免长时间锁定资源，每批处理的数量应该在合理范围内
**验证需求: 需求 11.1**

### 属性 26: 分页查询性能保证
*对于任何* 策略列表查询，系统应该支持分页查询并在合理时间内返回结果，查询性能应该满足要求
**验证需求: 需求 11.2**

### 属性 27: 缓存数据一致性
*对于任何* 使用缓存的统计查询，缓存数据应该与实际数据保持一致，缓存更新应该及时反映数据变更
**验证需求: 需求 11.3**

### 属性 28: 执行进度反馈准确性
*对于任何* 长时间运行的清理执行，系统应该提供准确的执行进度反馈，进度值应该在0-100范围内并反映实际执行状态
**验证需求: 需求 11.5**

### 属性 29: 国际化文本显示正确性
*对于任何* 用户界面文本和错误消息，系统应该根据用户语言偏好显示对应的本地化文本，文本内容应该准确无误
**验证需求: 需求 12.1, 12.2**

### 属性 30: 错误信息详细性和安全性
*对于任何* API错误和参数验证失败，系统应该提供详细的错误描述和解决建议；对于系统内部错误，应该记录详细日志但只向用户返回通用错误信息
**验证需求: 需求 12.3, 12.4, 12.5**

## 错误处理

### 业务异常处理

1. **策略不存在异常**
   - 异常类型: `ResourceNotFound`
   - 触发条件: 查询、更新、删除不存在的策略
   - 错误码: `CLEANUP_POLICY_NOT_FOUND`
   - 处理方式: 返回404状态码和本地化错误消息

2. **策略名称冲突异常**
   - 异常类型: `ProtocolException`
   - 触发条件: 创建或更新策略时名称在同一仓库下已存在
   - 错误码: `CLEANUP_POLICY_NAME_EXISTS`
   - 处理方式: 返回400状态码和冲突提示

3. **策略正在执行异常**
   - 异常类型: `ProtocolException`
   - 触发条件: 策略正在执行时尝试立即执行或删除
   - 错误码: `CLEANUP_POLICY_EXECUTING`
   - 处理方式: 返回400状态码和执行中提示

4. **清理条件验证异常**
   - 异常类型: `ProtocolException`
   - 触发条件: 清理条件配置不符合业务规则
   - 错误码: `CLEANUP_CONDITION_INVALID`
   - 处理方式: 返回400状态码和具体验证错误

5. **调度配置验证异常**
   - 异常类型: `ProtocolException`
   - 触发条件: CRON表达式格式错误或调度配置无效
   - 错误码: `CLEANUP_SCHEDULE_INVALID`
   - 处理方式: 返回400状态码和配置错误详情

### 系统异常处理

1. **数据库连接异常**
   - 处理方式: 记录详细错误日志，返回通用系统错误
   - 重试机制: 自动重试数据库操作
   - 降级策略: 返回缓存数据或默认值

2. **调度器异常**
   - 处理方式: 记录调度失败日志，更新执行记录状态
   - 重试机制: 根据重试策略决定是否重试
   - 告警机制: 发送调度失败告警

3. **清理执行异常**
   - 处理方式: 记录执行失败详情，回滚部分操作
   - 状态更新: 将执行状态设置为FAILED
   - 通知机制: 发送执行失败通知

### 参数验证异常

1. **必填参数缺失**
   - 使用Bean Validation注解自动验证
   - 返回具体的字段错误信息
   - 支持多语言错误消息

2. **参数格式错误**
   - 验证参数类型、长度、范围
   - 返回详细的验证规则说明
   - 提供正确的参数示例

3. **业务规则验证**
   - 在Cmd层使用BizTemplate进行业务验证
   - 返回业务相关的错误提示
   - 支持复杂的业务规则验证

## 测试策略

### 双重测试方法

本系统采用单元测试和属性测试相结合的综合测试策略：

**单元测试**：
- 验证具体示例和边界情况
- 测试组件间的集成点
- 验证错误条件和异常处理
- 测试特定的业务场景

**属性测试**：
- 验证通用属性在所有输入下的正确性
- 通过随机化实现全面的输入覆盖
- 验证系统的整体正确性保证

### 属性测试配置

**测试框架选择**：
- Java: 使用jqwik属性测试框架
- 每个属性测试最少运行100次迭代
- 使用随机数据生成器创建测试数据

**测试标记格式**：
每个属性测试必须使用以下注释格式标记：
```java
/**
 * Feature: cleanup-strategy-interface, Property 1: 清理策略CRUD操作一致性
 */
@Property
void cleanupPolicyCrudConsistency(@ForAll CleanupPolicy policy) {
    // 测试实现
}
```

**属性测试实现要求**：
- 每个正确性属性必须对应一个属性测试
- 测试必须验证属性在所有有效输入下的正确性
- 使用数据生成器创建符合约束的随机测试数据
- 验证操作的前置条件和后置条件

### 单元测试重点

**边界情况测试**：
- 空数据处理（需求5.4）
- 最大/最小值边界
- 特殊字符和编码处理
- 网络超时和重试

**集成测试**：
- 数据库事务处理
- 调度器集成
- 多租户数据隔离
- 权限验证集成

**错误处理测试**：
- 各种异常场景的处理
- 错误消息的国际化
- 系统异常的优雅降级
- 审计日志的完整记录

### 性能测试

**负载测试**：
- 大量策略的查询性能
- 批量清理操作的性能
- 并发执行的性能表现
- 数据库查询优化验证

**压力测试**：
- 系统在高负载下的稳定性
- 内存和CPU使用情况
- 数据库连接池的管理
- 调度器的性能表现

### 安全测试

**权限测试**：
- 多租户数据隔离验证
- 跨租户访问控制
- 用户权限验证
- API安全性测试

**数据安全测试**：
- 敏感信息的保护
- 审计日志的完整性
- 数据传输的安全性
- 试运行模式的安全性

## 基础设施层实现

### 持久化实现

#### MySQL实现

```java
// MySQL清理策略仓储实现
@Repository
public interface CleanupPolicyRepoMysql extends CleanupPolicyRepo {
    // 继承领域层接口，Spring会根据配置自动选择实现
}

// MySQL清理执行记录仓储实现
@Repository
public interface CleanupExecutionRepoMysql extends CleanupExecutionRepo {
    // 继承领域层接口
}

// MySQL搜索仓储实现
@Repository
public interface CleanupPolicySearchRepoMysql extends CleanupPolicySearchRepo {
    // 继承全文搜索接口
}
```

#### PostgreSQL实现

```java
// PostgreSQL清理策略仓储实现
@Repository
public interface CleanupPolicyRepoPostgres extends CleanupPolicyRepo {
    // 继承领域层接口，Spring会根据配置自动选择实现
}

// PostgreSQL清理执行记录仓储实现
@Repository
public interface CleanupExecutionRepoPostgres extends CleanupExecutionRepo {
    // 继承领域层接口
}

// PostgreSQL搜索仓储实现
@Repository
public interface CleanupPolicySearchRepoPostgres extends CleanupPolicySearchRepo {
    // 继承全文搜索接口
}
```

### 调度器实现

```java
// 清理调度器接口
public interface CleanupScheduler {
    void schedulePolicy(CleanupPolicy policy);
    void reschedulePolicy(CleanupPolicy policy);
    void unschedulePolicy(String policyId);
    void executePolicy(CleanupPolicy policy, CleanupExecution execution, boolean dryRun);
    LocalDateTime calculateNextExecution(CleanupSchedule schedule);
    void startScheduler();
    void stopScheduler();
}

// 清理调度器实现
@Component
public class CleanupSchedulerImpl implements CleanupScheduler {

    @Resource
    private TaskScheduler taskScheduler;

    @Resource
    private CleanupExecutor cleanupExecutor;

    @Resource
    private CleanupPolicyRepo cleanupPolicyRepo;

    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    @Override
    public void schedulePolicy(CleanupPolicy policy) {
        if (!policy.getEnabled() || policy.getNextExecution() == null) {
            return;
        }
        
        // 取消现有调度
        unschedulePolicy(policy.getId());
        
        // 创建新的调度任务
        Runnable task = () -> {
            try {
                // 创建执行记录
                CleanupExecution execution = new CleanupExecution();
                execution.setId(IdGenerator.nextId());
                execution.setPolicyId(policy.getId());
                execution.setStatus(CleanupStatus.PENDING);
                
                // 异步执行清理
                executePolicy(policy, execution, policy.getDryRun());
                
                // 计算下次执行时间
                LocalDateTime nextExecution = calculateNextExecution(policy.getSchedule());
                cleanupPolicyRepo.updateNextExecution(policy.getId(), nextExecution);
                
            } catch (Exception e) {
                log.error("调度执行策略失败: policyId={}", policy.getId(), e);
            }
        };
        
        // 调度任务
        ScheduledFuture<?> future = taskScheduler.schedule(task, 
            Date.from(policy.getNextExecution().atZone(ZoneId.systemDefault()).toInstant()));
        scheduledTasks.put(policy.getId(), future);
    }

    @Override
    public void executePolicy(CleanupPolicy policy, CleanupExecution execution, boolean dryRun) {
        // 异步执行清理操作
        CompletableFuture.runAsync(() -> {
            cleanupExecutor.execute(policy, execution, dryRun);
        });
    }

    @Override
    public LocalDateTime calculateNextExecution(CleanupSchedule schedule) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (schedule.getType()) {
            case ONCE:
                return null; // 只执行一次，不再调度
                
            case DAILY:
                LocalTime executeTime = schedule.getExecuteTime() != null 
                    ? schedule.getExecuteTime() : LocalTime.of(3, 0);
                LocalDateTime nextDaily = now.toLocalDate().atTime(executeTime);
                if (nextDaily.isBefore(now)) {
                    nextDaily = nextDaily.plusDays(1);
                }
                return nextDaily;
                
            case WEEKLY:
                LocalDateTime nextWeekly = now.plusWeeks(1);
                return nextWeekly;
                
            case MONTHLY:
                LocalDateTime nextMonthly = now.plusMonths(1);
                return nextMonthly;
                
            case CRON:
                if (StringUtils.isNotBlank(schedule.getCronExpression())) {
                    CronExpression cron = CronExpression.parse(schedule.getCronExpression());
                    return cron.next(now);
                }
                break;
        }
        
        return null;
    }

    @PostConstruct
    public void startScheduler() {
        // 系统启动时加载所有启用的策略
        List<CleanupPolicy> enabledPolicies = cleanupPolicyRepo.findByEnabledTrue();
        for (CleanupPolicy policy : enabledPolicies) {
            schedulePolicy(policy);
        }
    }

    @PreDestroy
    public void stopScheduler() {
        // 系统关闭时取消所有调度任务
        scheduledTasks.values().forEach(future -> future.cancel(false));
        scheduledTasks.clear();
    }
}
```

### 清理执行器

```java
// 清理执行器接口
public interface CleanupExecutor {
    void execute(CleanupPolicy policy, CleanupExecution execution, boolean dryRun);
}

// 清理执行器实现
@Component
public class CleanupExecutorImpl implements CleanupExecutor {

    @Resource
    private CleanupExecutionCmd cleanupExecutionCmd;

    @Resource
    private ArtifactService artifactService;

    @Override
    public void execute(CleanupPolicy policy, CleanupExecution execution, boolean dryRun) {
        try {
            // 更新执行状态为运行中
            cleanupExecutionCmd.updateStatus(execution.getId(), CleanupStatus.RUNNING, 0);
            
            // 根据清理类型执行不同的清理逻辑
            CleanupStatistics statistics = executeCleanupByType(policy, execution, dryRun);
            
            // 更新执行完成状态
            cleanupExecutionCmd.complete(execution.getId(), statistics);
            
        } catch (Exception e) {
            // 更新执行失败状态
            cleanupExecutionCmd.fail(execution.getId(), e.getMessage());
            log.error("清理执行失败: policyId={}, executionId={}", 
                policy.getId(), execution.getId(), e);
        }
    }

    private CleanupStatistics executeCleanupByType(CleanupPolicy policy, 
                                                 CleanupExecution execution, 
                                                 boolean dryRun) {
        CleanupStatistics statistics = new CleanupStatistics();
        statistics.setExecutedAt(LocalDateTime.now());
        
        LocalDateTime startTime = LocalDateTime.now();
        
        try {
            switch (policy.getType()) {
                case BY_AGE:
                    statistics = executeAgeBasedCleanup(policy, execution, dryRun);
                    break;
                case BY_COUNT:
                    statistics = executeCountBasedCleanup(policy, execution, dryRun);
                    break;
                case BY_SIZE:
                    statistics = executeSizeBasedCleanup(policy, execution, dryRun);
                    break;
                case BY_PATTERN:
                    statistics = executePatternBasedCleanup(policy, execution, dryRun);
                    break;
            }
        } finally {
            LocalDateTime endTime = LocalDateTime.now();
            statistics.setDurationSeconds(Duration.between(startTime, endTime).getSeconds());
        }
        
        return statistics;
    }

    private CleanupStatistics executeAgeBasedCleanup(CleanupPolicy policy, 
                                                   CleanupExecution execution, 
                                                   boolean dryRun) {
        // 按时间清理的具体实现
        CleanupCondition condition = policy.getCondition();
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(condition.getOlderThanDays());
        
        // 查询需要清理的制品
        List<Artifact> artifactsToClean = artifactService.findArtifactsOlderThan(
            policy.getRepositoryId(), cutoffDate);
        
        return performCleanup(artifactsToClean, execution, dryRun);
    }

    private CleanupStatistics executeCountBasedCleanup(CleanupPolicy policy, 
                                                     CleanupExecution execution, 
                                                     boolean dryRun) {
        // 按数量清理的具体实现
        CleanupCondition condition = policy.getCondition();
        
        // 查询需要清理的制品（保留最新的N个版本）
        List<Artifact> artifactsToClean = artifactService.findArtifactsExceedingCount(
            policy.getRepositoryId(), condition.getKeepLastVersions());
        
        return performCleanup(artifactsToClean, execution, dryRun);
    }

    private CleanupStatistics executeSizeBasedCleanup(CleanupPolicy policy, 
                                                    CleanupExecution execution, 
                                                    boolean dryRun) {
        // 按大小清理的具体实现
        CleanupCondition condition = policy.getCondition();
        
        // 查询需要清理的制品（按大小排序）
        List<Artifact> artifactsToClean = artifactService.findArtifactsExceedingSize(
            policy.getRepositoryId(), condition.getMaxSizeBytes());
        
        return performCleanup(artifactsToClean, execution, dryRun);
    }

    private CleanupStatistics executePatternBasedCleanup(CleanupPolicy policy, 
                                                       CleanupExecution execution, 
                                                       boolean dryRun) {
        // 按模式清理的具体实现
        CleanupCondition condition = policy.getCondition();
        Pattern pattern = Pattern.compile(condition.getNamePattern());
        
        // 查询匹配模式的制品
        List<Artifact> artifactsToClean = artifactService.findArtifactsByPattern(
            policy.getRepositoryId(), pattern);
        
        return performCleanup(artifactsToClean, execution, dryRun);
    }

    private CleanupStatistics performCleanup(List<Artifact> artifacts, 
                                           CleanupExecution execution, 
                                           boolean dryRun) {
        CleanupStatistics statistics = new CleanupStatistics();
        statistics.setScannedArtifacts(artifacts.size());
        
        List<String> deletedArtifactNames = new ArrayList<>();
        long totalFreedBytes = 0;
        int deletedCount = 0;
        
        for (int i = 0; i < artifacts.size(); i++) {
            Artifact artifact = artifacts.get(i);
            
            try {
                if (!dryRun) {
                    // 实际删除制品
                    artifactService.deleteArtifact(artifact.getId());
                }
                
                deletedArtifactNames.add(artifact.getName());
                totalFreedBytes += artifact.getSize();
                deletedCount++;
                
                // 更新进度
                int progress = (i + 1) * 100 / artifacts.size();
                cleanupExecutionCmd.updateStatus(execution.getId(), CleanupStatus.RUNNING, progress);
                
            } catch (Exception e) {
                log.warn("删除制品失败: artifactId={}", artifact.getId(), e);
                statistics.setSkippedArtifacts(statistics.getSkippedArtifacts() + 1);
            }
        }
        
        statistics.setDeletedArtifacts(deletedCount);
        statistics.setFreedSpaceBytes(totalFreedBytes);
        statistics.setFreedSpace(FileUtils.formatFileSize(totalFreedBytes));
        statistics.setDeletedArtifactNames(deletedArtifactNames);
        
        return statistics;
    }
}
```

### 工具类

```java
// 文件大小格式化工具
public class FileUtils {
    
    private static final String[] SIZE_UNITS = {"B", "KB", "MB", "GB", "TB"};
    
    public static String formatFileSize(long bytes) {
        if (bytes <= 0) return "0 B";
        
        int unitIndex = (int) (Math.log(bytes) / Math.log(1024));
        double size = bytes / Math.pow(1024, unitIndex);
        
        return String.format("%.1f %s", size, SIZE_UNITS[unitIndex]);
    }
}

// ID生成器
public class IdGenerator {
    
    private static final SnowflakeIdWorker idWorker = new SnowflakeIdWorker(1, 1);
    
    public static String nextId() {
        return String.valueOf(idWorker.nextId());
    }
}
```

## 门面服务和REST控制器

### 门面服务实现

```java
// 清理策略门面接口
public interface CleanupPolicyFacade {
    CleanupPolicyDetailVo create(CleanupPolicyCreateDto dto);
    CleanupPolicyDetailVo update(String id, CleanupPolicyUpdateDto dto);
    CleanupPolicyDetailVo updateEnabled(String id, CleanupPolicyEnabledDto dto);
    void delete(String id);
    CleanupPolicyDetailVo getDetail(String id);
    PageResult<CleanupPolicyListVo> list(CleanupPolicyFindDto dto);
    CleanupExecutionVo executeImmediately(String id, CleanupPolicyExecuteDto dto);
    PageResult<CleanupExecutionVo> listExecutions(String id, CleanupExecutionFindDto dto);
    CleanupOverallStatisticsVo getStatistics();
}

// 清理策略门面实现
@Component
public class CleanupPolicyFacadeImpl implements CleanupPolicyFacade {

    @Resource
    private CleanupPolicyCmd cleanupPolicyCmd;

    @Resource
    private CleanupPolicyQuery cleanupPolicyQuery;

    @Resource
    private CleanupExecutionQuery cleanupExecutionQuery;

    @NameJoin
    @Override
    public CleanupPolicyDetailVo create(CleanupPolicyCreateDto dto) {
        // DTO → Domain
        CleanupPolicy policy = CleanupPolicyAssembler.toCreateDomain(dto);
        
        // 调用命令服务保存
        CleanupPolicy saved = cleanupPolicyCmd.create(policy);
        
        // Domain → VO
        return CleanupPolicyAssembler.toDetailVo(saved);
    }

    @NameJoin
    @Override
    public CleanupPolicyDetailVo update(String id, CleanupPolicyUpdateDto dto) {
        CleanupPolicy policy = CleanupPolicyAssembler.toUpdateDomain(dto, id);
        CleanupPolicy updated = cleanupPolicyCmd.update(policy);
        return CleanupPolicyAssembler.toDetailVo(updated);
    }

    @NameJoin
    @Override
    public CleanupPolicyDetailVo updateEnabled(String id, CleanupPolicyEnabledDto dto) {
        CleanupPolicy updated = cleanupPolicyCmd.updateEnabled(id, dto.getEnabled());
        return CleanupPolicyAssembler.toDetailVo(updated);
    }

    @Override
    public void delete(String id) {
        cleanupPolicyCmd.delete(id);
    }

    @NameJoin
    @Override
    public CleanupPolicyDetailVo getDetail(String id) {
        CleanupPolicy policy = cleanupPolicyQuery.detail(id);
        return CleanupPolicyAssembler.toDetailVo(policy);
    }

    @NameJoin
    @Override
    public PageResult<CleanupPolicyListVo> list(CleanupPolicyFindDto dto) {
        // DTO → Specification（查询条件）
        GenericSpecification<CleanupPolicy> spec = CleanupPolicyAssembler.getSpecification(dto);
        
        // 查询数据
        Page<CleanupPolicy> page = cleanupPolicyQuery.list(spec, dto.tranPage(),
            dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
        
        // Domain → VO（分页结果）
        return buildVoPageResult(page, CleanupPolicyAssembler::toListVo);
    }

    @Override
    public CleanupExecutionVo executeImmediately(String id, CleanupPolicyExecuteDto dto) {
        CleanupExecution execution = cleanupPolicyCmd.executeImmediately(id, dto.getDryRun());
        return CleanupExecutionAssembler.toVo(execution);
    }

    @Override
    public PageResult<CleanupExecutionVo> listExecutions(String id, CleanupExecutionFindDto dto) {
        Page<CleanupExecution> page = cleanupExecutionQuery.listByPolicy(id, dto.tranPage());
        return buildVoPageResult(page, CleanupExecutionAssembler::toVo);
    }

    @Override
    public CleanupOverallStatisticsVo getStatistics() {
        return cleanupPolicyQuery.getStatistics();
    }
}
```

### REST控制器实现

```java
@Tag(name = "Cleanup Policies", description = "清理策略管理 - 制品清理策略的创建、查询、更新、删除、执行等功能")
@Validated
@RestController
@RequestMapping("/api/v1/cleanup-policies")
public class CleanupPolicyRest {

    @Resource
    private CleanupPolicyFacade cleanupPolicyFacade;

    @Operation(operationId = "createCleanupPolicy", summary = "创建清理策略", description = "创建新的制品清理策略")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "策略创建成功"),
        @ApiResponse(responseCode = "400", description = "参数验证失败或策略名称已存在")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiLocaleResult<CleanupPolicyDetailVo> create(@Valid @RequestBody CleanupPolicyCreateDto dto) {
        return ApiLocaleResult.success(cleanupPolicyFacade.create(dto));
    }

    @Operation(operationId = "updateCleanupPolicy", summary = "更新清理策略", description = "更新清理策略的配置信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "404", description = "策略不存在")
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public ApiLocaleResult<CleanupPolicyDetailVo> update(
        @Parameter(description = "策略ID") @PathVariable String id,
        @Valid @RequestBody CleanupPolicyUpdateDto dto) {
        return ApiLocaleResult.success(cleanupPolicyFacade.update(id, dto));
    }

    @Operation(operationId = "updateCleanupPolicyEnabled", summary = "启用/禁用策略", description = "启用或禁用清理策略")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "状态更新成功"),
        @ApiResponse(responseCode = "404", description = "策略不存在")
    })
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}/enabled")
    public ApiLocaleResult<CleanupPolicyDetailVo> updateEnabled(
        @Parameter(description = "策略ID") @PathVariable String id,
        @Valid @RequestBody CleanupPolicyEnabledDto dto) {
        return ApiLocaleResult.success(cleanupPolicyFacade.updateEnabled(id, dto));
    }

    @Operation(operationId = "deleteCleanupPolicy", summary = "删除清理策略", description = "删除指定的清理策略")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "删除成功"),
        @ApiResponse(responseCode = "404", description = "策略不存在"),
        @ApiResponse(responseCode = "400", description = "策略正在执行中")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@Parameter(description = "策略ID") @PathVariable String id) {
        cleanupPolicyFacade.delete(id);
    }

    @Operation(operationId = "getCleanupPolicyDetail", summary = "获取策略详情", description = "获取指定清理策略的详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "策略详情获取成功"),
        @ApiResponse(responseCode = "404", description = "策略不存在")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ApiLocaleResult<CleanupPolicyDetailVo> getDetail(
        @Parameter(description = "策略ID") @PathVariable String id) {
        return ApiLocaleResult.success(cleanupPolicyFacade.getDetail(id));
    }

    @Operation(operationId = "listCleanupPolicies", summary = "获取策略列表", description = "获取清理策略列表，支持分页和筛选")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "策略列表获取成功")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ApiLocaleResult<PageResult<CleanupPolicyListVo>> list(
        @Valid @ParameterObject CleanupPolicyFindDto dto) {
        return ApiLocaleResult.success(cleanupPolicyFacade.list(dto));
    }

    @Operation(operationId = "executeCleanupPolicy", summary = "立即执行策略", description = "立即执行指定的清理策略")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "执行请求已提交"),
        @ApiResponse(responseCode = "404", description = "策略不存在"),
        @ApiResponse(responseCode = "400", description = "策略正在执行中")
    })
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{id}/execute")
    public ApiLocaleResult<CleanupExecutionVo> executeImmediately(
        @Parameter(description = "策略ID") @PathVariable String id,
        @Valid @RequestBody(required = false) CleanupPolicyExecuteDto dto) {
        if (dto == null) {
            dto = new CleanupPolicyExecuteDto();
        }
        return ApiLocaleResult.success(cleanupPolicyFacade.executeImmediately(id, dto));
    }

    @Operation(operationId = "listCleanupExecutions", summary = "获取执行历史", description = "获取指定策略的执行历史记录")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "执行历史获取成功"),
        @ApiResponse(responseCode = "404", description = "策略不存在")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/executions")
    public ApiLocaleResult<PageResult<CleanupExecutionVo>> listExecutions(
        @Parameter(description = "策略ID") @PathVariable String id,
        @Valid @ParameterObject CleanupExecutionFindDto dto) {
        return ApiLocaleResult.success(cleanupPolicyFacade.listExecutions(id, dto));
    }

    @Operation(operationId = "getCleanupStatistics", summary = "获取清理统计", description = "获取整体清理统计信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "统计信息获取成功")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/statistics")
    public ApiLocaleResult<CleanupOverallStatisticsVo> getStatistics() {
        return ApiLocaleResult.success(cleanupPolicyFacade.getStatistics());
    }
}
```

## 总结

本设计文档基于DDD分层架构，严格遵循接口开发规范，实现了完整的清理策略接口功能。设计包含：

**核心特性**：
- 完整的CRUD操作和状态管理
- 多种清理类型支持（按时间、数量、大小、模式）
- 灵活的调度配置（CRON表达式、简单调度）
- 完整的执行历史和统计分析
- 试运行模式保证安全性

**技术特性**：
- 严格的DDD分层架构
- MySQL和PostgreSQL双数据库支持
- 多租户数据隔离和审计功能
- 全文搜索和分页查询
- 完善的异常处理和国际化支持
- 属性测试和单元测试相结合的测试策略

**性能和安全**：
- 批量处理和分页优化
- 缓存机制提升查询性能
- 权限验证和跨租户访问控制
- 完整的审计日志记录
- 事务一致性和并发控制

该设计确保了系统的可靠性、可维护性和可扩展性，为制品仓库提供了强大的自动化清理管理能力。