# 清理策略接口功能实现计划

## 概述

本实现计划基于DDD分层架构，使用Java Spring Boot技术栈，严格遵循接口开发规范，实现完整的清理策略接口功能。计划包含领域层、基础设施层、应用层、接口层的完整实现，支持MySQL和PostgreSQL双数据库，具备多租户、审计、国际化等企业级特性。

## 任务列表

- [x] 1. 创建项目结构和核心配置
  - 创建DDD分层目录结构
  - 配置Spring Boot基础依赖
  - 配置多数据库支持（MySQL/PostgreSQL）
  - 配置多租户和审计功能
  - _需求: 需求 8.1, 8.2, 9.1_

- [ ] 2. 实现领域层核心组件
  - [x] 2.1 创建清理策略领域实体
    - 实现CleanupPolicy实体类，继承TenantAuditingEntity
    - 定义所有业务字段和JSON类型字段
    - 实现identity()方法和审计监听器
    - _需求: 需求 1.1, 8.1, 9.1_

  - [x] 2.2 创建清理执行记录领域实体
    - 实现CleanupExecution实体类，继承TenantEntity
    - 定义执行状态、进度、统计等字段
    - 实现PrePersist生命周期方法
    - _需求: 需求 3.1, 3.4, 3.5_

  - [x] 2.3 创建枚举类型和值对象
    - 实现CleanupType、CleanupStatus、ScheduleType枚举
    - 实现CleanupCondition、CleanupSchedule、CleanupStatistics值对象
    - 定义完整的枚举值和描述信息
    - _需求: 需求 7.1, 7.2, 7.3, 7.4, 6.2, 6.3_

  - [x] 2.4 创建仓储接口
    - 实现CleanupPolicyRepo接口，继承BaseRepository
    - 定义基础查询、统计查询、批量更新方法
    - 实现CleanupExecutionRepo接口
    - 创建CleanupPolicySearchRepo全文搜索接口
    - _需求: 需求 1.2, 4.1, 5.1, 5.2_

  - [x] 2.5 编写领域层属性测试
    - **属性 1: 清理策略CRUD操作一致性**
    - **验证需求: 需求 1.1, 1.3, 1.4, 1.5**

  - [ ] 2.6 编写领域层单元测试
    - 测试实体类的基本功能和约束
    - 测试枚举和值对象的正确性
    - _需求: 需求 1.1, 3.1_

- [ ] 3. 实现基础设施层持久化
  - [ ] 3.1 创建MySQL持久化实现
    - 实现CleanupPolicyRepoMysql接口
    - 实现CleanupExecutionRepoMysql接口
    - 实现CleanupPolicySearchRepoMysql接口
    - _需求: 需求 1.2, 4.1_

  - [ ] 3.2 创建PostgreSQL持久化实现
    - 实现CleanupPolicyRepoPostgres接口
    - 实现CleanupExecutionRepoPostgres接口
    - 实现CleanupPolicySearchRepoPostgres接口
    - _需求: 需求 1.2, 4.1_

  - [ ] 3.3 创建数据库表结构
    - 创建cleanup_policy表（MySQL和PostgreSQL版本）
    - 创建cleanup_execution表（MySQL和PostgreSQL版本）
    - 创建必要的索引和外键约束
    - _需求: 需求 1.1, 3.1, 8.1_

  - [ ] 3.4 编写持久化层属性测试
    - **属性 2: 策略名称唯一性约束**
    - **属性 3: 多租户数据隔离**
    - **验证需求: 需求 1.1, 8.1, 8.5**

- [ ] 4. 实现调度器和执行器组件
  - [ ] 4.1 实现清理调度器
    - 实现CleanupScheduler接口和CleanupSchedulerImpl类
    - 实现策略调度、重新调度、取消调度功能
    - 实现CRON表达式和简单调度的时间计算
    - 实现系统启动时的调度恢复功能
    - _需求: 需求 6.1, 6.2, 6.3, 6.5_

  - [ ] 4.2 实现清理执行器
    - 实现CleanupExecutor接口和CleanupExecutorImpl类
    - 实现按时间、数量、大小、模式的清理逻辑
    - 实现试运行模式和实际清理模式
    - 实现执行进度更新和统计信息收集
    - _需求: 需求 7.1, 7.2, 7.3, 7.4, 3.3, 11.5_

  - [ ] 4.3 创建工具类
    - 实现FileUtils文件大小格式化工具
    - 实现IdGenerator ID生成器
    - 实现其他必要的工具类
    - _需求: 需求 5.2_

  - [ ] 4.4 编写调度器属性测试
    - **属性 13: 调度时间计算准确性**
    - **属性 14: 定时调度执行触发**
    - **验证需求: 需求 6.1, 6.2, 6.3**

  - [ ] 4.5 编写执行器属性测试
    - **属性 8: 按时间清理逻辑正确性**
    - **属性 9: 按数量清理逻辑正确性**
    - **属性 10: 按大小清理逻辑正确性**
    - **属性 11: 按模式清理逻辑正确性**
    - **属性 7: 试运行模式安全性**
    - **验证需求: 需求 7.1, 7.2, 7.3, 7.4, 3.3**

- [ ] 5. 实现应用层命令服务
  - [ ] 5.1 实现清理策略命令服务
    - 实现CleanupPolicyCmd接口和CleanupPolicyCmdImpl类
    - 实现create、update、updateEnabled、delete方法
    - 实现executeImmediately立即执行方法
    - 使用BizTemplate进行参数校验和业务处理
    - 实现清理条件和调度配置的验证逻辑
    - _需求: 需求 1.1, 1.3, 1.4, 2.1, 2.2, 3.1_

  - [ ] 5.2 实现清理执行命令服务
    - 实现CleanupExecutionCmd接口和CleanupExecutionCmdImpl类
    - 实现create、updateStatus、complete、fail方法
    - 实现isExecuting并发执行检查方法
    - 实现cleanupOldExecutions历史数据清理方法
    - _需求: 需求 3.1, 3.2, 3.4, 3.5_

  - [ ] 5.3 编写命令服务属性测试
    - **属性 4: 策略状态变更一致性**
    - **属性 5: 执行记录生命周期管理**
    - **属性 6: 并发执行控制**
    - **属性 23: 事务操作原子性**
    - **验证需求: 需求 2.1, 2.2, 3.1, 3.2, 3.4, 3.5, 10.1**

- [ ] 6. 实现应用层查询服务
  - [ ] 6.1 实现清理策略查询服务
    - 实现CleanupPolicyQuery接口和CleanupPolicyQueryImpl类
    - 实现findAndCheck、detail、list方法
    - 实现getStatistics统计查询方法（允许返回VO）
    - 实现assembleDetailInfos关联数据组装方法
    - 支持全文搜索和标准查询两种模式
    - _需求: 需求 1.2, 1.5, 4.1, 5.1, 5.2, 5.3_

  - [ ] 6.2 实现清理执行查询服务
    - 实现CleanupExecutionQuery接口和CleanupExecutionQueryImpl类
    - 实现findAndCheck、listByPolicy方法
    - 实现assembleDetailInfos关联数据组装方法
    - _需求: 需求 4.1, 4.2, 4.3_

  - [ ] 6.3 编写查询服务属性测试
    - **属性 16: 查询结果分页和筛选**
    - **属性 17: 执行历史完整性**
    - **属性 18: 统计数据计算准确性**
    - **属性 19: 趋势数据一致性**
    - **验证需求: 需求 1.2, 4.1, 4.2, 4.4, 5.1, 5.2, 5.3**

- [ ] 7. 检查点 - 确保核心业务逻辑测试通过
  - 确保所有测试通过，如有问题请询问用户

- [ ] 8. 实现接口层DTO和VO
  - [ ] 8.1 创建请求DTO类
    - 实现CleanupPolicyCreateDto、CleanupPolicyUpdateDto类
    - 实现CleanupPolicyFindDto、CleanupPolicyEnabledDto类
    - 实现CleanupPolicyExecuteDto、CleanupExecutionFindDto类
    - 实现CleanupConditionDto、CleanupScheduleDto类
    - 使用Bean Validation注解进行参数验证
    - 使用BizConstant和Constants中的常量限制长度
    - _需求: 需求 1.1, 1.2, 1.3, 2.1, 3.1, 4.1, 12.4_

  - [ ] 8.2 创建响应VO类
    - 实现CleanupPolicyDetailVo、CleanupPolicyListVo类
    - 实现CleanupExecutionVo、CleanupStatisticsVo类
    - 实现CleanupConditionVo、CleanupScheduleVo类
    - 实现CleanupOverallStatisticsVo、CleanupTrendVo类
    - 继承TenantAuditingVo基类（需要审计信息的VO）
    - 使用@Schema注解完善API文档
    - _需求: 需求 1.5, 4.2, 5.1, 9.1_

  - [ ] 8.3 编写DTO/VO属性测试
    - **属性 30: 错误信息详细性和安全性**
    - **验证需求: 需求 12.3, 12.4, 12.5**

- [ ] 9. 实现接口层转换器
  - [ ] 9.1 实现清理策略转换器
    - 实现CleanupPolicyAssembler类
    - 实现toCreateDomain、toUpdateDomain方法
    - 实现toDetailVo、toListVo方法
    - 实现getSpecification查询条件转换方法
    - 配置全文搜索字段（name、description）
    - _需求: 需求 1.1, 1.3, 1.5_

  - [ ] 9.2 实现清理执行转换器
    - 实现CleanupExecutionAssembler类
    - 实现toVo方法和getSpecification方法
    - 实现执行时长计算逻辑
    - _需求: 需求 4.2_

  - [ ] 9.3 编写转换器属性测试
    - 测试DTO到Domain的转换正确性
    - 测试Domain到VO的转换正确性
    - 测试查询条件构建的正确性
    - _需求: 需求 1.1, 1.5_

- [ ] 10. 实现接口层门面服务
  - [ ] 10.1 实现清理策略门面服务
    - 实现CleanupPolicyFacade接口和CleanupPolicyFacadeImpl类
    - 实现create、update、updateEnabled、delete方法
    - 实现getDetail、list、executeImmediately方法
    - 实现listExecutions、getStatistics方法
    - 使用@NameJoin注解自动填充关联名称
    - 使用buildVoPageResult构建分页结果
    - _需求: 需求 1.1, 1.2, 1.3, 1.4, 1.5, 2.1, 2.2, 3.1, 4.1, 5.1_

  - [ ] 10.2 编写门面服务属性测试
    - **属性 20: 权限验证有效性**
    - **属性 21: 审计信息完整记录**
    - **验证需求: 需求 8.3, 8.4, 9.1, 9.2**

- [ ] 11. 实现REST控制器
  - [ ] 11.1 实现清理策略REST控制器
    - 实现CleanupPolicyRest类
    - 实现create、update、updateEnabled、delete接口
    - 实现getDetail、list、executeImmediately接口
    - 实现listExecutions、getStatistics接口
    - 使用正确的HTTP方法和状态码
    - 使用@Tag、@Operation、@ApiResponses完善API文档
    - 使用@Valid和@ParameterObject进行参数验证
    - _需求: 需求 1.1, 1.2, 1.3, 1.4, 1.5, 2.1, 2.2, 3.1, 4.1, 5.1_

  - [ ] 11.2 编写REST控制器属性测试
    - **属性 29: 国际化文本显示正确性**
    - **验证需求: 需求 12.1, 12.2**

- [ ] 12. 实现性能优化和缓存
  - [ ] 12.1 实现分页查询优化
    - 优化策略列表查询的分页性能
    - 优化执行历史查询的分页性能
    - 添加必要的数据库索引
    - _需求: 需求 11.2_

  - [ ] 12.2 实现统计查询缓存
    - 实现统计数据的缓存机制
    - 实现缓存更新和失效策略
    - 确保缓存数据与实际数据的一致性
    - _需求: 需求 11.3_

  - [ ] 12.3 实现批量处理优化
    - 实现大批量清理操作的分批处理
    - 优化批处理的性能和资源使用
    - 实现合理的批处理大小控制
    - _需求: 需求 11.1_

  - [ ] 12.4 编写性能优化属性测试
    - **属性 25: 批量处理性能优化**
    - **属性 26: 分页查询性能保证**
    - **属性 27: 缓存数据一致性**
    - **属性 28: 执行进度反馈准确性**
    - **验证需求: 需求 11.1, 11.2, 11.3, 11.5**

- [ ] 13. 实现安全和权限控制
  - [ ] 13.1 实现多租户权限验证
    - 在Cmd层实现租户ID自动设置
    - 在Query层实现租户数据隔离
    - 实现跨租户访问控制
    - _需求: 需求 8.1, 8.2, 8.5_

  - [ ] 13.2 实现操作权限验证
    - 实现策略修改权限验证
    - 实现策略删除权限验证
    - 实现仓库访问权限验证
    - _需求: 需求 8.3, 8.4_

  - [ ] 13.3 编写安全控制属性测试
    - **属性 24: 并发修改冲突检测**
    - **验证需求: 需求 10.3**

- [ ] 14. 实现国际化和错误处理
  - [ ] 14.1 配置国际化支持
    - 配置MessageSource和资源文件
    - 创建中英文消息资源文件
    - 实现错误消息的国际化
    - _需求: 需求 12.1, 12.2_

  - [ ] 14.2 实现全局异常处理
    - 实现业务异常的统一处理
    - 实现参数验证异常的处理
    - 实现系统异常的安全处理
    - 返回详细的错误描述和解决建议
    - _需求: 需求 12.3, 12.4, 12.5_

  - [ ] 14.3 实现审计日志记录
    - 实现策略操作的审计日志
    - 实现执行过程的详细日志
    - 实现清理操作的记录日志
    - 实现审计日志的查询和筛选
    - _需求: 需求 9.1, 9.2, 9.3, 9.4, 9.5_

  - [ ] 14.4 编写国际化和审计属性测试
    - **属性 22: 审计日志查询筛选**
    - **验证需求: 需求 9.5**

- [ ] 15. 集成测试和端到端测试
  - [ ] 15.1 编写集成测试
    - 测试完整的CRUD操作流程
    - 测试策略执行的完整流程
    - 测试多租户数据隔离
    - 测试数据库事务的正确性
    - _需求: 需求 1.1, 1.3, 1.4, 3.1, 8.1, 10.1_

  - [ ] 15.2 编写端到端测试
    - 测试REST API的完整调用链
    - 测试异常场景的处理
    - 测试并发操作的正确性
    - 测试性能要求的满足情况
    - _需求: 需求 3.2, 10.3, 11.1, 11.2_

- [ ] 16. 最终检查点 - 确保所有测试通过
  - 确保所有测试通过，如有问题请询问用户

## 注意事项

- 每个任务都引用了具体的需求编号，确保需求覆盖的完整性
- 属性测试必须使用jqwik框架，每个测试至少运行100次迭代
- 单元测试重点关注边界情况、错误处理和集成点
- 所有代码必须遵循接口开发规范的命名和结构要求
- 数据库表结构必须同时支持MySQL和PostgreSQL
- 国际化消息必须支持中英文两种语言
- 审计功能必须记录所有关键操作的详细信息