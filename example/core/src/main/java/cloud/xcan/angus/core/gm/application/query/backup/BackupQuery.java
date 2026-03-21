package cloud.xcan.angus.core.gm.application.query.backup;

import cloud.xcan.angus.api.commonlink.setting.backup.BackupSettings;
import cloud.xcan.angus.core.gm.domain.backup.Backup;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * 备份查询服务接口
 */
public interface BackupQuery {

  /**
   * 根据ID查找备份并检查是否存在
   */
  Backup findAndCheck(Long id);

  /**
   * 分页查询备份列表，支持全文搜索和标准查询两种模式
   */
  Page<Backup> find(GenericSpecification<Backup> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  /**
   * 获取备份统计信息
   */
  BackupStatsVo getStats();

  /**
   * 查找所有备份
   */
  List<Backup> findAll();

  /**
   * 计算已使用存储空间（所有备份文件大小的总和，单位：字节）
   */
  Long calculateUsedStorageSize();

  /**
   * 获取备份存储路径
   */
  String getStoragePath();

  /**
   * 获取备份设置
   */
  BackupSettings getBackupSettings();

}
