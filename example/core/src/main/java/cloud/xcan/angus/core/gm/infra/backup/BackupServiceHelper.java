package cloud.xcan.angus.core.gm.infra.backup;

import static cloud.xcan.angus.api.commonlink.setting.Setting.getDefaultBackupSettings;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.setting.Setting;
import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.backup.BackupSettings;
import cloud.xcan.angus.api.commonlink.setting.backup.CompressionLevel;
import cloud.xcan.angus.api.manager.SettingManager;
import cloud.xcan.angus.core.gm.domain.backup.Backup;
import cloud.xcan.angus.core.gm.domain.backup.enums.BackupType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 备份服务工具类 提供数据库备份、文件处理、压缩等工具方法
 */
@Slf4j
@Component
public class BackupServiceHelper {

  @Resource
  @Qualifier("dataSource")
  private DataSource dataSource;

  @Resource
  private Environment environment;

  @Resource
  private SettingManager settingManager;

  @Resource
  private ObjectMapper objectMapper;

  @Value("${xcan.datasource.extra.dbType:MYSQL}")
  private String dbType;

  /**
   * 超大文件阈值（1GB），超过此大小的文件需要特殊处理
   */
  private static final long LARGE_FILE_THRESHOLD = 1024L * 1024L * 1024L;

  /**
   * 流式处理缓冲区大小（8MB）
   */
  private static final int BUFFER_SIZE = 8 * 1024 * 1024;


  /**
   * 表名缓存（避免每次都查询数据库） 缓存格式：catalog -> List<tableName>
   */
  private volatile Map<String, List<String>> tableNameCache = new ConcurrentHashMap<>();

  /**
   * 表名缓存时间戳（用于缓存失效） 缓存格式：catalog -> timestamp
   */
  private volatile Map<String, Long> tableNameCacheTimestamp = new ConcurrentHashMap<>();

  /**
   * 表名缓存有效期（毫秒），默认5分钟
   */
  private static final long TABLE_NAME_CACHE_TTL = 5 * 60 * 1000L;

  // ==================== 数据库备份相关方法 ====================

  /**
   * 备份MySQL数据库（全量）- 使用纯SQL方式
   */
  public void backupMySQLDatabase(DatabaseConnectionInfo dbInfo, String outputFile)
      throws Exception {
    log.info("开始使用SQL方式备份MySQL数据库");

    try (java.sql.Connection connection = dataSource.getConnection();
        FileOutputStream fos = new FileOutputStream(outputFile);
        java.io.PrintWriter writer = new java.io.PrintWriter(
            new java.io.OutputStreamWriter(fos, "UTF-8"))) {

      // 设置MySQL会话参数
      try (java.sql.Statement stmt = connection.createStatement()) {
        stmt.execute("SET NAMES utf8mb4");
        stmt.execute("SET FOREIGN_KEY_CHECKS=0");
      }

      writer.println("-- MySQL数据库备份");
      writer.println("-- 备份时间: " + LocalDateTime.now());
      writer.println("-- 数据库: " + dbInfo.getDatabase());
      writer.println("SET NAMES utf8mb4;");
      writer.println("SET FOREIGN_KEY_CHECKS=0;");
      writer.println();

      // 获取所有表名
      List<String> tableNames = getTableNames();
      log.info("找到 {} 个表需要备份", tableNames.size());

      // 备份每个表的结构和数据
      for (String tableName : tableNames) {
        try {
          // 1. 备份表结构
          backupMySQLTableStructure(connection, writer, tableName);

          // 2. 备份表数据
          backupMySQLTableData(connection, writer, tableName, null);

          writer.println();
        } catch (Exception e) {
          log.error("备份表 {} 失败：{}", tableName, e.getMessage(), e);
          writer.println("-- 警告：表 " + tableName + " 备份失败：" + e.getMessage());
        }
      }

      writer.println("SET FOREIGN_KEY_CHECKS=1;");
      writer.flush();

      log.info("MySQL数据库备份完成：{}", outputFile);
    }
  }

  /**
   * 备份MySQL表结构
   */
  private void backupMySQLTableStructure(java.sql.Connection connection,
      java.io.PrintWriter writer, String tableName) throws Exception {
    writer.println("-- ----------------------------");
    writer.println("-- Table structure for " + tableName);
    writer.println("-- ----------------------------");
    writer.println("DROP TABLE IF EXISTS `" + tableName + "`;");

    try (java.sql.Statement stmt = connection.createStatement();
        java.sql.ResultSet rs = stmt.executeQuery("SHOW CREATE TABLE `" + tableName + "`")) {
      if (rs.next()) {
        String createTableSql = rs.getString("Create Table");
        writer.println(createTableSql + ";");
      }
    }
    writer.println();
  }

  /**
   * 备份MySQL表数据
   */
  private void backupMySQLTableData(java.sql.Connection connection,
      java.io.PrintWriter writer, String tableName, Long lastMaxId) throws Exception {
    // 构建WHERE条件
    String whereClause = lastMaxId != null ? " WHERE id > " + lastMaxId : "";

    // 检查表是否有数据
    String countSql = "SELECT COUNT(*) FROM `" + tableName + "`" + whereClause;
    try (java.sql.Statement stmt = connection.createStatement();
        java.sql.ResultSet rs = stmt.executeQuery(countSql)) {
      if (rs.next() && rs.getLong(1) == 0) {
        writer.println("-- Table " + tableName + " has no data");
        return;
      }
    }

    writer.println("-- ----------------------------");
    writer.println("-- Data for table " + tableName);
    writer.println("-- ----------------------------");

    // 获取列信息
    java.sql.DatabaseMetaData metaData = connection.getMetaData();
    String catalog = connection.getCatalog();
    java.sql.ResultSet columns = metaData.getColumns(catalog, null, tableName, null);
    List<String> columnNames = new ArrayList<>();
    List<Integer> columnTypes = new ArrayList<>();
    while (columns.next()) {
      columnNames.add(columns.getString("COLUMN_NAME"));
      columnTypes.add(columns.getInt("DATA_TYPE"));
    }
    columns.close();

    if (columnNames.isEmpty()) {
      return;
    }

    // 查询数据并生成INSERT语句
    String selectSql = "SELECT * FROM `" + tableName + "`" + whereClause + " ORDER BY id";
    try (java.sql.Statement stmt = connection.createStatement(
        java.sql.ResultSet.TYPE_FORWARD_ONLY,
        java.sql.ResultSet.CONCUR_READ_ONLY);
        java.sql.ResultSet rs = stmt.executeQuery(selectSql)) {

      // 设置fetch size以避免内存溢出
      stmt.setFetchSize(1000);

      int batchCount = 0;
      writer.print("INSERT INTO `" + tableName + "` (`");
      writer.print(String.join("`, `", columnNames));
      writer.println("`) VALUES");

      boolean firstRow = true;
      while (rs.next()) {
        if (!firstRow) {
          writer.println(",");
        }
        writer.print("(");

        for (int i = 0; i < columnNames.size(); i++) {
          if (i > 0) {
            writer.print(", ");
          }
          Object value = rs.getObject(i + 1);
          writer.print(formatSqlValueForMySQL(value, columnTypes.get(i)));
        }

        writer.print(")");
        firstRow = false;
        batchCount++;

        // 每1000条记录换行，提高可读性
        if (batchCount % 1000 == 0) {
          writer.println(";");
          writer.print("INSERT INTO `" + tableName + "` (`");
          writer.print(String.join("`, `", columnNames));
          writer.println("`) VALUES");
          firstRow = true;
        }
      }

      if (!firstRow) {
        writer.println(";");
      }
    }
  }

  /**
   * 备份MySQL数据库（增量）
   */
  public void backupMySQLDatabaseIncremental(DatabaseConnectionInfo dbInfo, String outputFile,
      Long lastMaxId) throws Exception {
    log.info("开始使用SQL方式备份MySQL数据库（增量），上次最大ID：{}", lastMaxId);

    try (java.sql.Connection connection = dataSource.getConnection();
        FileOutputStream fos = new FileOutputStream(outputFile);
        java.io.PrintWriter writer = new java.io.PrintWriter(
            new java.io.OutputStreamWriter(fos, "UTF-8"))) {

      writer.println("-- MySQL增量备份");
      writer.println("-- 备份时间: " + LocalDateTime.now());
      writer.println("-- 上次最大ID: " + lastMaxId);
      writer.println("SET FOREIGN_KEY_CHECKS=0;");
      writer.println();

      List<String> tableNames = getTableNames();

      for (String tableName : tableNames) {
        try {
          // 只备份增量数据，不备份表结构
          backupMySQLTableData(connection, writer, tableName, lastMaxId);
          writer.println();
        } catch (Exception e) {
          log.debug("跳过表：{}，原因：{}", tableName, e.getMessage());
        }
      }

      writer.println("SET FOREIGN_KEY_CHECKS=1;");
      writer.flush();

      log.info("MySQL增量备份完成：{}", outputFile);
    }
  }

  /**
   * 备份PostgreSQL数据库（全量）- 使用纯SQL方式
   */
  public void backupPostgreSQLDatabase(DatabaseConnectionInfo dbInfo, String outputFile)
      throws Exception {
    log.info("开始使用SQL方式备份PostgreSQL数据库");

    try (java.sql.Connection connection = dataSource.getConnection();
        FileOutputStream fos = new FileOutputStream(outputFile);
        java.io.PrintWriter writer = new java.io.PrintWriter(
            new java.io.OutputStreamWriter(fos, "UTF-8"))) {

      writer.println("-- PostgreSQL数据库备份");
      writer.println("-- 备份时间: " + LocalDateTime.now());
      writer.println("-- 数据库: " + dbInfo.getDatabase());
      writer.println();

      // 获取所有表名
      List<String> tableNames = getTableNames();
      log.info("找到 {} 个表需要备份", tableNames.size());

      // 备份每个表的结构和数据
      for (String tableName : tableNames) {
        try {
          // 1. 备份表结构
          backupPostgreSQLTableStructure(connection, writer, tableName);

          // 2. 备份表数据
          backupPostgreSQLTableData(connection, writer, tableName, null);

          writer.println();
        } catch (Exception e) {
          log.error("备份表 {} 失败：{}", tableName, e.getMessage(), e);
          writer.println("-- 警告：表 " + tableName + " 备份失败：" + e.getMessage());
        }
      }

      writer.flush();

      log.info("PostgreSQL数据库备份完成：{}", outputFile);
    }
  }

  /**
   * 备份PostgreSQL表结构
   */
  private void backupPostgreSQLTableStructure(java.sql.Connection connection,
      java.io.PrintWriter writer, String tableName) throws Exception {
    writer.println("-- ----------------------------");
    writer.println("-- Table structure for " + tableName);
    writer.println("-- ----------------------------");
    writer.println("DROP TABLE IF EXISTS \"" + tableName + "\" CASCADE;");
    writer.println();

    // 查询表结构定义
    String sql = "SELECT " +
        "    'CREATE TABLE \"' || tablename || '\" (' || " +
        "    string_agg(" +
        "        '\"' || column_name || '\" ' || " +
        "        CASE " +
        "            WHEN data_type = 'character varying' THEN 'VARCHAR(' || character_maximum_length || ')' "
        +
        "            WHEN data_type = 'character' THEN 'CHAR(' || character_maximum_length || ')' "
        +
        "            WHEN data_type = 'numeric' THEN 'NUMERIC(' || numeric_precision || ',' || numeric_scale || ')' "
        +
        "            WHEN data_type = 'timestamp without time zone' THEN 'TIMESTAMP' " +
        "            WHEN data_type = 'timestamp with time zone' THEN 'TIMESTAMPTZ' " +
        "            ELSE UPPER(data_type) " +
        "        END || " +
        "        CASE WHEN is_nullable = 'NO' THEN ' NOT NULL' ELSE '' END || " +
        "        CASE WHEN column_default IS NOT NULL THEN ' DEFAULT ' || column_default ELSE '' END, "
        +
        "        ', ' ORDER BY ordinal_position" +
        "    ) || " +
        "    ');' AS create_table_sql " +
        "FROM information_schema.columns " +
        "WHERE table_schema = 'public' AND table_name = ? " +
        "GROUP BY tablename";

    try (java.sql.PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, tableName);
      try (java.sql.ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          writer.println(rs.getString("create_table_sql"));
        } else {
          // 如果查询失败，尝试使用pg_get_tabledef（需要安装扩展）
          writer.println("-- 无法获取表结构，请手动创建表");
        }
      }
    }
    writer.println();
  }

  /**
   * 备份PostgreSQL表数据
   */
  private void backupPostgreSQLTableData(java.sql.Connection connection,
      java.io.PrintWriter writer, String tableName, Long lastMaxId) throws Exception {
    // 构建WHERE条件
    String whereClause = lastMaxId != null ? " WHERE id > " + lastMaxId : "";

    // 检查表是否有数据
    String countSql = "SELECT COUNT(*) FROM \"" + tableName + "\"" + whereClause;
    try (java.sql.Statement stmt = connection.createStatement();
        java.sql.ResultSet rs = stmt.executeQuery(countSql)) {
      if (rs.next() && rs.getLong(1) == 0) {
        writer.println("-- Table " + tableName + " has no data");
        return;
      }
    }

    writer.println("-- ----------------------------");
    writer.println("-- Data for table " + tableName);
    writer.println("-- ----------------------------");

    // 获取列信息
    java.sql.DatabaseMetaData metaData = connection.getMetaData();
    java.sql.ResultSet columns = metaData.getColumns(null, "public", tableName, null);
    List<String> columnNames = new ArrayList<>();
    List<Integer> columnTypes = new ArrayList<>();
    while (columns.next()) {
      columnNames.add(columns.getString("COLUMN_NAME"));
      columnTypes.add(columns.getInt("DATA_TYPE"));
    }
    columns.close();

    if (columnNames.isEmpty()) {
      return;
    }

    // 查询数据并生成INSERT语句
    String selectSql = "SELECT * FROM \"" + tableName + "\"" + whereClause + " ORDER BY id";
    try (java.sql.Statement stmt = connection.createStatement(
        java.sql.ResultSet.TYPE_FORWARD_ONLY,
        java.sql.ResultSet.CONCUR_READ_ONLY);
        java.sql.ResultSet rs = stmt.executeQuery(selectSql)) {

      // 设置fetch size以避免内存溢出
      stmt.setFetchSize(1000);

      int batchCount = 0;
      writer.print("INSERT INTO \"" + tableName + "\" (\"");
      writer.print(String.join("\", \"", columnNames));
      writer.println("\") VALUES");

      boolean firstRow = true;
      while (rs.next()) {
        if (!firstRow) {
          writer.println(",");
        }
        writer.print("(");

        for (int i = 0; i < columnNames.size(); i++) {
          if (i > 0) {
            writer.print(", ");
          }
          Object value = rs.getObject(i + 1);
          writer.print(formatSqlValueForPostgreSQL(value, columnTypes.get(i)));
        }

        writer.print(")");
        firstRow = false;
        batchCount++;

        // 每1000条记录换行
        if (batchCount % 1000 == 0) {
          writer.println(";");
          writer.print("INSERT INTO \"" + tableName + "\" (\"");
          writer.print(String.join("\", \"", columnNames));
          writer.println("\") VALUES");
          firstRow = true;
        }
      }

      if (!firstRow) {
        writer.println(";");
      }
    }
  }

  /**
   * 备份PostgreSQL数据库（增量）
   */
  public void backupPostgreSQLDatabaseIncremental(DatabaseConnectionInfo dbInfo, String outputFile,
      Long lastMaxId) throws Exception {
    log.info("开始使用SQL方式备份PostgreSQL数据库（增量），上次最大ID：{}", lastMaxId);

    try (java.sql.Connection connection = dataSource.getConnection();
        FileOutputStream fos = new FileOutputStream(outputFile);
        java.io.PrintWriter writer = new java.io.PrintWriter(
            new java.io.OutputStreamWriter(fos, "UTF-8"))) {

      writer.println("-- PostgreSQL增量备份");
      writer.println("-- 备份时间: " + LocalDateTime.now());
      writer.println("-- 上次最大ID: " + lastMaxId);
      writer.println();

      List<String> tableNames = getTableNames();

      for (String tableName : tableNames) {
        try {
          // 只备份增量数据，不备份表结构
          backupPostgreSQLTableData(connection, writer, tableName, lastMaxId);
          writer.println();
        } catch (Exception e) {
          log.debug("跳过表：{}，原因：{}", tableName, e.getMessage());
        }
      }

      writer.flush();

      log.info("PostgreSQL增量备份完成：{}", outputFile);
    }
  }

  /**
   * 格式化MySQL SQL值
   */
  private String formatSqlValueForMySQL(Object value, int sqlType) {
    if (value == null) {
      return "NULL";
    }

    switch (sqlType) {
      case java.sql.Types.BIT:
      case java.sql.Types.BOOLEAN:
        return ((Boolean) value) ? "1" : "0";

      case java.sql.Types.TINYINT:
      case java.sql.Types.SMALLINT:
      case java.sql.Types.INTEGER:
      case java.sql.Types.BIGINT:
      case java.sql.Types.FLOAT:
      case java.sql.Types.DOUBLE:
      case java.sql.Types.DECIMAL:
      case java.sql.Types.NUMERIC:
        return value.toString();

      case java.sql.Types.DATE:
        return "'" + value.toString() + "'";

      case java.sql.Types.TIME:
        return "'" + value.toString() + "'";

      case java.sql.Types.TIMESTAMP:
        if (value instanceof java.sql.Timestamp) {
          java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          return "'" + sdf.format((java.sql.Timestamp) value) + "'";
        }
        return "'" + value.toString() + "'";

      case java.sql.Types.BLOB:
      case java.sql.Types.BINARY:
      case java.sql.Types.VARBINARY:
      case java.sql.Types.LONGVARBINARY:
        // BLOB类型需要转换为十六进制
        try {
          byte[] bytes = (byte[]) value;
          return "0x" + bytesToHex(bytes);
        } catch (Exception e) {
          log.warn("BLOB转换失败，使用NULL", e);
          return "NULL";
        }

      case java.sql.Types.CLOB:
      case java.sql.Types.LONGVARCHAR:
        // CLOB类型
        return "'" + escapeSqlForMySQL(value.toString()) + "'";

      default:
        // 字符串类型
        return "'" + escapeSqlForMySQL(value.toString()) + "'";
    }
  }

  /**
   * MySQL SQL转义
   */
  private String escapeSqlForMySQL(String str) {
    if (str == null) {
      return null;
    }
    return str.replace("\\", "\\\\")
        .replace("'", "''")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
        .replace("\0", "\\0")
        .replace("\u001a", "\\Z"); // Ctrl+Z
  }

  /**
   * 格式化PostgreSQL SQL值
   */
  private String formatSqlValueForPostgreSQL(Object value, int sqlType) {
    if (value == null) {
      return "NULL";
    }

    switch (sqlType) {
      case java.sql.Types.BIT:
      case java.sql.Types.BOOLEAN:
        return ((Boolean) value) ? "TRUE" : "FALSE";

      case java.sql.Types.TINYINT:
      case java.sql.Types.SMALLINT:
      case java.sql.Types.INTEGER:
      case java.sql.Types.BIGINT:
      case java.sql.Types.FLOAT:
      case java.sql.Types.DOUBLE:
      case java.sql.Types.DECIMAL:
      case java.sql.Types.NUMERIC:
        return value.toString();

      case java.sql.Types.DATE:
        return "'" + value.toString() + "'";

      case java.sql.Types.TIME:
        return "'" + value.toString() + "'";

      case java.sql.Types.TIMESTAMP:
        if (value instanceof java.sql.Timestamp) {
          java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          return "'" + sdf.format((java.sql.Timestamp) value) + "'";
        }
        return "'" + value.toString() + "'";

      case java.sql.Types.BLOB:
      case java.sql.Types.BINARY:
      case java.sql.Types.VARBINARY:
      case java.sql.Types.LONGVARBINARY:
        // PostgreSQL使用BYTEA类型，需要转换为十六进制
        try {
          byte[] bytes = (byte[]) value;
          return "'\\x" + bytesToHex(bytes) + "'";
        } catch (Exception e) {
          log.warn("BLOB转换失败，使用NULL", e);
          return "NULL";
        }

      case java.sql.Types.CLOB:
      case java.sql.Types.LONGVARCHAR:
        return "'" + escapeSqlForPostgreSQL(value.toString()) + "'";

      default:
        // 字符串类型
        return "'" + escapeSqlForPostgreSQL(value.toString()) + "'";
    }
  }

  /**
   * PostgreSQL SQL转义
   */
  private String escapeSqlForPostgreSQL(String str) {
    if (str == null) {
      return null;
    }
    return str.replace("'", "''")
        .replace("\\", "\\\\");
  }

  /**
   * 字节数组转十六进制字符串
   */
  private String bytesToHex(byte[] bytes) {
    StringBuilder hex = new StringBuilder();
    for (byte b : bytes) {
      hex.append(String.format("%02x", b));
    }
    return hex.toString();
  }

  /**
   * 获取所有表名
   */
  public List<String> getTableNames() {
    List<String> tableNames = new ArrayList<>();
    try (java.sql.Connection connection = dataSource.getConnection()) {
      java.sql.DatabaseMetaData metaData = connection.getMetaData();
      String catalog = connection.getCatalog();
      java.sql.ResultSet tables = metaData.getTables(catalog, null, null, new String[]{"TABLE"});
      while (tables.next()) {
        String tableName = tables.getString("TABLE_NAME");
        tableNames.add(tableName);
      }
    } catch (Exception e) {
      log.error("获取表名失败", e);
    }
    return tableNames;
  }

  /**
   * 获取当前数据库的最大ID 使用表名缓存优化性能，根据数据库类型正确转义表名，只查询当前schema的表
   */
  public Long getCurrentMaxId() {
    try (Connection connection = dataSource.getConnection()) {
      DatabaseMetaData metaData = connection.getMetaData();
      String catalog = connection.getCatalog();
      String schema = connection.getSchema();

      // 获取表名列表（使用缓存）
      List<String> tableNames = getTableNamesWithCache(catalog, schema, metaData);

      if (tableNames.isEmpty()) {
        log.debug("当前数据库中没有找到表");
        return null;
      }

      log.debug("开始查询 {} 个表的最大ID", tableNames.size());

      Long maxId = null;
      int successCount = 0;
      int skipCount = 0;

      for (String tableName : tableNames) {
        try {
          // 根据数据库类型转义表名
          String escapedTableName = escapeTableName(tableName);
          String sql = "SELECT MAX(id) FROM " + escapedTableName;

          try (java.sql.Statement stmt = connection.createStatement();
              ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
              Object maxIdObj = rs.getObject(1);
              if (maxIdObj != null) {
                Long tableMaxId = ((Number) maxIdObj).longValue();
                if (maxId == null || tableMaxId > maxId) {
                  maxId = tableMaxId;
                }
                successCount++;
              }
            }
          }
        } catch (Exception e) {
          // 表可能没有id字段，跳过
          skipCount++;
          log.debug("表 {} 没有id字段或查询失败：{}", tableName, e.getMessage());
        }
      }

      log.debug("查询完成：成功 {} 个表，跳过 {} 个表，最大ID：{}", successCount, skipCount, maxId);
      return maxId;
    } catch (Exception e) {
      log.error("获取当前数据库最大ID失败", e);
      return null;
    }
  }

  /**
   * 获取表名列表（带缓存） 只查询当前schema的表
   */
  private List<String> getTableNamesWithCache(String catalog, String schema,
      DatabaseMetaData metaData) {
    String cacheKey = catalog != null ? catalog : (schema != null ? schema : "default");

    // 检查缓存是否有效
    Long cacheTime = tableNameCacheTimestamp.get(cacheKey);
    if (cacheTime != null && (System.currentTimeMillis() - cacheTime) < TABLE_NAME_CACHE_TTL) {
      List<String> cachedTables = tableNameCache.get(cacheKey);
      if (cachedTables != null) {
        log.debug("使用缓存的表名列表，共 {} 个表", cachedTables.size());
        return new ArrayList<>(cachedTables);
      }
    }

    // 缓存失效或不存在，重新查询
    List<String> tableNames = new ArrayList<>();
    try {
      // 只查询当前schema的表（使用catalog和schema参数）
      // catalog: 数据库名称（MySQL）或null（PostgreSQL）
      // schema: schema名称（PostgreSQL）或null（MySQL）
      ResultSet tables = metaData.getTables(catalog, schema, null, new String[]{"TABLE"});
      while (tables.next()) {
        String tableName = tables.getString("TABLE_NAME");
        tableNames.add(tableName);
      }

      // 更新缓存
      tableNameCache.put(cacheKey, new ArrayList<>(tableNames));
      tableNameCacheTimestamp.put(cacheKey, System.currentTimeMillis());

      log.debug("查询到 {} 个表，已更新缓存", tableNames.size());
    } catch (Exception e) {
      log.warn("获取表名列表失败，使用空列表：{}", e.getMessage());
    }

    return tableNames;
  }

  /**
   * 根据数据库类型转义表名 MySQL使用反引号 `，PostgreSQL使用双引号 "
   */
  private String escapeTableName(String tableName) {
    if ("POSTGRES".equalsIgnoreCase(dbType) || "POSTGRESQL".equalsIgnoreCase(dbType)) {
      // PostgreSQL使用双引号转义
      return "\"" + tableName + "\"";
    } else {
      // MySQL使用反引号转义（默认）
      return "`" + tableName + "`";
    }
  }

  /**
   * 清除表名缓存（用于测试或手动刷新）
   */
  public void clearTableNameCache() {
    tableNameCache.clear();
    tableNameCacheTimestamp.clear();
    log.info("表名缓存已清除");
  }

  /**
   * 获取数据库连接信息 优先从DataSource获取，如果失败则从环境变量获取
   */
  public DatabaseConnectionInfo getDatabaseConnectionInfo() throws Exception {
    DatabaseConnectionInfo info = new DatabaseConnectionInfo();
    boolean fromDataSource = false;

    // 优先从DataSource获取连接信息
    if (dataSource instanceof HikariDataSource) {
      try {
        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        String jdbcUrl = hikariDataSource.getJdbcUrl();
        info.setUsername(hikariDataSource.getUsername());
        info.setPassword(hikariDataSource.getPassword());

        // 解析JDBC URL（正确处理特殊字符）
        if (jdbcUrl.startsWith("jdbc:mysql://")) {
          parseMySQLUrl(jdbcUrl, info);
          fromDataSource = true;
        } else if (jdbcUrl.startsWith("jdbc:postgresql://")) {
          parsePostgreSQLUrl(jdbcUrl, info);
          fromDataSource = true;
        } else {
          log.warn("不支持的JDBC URL格式：{}，将从环境变量获取连接信息", jdbcUrl);
        }
      } catch (Exception e) {
        log.warn("从DataSource获取连接信息失败：{}，将从环境变量获取", e.getMessage());
      }
    } else {
      log.debug("DataSource不是HikariDataSource类型，将从环境变量获取连接信息");
    }

    // 如果从DataSource获取失败，从环境变量获取
    if (!fromDataSource) {
      log.info("从环境变量获取数据库连接信息");
      if ("MYSQL".equalsIgnoreCase(dbType)) {
        info.setHost(environment.getProperty("GM_MYSQL_HOST", "localhost"));
        info.setPort(environment.getProperty("GM_MYSQL_PORT", "3306"));
        info.setDatabase(environment.getProperty("GM_MYSQL_DB", "angus_gm"));
        info.setUsername(environment.getProperty("GM_MYSQL_USER", "root"));
        info.setPassword(environment.getProperty("GM_MYSQL_PASSWORD", ""));
      } else if ("POSTGRES".equalsIgnoreCase(dbType)) {
        info.setHost(environment.getProperty("GM_POSTGRES_HOST", "localhost"));
        info.setPort(environment.getProperty("GM_POSTGRES_PORT", "5432"));
        info.setDatabase(environment.getProperty("GM_POSTGRES_DB", "angus_gm"));
        info.setUsername(environment.getProperty("GM_POSTGRES_USER", "postgres"));
        info.setPassword(environment.getProperty("GM_POSTGRES_PASSWORD", ""));
      } else {
        throw new UnsupportedOperationException("不支持的数据库类型：" + dbType);
      }
    }

    // 验证连接信息是否完整
    validateConnectionInfo(info);

    return info;
  }

  /**
   * 解析MySQL JDBC URL 正确处理URL中的特殊字符（如密码中的@符号）
   */
  private void parseMySQLUrl(String jdbcUrl, DatabaseConnectionInfo info) throws Exception {
    // MySQL URL格式：jdbc:mysql://[host][:port]/[database][?propertyName1=propertyValue1[&propertyName2=propertyValue2]...]
    // 注意：如果用户名或密码包含特殊字符，需要使用URL编码

    String urlWithoutPrefix = jdbcUrl.substring(13); // 去掉 "jdbc:mysql://"

    // 查找第一个'/'，之前是host:port，之后是database和参数
    int slashIndex = urlWithoutPrefix.indexOf('/');
    if (slashIndex == -1) {
      throw new IllegalArgumentException("MySQL URL格式错误，缺少数据库名：" + jdbcUrl);
    }

    String hostPort = urlWithoutPrefix.substring(0, slashIndex);
    String dbAndParams = urlWithoutPrefix.substring(slashIndex + 1);

    // 解析host和port
    int colonIndex = hostPort.indexOf(':');
    if (colonIndex > 0) {
      info.setHost(hostPort.substring(0, colonIndex));
      try {
        info.setPort(hostPort.substring(colonIndex + 1));
      } catch (Exception e) {
        info.setPort("3306");
      }
    } else {
      info.setHost(hostPort);
      info.setPort("3306");
    }

    // 解析数据库名（去掉参数部分）
    int questionMarkIndex = dbAndParams.indexOf('?');
    if (questionMarkIndex > 0) {
      info.setDatabase(dbAndParams.substring(0, questionMarkIndex));
    } else {
      info.setDatabase(dbAndParams);
    }

    // 验证解析结果
    if (info.getHost() == null || info.getHost().isEmpty()) {
      throw new IllegalArgumentException("无法从MySQL URL中解析host：" + jdbcUrl);
    }
    if (info.getDatabase() == null || info.getDatabase().isEmpty()) {
      throw new IllegalArgumentException("无法从MySQL URL中解析database：" + jdbcUrl);
    }
  }

  /**
   * 解析PostgreSQL JDBC URL 正确处理URL中的特殊字符
   */
  private void parsePostgreSQLUrl(String jdbcUrl, DatabaseConnectionInfo info) throws Exception {
    // PostgreSQL URL格式：jdbc:postgresql://[host][:port]/[database][?propertyName1=propertyValue1[&propertyName2=propertyValue2]...]

    String urlWithoutPrefix = jdbcUrl.substring(18); // 去掉 "jdbc:postgresql://"

    // 查找第一个'/'，之前是host:port，之后是database和参数
    int slashIndex = urlWithoutPrefix.indexOf('/');
    if (slashIndex == -1) {
      throw new IllegalArgumentException("PostgreSQL URL格式错误，缺少数据库名：" + jdbcUrl);
    }

    String hostPort = urlWithoutPrefix.substring(0, slashIndex);
    String dbAndParams = urlWithoutPrefix.substring(slashIndex + 1);

    // 解析host和port
    int colonIndex = hostPort.indexOf(':');
    if (colonIndex > 0) {
      info.setHost(hostPort.substring(0, colonIndex));
      try {
        info.setPort(hostPort.substring(colonIndex + 1));
      } catch (Exception e) {
        info.setPort("5432");
      }
    } else {
      info.setHost(hostPort);
      info.setPort("5432");
    }

    // 解析数据库名（去掉参数部分）
    int questionMarkIndex = dbAndParams.indexOf('?');
    if (questionMarkIndex > 0) {
      info.setDatabase(dbAndParams.substring(0, questionMarkIndex));
    } else {
      info.setDatabase(dbAndParams);
    }

    // 验证解析结果
    if (info.getHost() == null || info.getHost().isEmpty()) {
      throw new IllegalArgumentException("无法从PostgreSQL URL中解析host：" + jdbcUrl);
    }
    if (info.getDatabase() == null || info.getDatabase().isEmpty()) {
      throw new IllegalArgumentException("无法从PostgreSQL URL中解析database：" + jdbcUrl);
    }
  }

  /**
   * 验证数据库连接信息是否完整
   */
  private void validateConnectionInfo(DatabaseConnectionInfo info) {
    List<String> missingFields = new ArrayList<>();

    if (info.getHost() == null || info.getHost().trim().isEmpty()) {
      missingFields.add("host");
    }

    if (info.getPort() == null || info.getPort().trim().isEmpty()) {
      missingFields.add("port");
    } else {
      // 验证端口号格式
      try {
        int port = Integer.parseInt(info.getPort());
        if (port <= 0 || port > 65535) {
          missingFields.add("port (无效的端口号: " + port + ")");
        }
      } catch (NumberFormatException e) {
        missingFields.add("port (格式错误: " + info.getPort() + ")");
      }
    }

    if (info.getDatabase() == null || info.getDatabase().trim().isEmpty()) {
      missingFields.add("database");
    }

    if (info.getUsername() == null || info.getUsername().trim().isEmpty()) {
      missingFields.add("username");
    }

    // 密码可以为空（某些数据库允许空密码）
    // 但通常应该有密码，这里只记录警告
    if (info.getPassword() == null) {
      log.warn("数据库密码为空，这可能不安全");
    }

    if (!missingFields.isEmpty()) {
      throw new IllegalStateException(
          "数据库连接信息不完整，缺少以下字段：" + String.join(", ", missingFields));
    }

    log.debug("数据库连接信息验证通过：host={}, port={}, database={}, username={}",
        info.getHost(), info.getPort(), info.getDatabase(), info.getUsername());
  }

  // ==================== 文件处理相关方法 ====================

  /**
   * 复制目录并提取文件元数据索引
   */
  public List<FileMetadata> copyDirectoryWithMetadata(Path source, Path target,
      Path basePath, BackupType backupType, Backup backup) throws IOException {
    List<FileMetadata> metadataList = new ArrayList<>();
    Files.createDirectories(target);

    Files.walk(source).forEach(sourcePath -> {
      try {
        Path relativePath = source.relativize(sourcePath);
        Path targetPath = target.resolve(relativePath);
        Path relativeToBase = basePath.relativize(sourcePath);

        if (Files.isDirectory(sourcePath)) {
          Files.createDirectories(targetPath);
        } else {
          Files.copy(sourcePath, targetPath);

          // 提取文件元数据并记录ID
          FileMetadata metadata = extractFileMetadata(sourcePath, relativeToBase, backupType,
              backup);
          if (metadata != null) {
            metadataList.add(metadata);
          }
        }
      } catch (IOException e) {
        log.error("复制文件失败：{} -> {}", sourcePath, target, e);
      }
    });

    return metadataList;
  }

  /**
   * 增量备份data目录（带元数据索引）
   */
  public List<FileMetadata> backupDataDirectoryIncremental(Path sourceDir, Path targetDir,
      Path basePath, Backup backup, BackupService backupService) throws Exception {
    List<FileMetadata> metadataList = new ArrayList<>();

    // 查找上一次备份
    Backup lastBackup = backupService.findLastFullBackup(backup);
    if (lastBackup == null) {
      // 没有上次备份，执行全量备份
      return copyDirectoryWithMetadata(sourceDir, targetDir, basePath, BackupType.FULL, backup);
    }

    // 获取上次备份的文件元数据索引
    List<FileMetadata> lastFileMetadata = getLastBackupFileMetadata(lastBackup);
    if (lastFileMetadata == null || lastFileMetadata.isEmpty()) {
      // 没有文件元数据索引，执行全量备份
      return copyDirectoryWithMetadata(sourceDir, targetDir, basePath, BackupType.FULL, backup);
    }

    // 获取上次备份的最大ID
    Long lastMaxId = getLastBackupMaxId(lastBackup);

    // 构建文件元数据索引Map（以相对路径为key）
    java.util.Map<String, FileMetadata> lastMetadataMap = new java.util.HashMap<>();
    for (FileMetadata metadata : lastFileMetadata) {
      lastMetadataMap.put(metadata.getRelativePath(), metadata);
    }

    // 遍历源目录，只备份需要备份的文件
    Files.createDirectories(targetDir);
    Files.walk(sourceDir).forEach(sourcePath -> {
      try {
        Path relativePath = sourceDir.relativize(sourcePath);
        Path targetPath = targetDir.resolve(relativePath);
        Path relativeToBase = basePath.relativize(sourcePath);
        String relativePathStr = relativeToBase.toString().replace("\\", "/");

        if (Files.isDirectory(sourcePath)) {
          Files.createDirectories(targetPath);
        } else {
          // 判断文件是否需要备份
          if (shouldBackupFile(sourcePath, relativePathStr, lastMetadataMap, lastMaxId)) {
            Files.copy(sourcePath, targetPath);

            // 提取文件元数据并记录ID
            FileMetadata metadata = extractFileMetadata(sourcePath, relativeToBase,
                BackupType.INCREMENTAL, backup);
            if (metadata != null) {
              metadataList.add(metadata);
            }
          }
        }
      } catch (IOException e) {
        log.error("备份文件失败：{}", sourcePath, e);
      }
    });

    return metadataList;
  }

  /**
   * 判断文件是否需要备份（增量备份）- 使用文件元数据索引
   */
  private boolean shouldBackupFile(Path filePath, String relativePath,
      java.util.Map<String, FileMetadata> lastMetadataMap, Long lastMaxId) {
    try {
      FileMetadata lastMetadata = lastMetadataMap.get(relativePath);

      // 如果文件不存在于上次备份中，需要备份
      if (lastMetadata == null) {
        return true;
      }

      // 检查文件是否修改（通过文件哈希值）
      String currentHash = calculateFileHash(filePath);
      if (!currentHash.equals(lastMetadata.getFileHash())) {
        // 文件内容已变更，需要备份
        return true;
      }

      // 如果文件包含ID，检查ID是否大于上次备份的最大ID
      if (lastMaxId != null && lastMetadata.getMaxIdInFile() != null) {
        // 如果文件中的最大ID大于上次备份的最大ID，需要备份
        // 注意：这里需要重新解析文件获取当前最大ID
        Long currentMaxId = extractMaxIdFromFile(filePath);
        if (currentMaxId != null && currentMaxId > lastMaxId) {
          return true;
        }
      }

      // 文件未变更且ID未超过阈值，不需要备份
      return false;
    } catch (Exception e) {
      log.warn("判断文件是否需要备份失败：{}，默认备份", filePath, e);
      // 无法判断时，保守策略：备份
      return true;
    }
  }

  /**
   * 提取文件元数据
   */
  public FileMetadata extractFileMetadata(Path filePath, Path relativePath,
      BackupType backupType, Backup backup) {
    try {
      FileMetadata metadata = new FileMetadata();
      metadata.setRelativePath(relativePath.toString().replace("\\", "/"));
      metadata.setFullPath(filePath.toString());
      metadata.setFileSize(Files.size(filePath));
      metadata.setBackupTime(LocalDateTime.now());

      // 获取文件修改时间
      FileTime lastModifiedTime = Files.getLastModifiedTime(filePath);
      metadata.setLastModifiedTime(
          LocalDateTime.ofInstant(lastModifiedTime.toInstant(),
              java.time.ZoneId.systemDefault()));

      // 计算文件哈希值
      metadata.setFileHash(calculateFileHash(filePath));

      // 检测文件类型
      String fileName = filePath.getFileName().toString().toLowerCase();
      if (fileName.endsWith(".json")) {
        metadata.setFileType("JSON");
      } else if (fileName.endsWith(".xml")) {
        metadata.setFileType("XML");
      } else if (fileName.endsWith(".txt") || fileName.endsWith(".log")) {
        metadata.setFileType("TXT");
      } else {
        metadata.setFileType("BINARY");
      }

      // 提取文件中的ID信息
      extractIdsFromFile(filePath, metadata);

      return metadata;
    } catch (Exception e) {
      log.warn("提取文件元数据失败：{}", filePath, e);
      return null;
    }
  }

  /**
   * 从文件中提取ID信息
   */
  private void extractIdsFromFile(Path filePath, FileMetadata metadata) {
    try {
      String fileName = filePath.getFileName().toString().toLowerCase();

      if (fileName.endsWith(".json")) {
        // JSON文件：解析JSON并查找ID字段
        extractIdsFromJsonFile(filePath, metadata);
      } else if (fileName.endsWith(".xml")) {
        // XML文件：解析XML并查找ID属性或元素
        extractIdsFromXmlFile(filePath, metadata);
      } else {
        // 其他文件：使用正则表达式查找ID模式
        extractIdsFromTextFile(filePath, metadata);
      }
    } catch (Exception e) {
      log.debug("从文件提取ID失败：{}", filePath, e);
    }
  }

  /**
   * 从JSON文件中提取ID
   */
  private void extractIdsFromJsonFile(Path filePath, FileMetadata metadata) {
    try {
      // 限制文件大小，避免大文件导致内存溢出
      long fileSize = Files.size(filePath);
      if (fileSize > 10 * 1024 * 1024) { // 10MB
        log.debug("文件过大，跳过ID提取：{}", filePath);
        return;
      }

      String content = new String(Files.readAllBytes(filePath), "UTF-8");
      JsonNode rootNode = objectMapper.readTree(content);

      Set<Long> ids = new HashSet<>();
      extractIdsFromJsonNode(rootNode, ids);

      if (!ids.isEmpty()) {
        Long maxId = ids.stream().max(Long::compareTo).orElse(null);
        Long minId = ids.stream().min(Long::compareTo).orElse(null);
        metadata.setMaxIdInFile(maxId);
        metadata.setMinIdInFile(minId);

        // 如果ID数量较少，保存所有ID
        if (ids.size() <= 1000) {
          metadata.setIdsInFile(new ArrayList<>(ids));
        }
      }
    } catch (Exception e) {
      log.debug("解析JSON文件失败：{}", filePath, e);
    }
  }

  /**
   * 递归提取JSON节点中的ID
   */
  private void extractIdsFromJsonNode(JsonNode node, Set<Long> ids) {
    if (node == null) {
      return;
    }

    // 检查当前节点的ID字段
    if (node.has("id") && node.get("id").isNumber()) {
      ids.add(node.get("id").asLong());
    }

    // 递归处理子节点
    if (node.isObject()) {
      node.fields().forEachRemaining(entry -> {
        extractIdsFromJsonNode(entry.getValue(), ids);
      });
    } else if (node.isArray()) {
      node.forEach(child -> extractIdsFromJsonNode(child, ids));
    }
  }

  /**
   * 从XML文件中提取ID
   */
  private void extractIdsFromXmlFile(Path filePath, FileMetadata metadata) {
    try {
      // 限制文件大小
      long fileSize = Files.size(filePath);
      if (fileSize > 10 * 1024 * 1024) { // 10MB
        log.debug("文件过大，跳过ID提取：{}", filePath);
        return;
      }

      String content = new String(Files.readAllBytes(filePath), "UTF-8");

      // 使用正则表达式查找ID属性或元素
      Pattern idPattern = Pattern.compile("id\\s*=\\s*[\"'](\\d+)[\"']|id\\s*>\\s*(\\d+)\\s*<",
          Pattern.CASE_INSENSITIVE);
      Matcher matcher = idPattern.matcher(content);

      Set<Long> ids = new HashSet<>();
      while (matcher.find()) {
        String idStr = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
        try {
          ids.add(Long.parseLong(idStr));
        } catch (NumberFormatException e) {
          // 忽略无效的ID
        }
      }

      if (!ids.isEmpty()) {
        Long maxId = ids.stream().max(Long::compareTo).orElse(null);
        Long minId = ids.stream().min(Long::compareTo).orElse(null);
        metadata.setMaxIdInFile(maxId);
        metadata.setMinIdInFile(minId);

        if (ids.size() <= 1000) {
          metadata.setIdsInFile(new ArrayList<>(ids));
        }
      }
    } catch (Exception e) {
      log.debug("解析XML文件失败：{}", filePath, e);
    }
  }

  /**
   * 从文本文件中提取ID（使用正则表达式）
   */
  private void extractIdsFromTextFile(Path filePath, FileMetadata metadata) {
    try {
      // 限制文件大小
      long fileSize = Files.size(filePath);
      if (fileSize > 5 * 1024 * 1024) { // 5MB
        log.debug("文件过大，跳过ID提取：{}", filePath);
        return;
      }

      String content = new String(Files.readAllBytes(filePath), "UTF-8");

      // 查找常见的ID模式：id: 123456 或 "id": 123456 等
      Pattern idPattern = Pattern.compile(
          "(?:\"id\"|'id'|id\\s*[:=])\\s*(\\d{10,})", Pattern.CASE_INSENSITIVE);
      Matcher matcher = idPattern.matcher(content);

      Set<Long> ids = new HashSet<>();
      int count = 0;
      while (matcher.find() && count < 10000) { // 限制匹配数量
        try {
          Long id = Long.parseLong(matcher.group(1));
          ids.add(id);
          count++;
        } catch (NumberFormatException e) {
          // 忽略无效的ID
        }
      }

      if (!ids.isEmpty()) {
        Long maxId = ids.stream().max(Long::compareTo).orElse(null);
        Long minId = ids.stream().min(Long::compareTo).orElse(null);
        metadata.setMaxIdInFile(maxId);
        metadata.setMinIdInFile(minId);

        if (ids.size() <= 1000) {
          metadata.setIdsInFile(new ArrayList<>(ids));
        }
      }
    } catch (Exception e) {
      log.debug("从文本文件提取ID失败：{}", filePath, e);
    }
  }

  /**
   * 从文件中提取最大ID（用于增量备份判断）
   */
  private Long extractMaxIdFromFile(Path filePath) {
    try {
      FileMetadata tempMetadata = new FileMetadata();
      extractIdsFromFile(filePath, tempMetadata);
      return tempMetadata.getMaxIdInFile();
    } catch (Exception e) {
      log.debug("提取文件最大ID失败：{}", filePath, e);
      return null;
    }
  }

  /**
   * 计算文件哈希值（MD5）
   */
  private String calculateFileHash(Path filePath) {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      try (FileInputStream fis = new FileInputStream(filePath.toFile())) {
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
          md.update(buffer, 0, bytesRead);
        }
      }
      byte[] hashBytes = md.digest();
      return bytesToHex(hashBytes);
    } catch (Exception e) {
      log.warn("计算文件哈希值失败：{}", filePath, e);
      return "";
    }
  }

  /**
   * 获取上次备份的文件元数据索引 使用流式读取manifest.json，避免解压整个备份文件
   */
  public List<FileMetadata> getLastBackupFileMetadata(Backup lastBackup) {
    try {
      String backupPath = lastBackup.getBackupPath();
      if (backupPath == null || (!backupPath.endsWith(".bak") && !backupPath.endsWith(".zip"))) {
        log.debug("备份文件路径无效：{}", backupPath);
        return null;
      }

      File backupFile = new File(backupPath);
      if (!backupFile.exists()) {
        log.debug("备份文件不存在：{}", backupPath);
        return null;
      }

      // 使用流式读取，直接从ZIP文件中读取manifest.json
      try (java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(backupPath)) {
        java.util.zip.ZipEntry manifestEntry = zipFile.getEntry("manifest.json");
        if (manifestEntry == null) {
          log.debug("备份文件中未找到manifest.json：{}", backupPath);
          return null;
        }

        // 流式读取manifest.json
        try (java.io.InputStream is = zipFile.getInputStream(manifestEntry)) {
          BackupManifest manifest = objectMapper.readValue(is, BackupManifest.class);

          // 收集所有应用的文件元数据索引
          List<FileMetadata> allMetadata = new ArrayList<>();
          if (manifest.getApplications() != null) {
            for (ApplicationBackupInfo appInfo : manifest.getApplications()) {
              if (appInfo.getFileMetadataIndex() != null) {
                allMetadata.addAll(appInfo.getFileMetadataIndex());
              }
            }
          }

          log.debug("从备份文件 {} 读取到 {} 个文件元数据索引", backupPath, allMetadata.size());
          return allMetadata;
        }
      }
    } catch (java.util.zip.ZipException e) {
      log.warn("备份文件格式错误或已损坏：{}，错误：{}", lastBackup.getBackupPath(), e.getMessage());
      return null;
    } catch (java.io.IOException e) {
      log.warn("读取备份文件失败：{}，错误：{}", lastBackup.getBackupPath(), e.getMessage(), e);
      return null;
    } catch (Exception e) {
      log.warn("读取上次备份的文件元数据索引失败：{}，错误：{}", lastBackup.getBackupPath(),
          e.getMessage(), e);
      return null;
    }
  }

  /**
   * 获取上次备份的最大ID 使用流式读取manifest.json，避免解压整个备份文件
   */
  public Long getLastBackupMaxId(Backup lastBackup) {
    try {
      // 从备份文件中读取清单
      String backupPath = lastBackup.getBackupPath();
      if (backupPath == null || (!backupPath.endsWith(".bak") && !backupPath.endsWith(".zip"))) {
        log.warn("备份文件路径无效或格式不正确：{}", backupPath);
        return null;
      }

      File backupFile = new File(backupPath);
      if (!backupFile.exists()) {
        log.warn("备份文件不存在：{}", backupPath);
        return null;
      }

      // 使用流式读取，直接从ZIP文件中读取manifest.json，无需解压整个文件
      try (java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(backupPath)) {
        java.util.zip.ZipEntry manifestEntry = zipFile.getEntry("manifest.json");
        if (manifestEntry == null) {
          log.warn("备份文件中未找到manifest.json：{}", backupPath);
          return null;
        }

        // 流式读取manifest.json
        try (java.io.InputStream is = zipFile.getInputStream(manifestEntry)) {
          BackupManifest manifest = objectMapper.readValue(is, BackupManifest.class);

          // 返回currentMaxId（上次备份时的最大ID），作为本次增量备份的起始ID
          // 如果currentMaxId为空，则使用lastBackupMaxId（兼容旧版本备份文件）
          Long maxId = manifest.getCurrentMaxId();
          if (maxId == null) {
            maxId = manifest.getLastBackupMaxId();
            if (maxId != null) {
              log.debug("使用lastBackupMaxId（兼容旧版本备份文件）：{}", maxId);
            }
          }

          if (maxId != null) {
            log.debug("从备份文件 {} 读取到最大ID：{}", backupPath, maxId);
          } else {
            log.warn("备份清单中未找到最大ID信息：{}", backupPath);
          }

          return maxId;
        }
      }
    } catch (java.util.zip.ZipException e) {
      log.warn("备份文件格式错误或已损坏：{}，错误：{}", lastBackup.getBackupPath(), e.getMessage());
      return null;
    } catch (java.io.IOException e) {
      log.warn("读取备份文件失败：{}，错误：{}", lastBackup.getBackupPath(), e.getMessage(), e);
      return null;
    } catch (Exception e) {
      log.warn("读取上次备份的最大ID失败：{}，错误：{}", lastBackup.getBackupPath(), e.getMessage(),
          e);
      return null;
    }
  }

  // ==================== 压缩相关方法 ====================

  /**
   * 创建备份压缩包 支持压缩级别配置、流式处理、进度监控、DirectoryStream优化
   */
  public void createBackupArchive(String backupDir, String outputFile) throws Exception {
    log.info("开始创建备份压缩包：{}", outputFile);

    Files.createDirectories(Paths.get(outputFile).getParent());

    // 获取压缩级别配置
    int compressionLevel = getCompressionLevel();

    // 统计信息
    long totalSize = 0;
    long processedSize = 0;
    int fileCount = 0;
    int errorCount = 0;

    Path backupPath = Paths.get(backupDir);

    // 先统计总大小和文件数（用于进度监控）
    ArchiveStats stats = calculateArchiveStats(backupPath);
    totalSize = stats.getTotalSize();
    fileCount = stats.getFileCount();

    log.info("备份统计：总文件数：{}，总大小：{} 字节（{}）",
        fileCount, totalSize, formatFileSize(totalSize));

    try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputFile))) {
      // 设置压缩级别
      zos.setLevel(compressionLevel);

      // 使用DirectoryStream递归遍历目录（性能优于Files.walk）
      Deque<Path> dirStack = new ArrayDeque<>();
      dirStack.push(backupPath);

      while (!dirStack.isEmpty()) {
        Path currentDir = dirStack.pop();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentDir)) {
          for (Path path : stream) {
            try {
              String relativePath = backupPath.relativize(path).toString();
              if (relativePath.isEmpty()) {
                continue;
              }

              if (Files.isDirectory(path)) {
                // 目录：添加到栈中继续处理
                dirStack.push(path);
                zos.putNextEntry(new ZipEntry(relativePath + "/"));
                zos.closeEntry();
              } else {
                // 文件：流式压缩
                long fileSize = Files.size(path);
                processedSize += fileSize;

                // 记录进度（每处理100个文件或每100MB记录一次）
                if (fileCount % 100 == 0 || processedSize % (100 * 1024 * 1024) == 0) {
                  double progress = totalSize > 0 ? (double) processedSize / totalSize * 100 : 0;
                  log.debug("备份进度：{:.2f}% ({}/{})",
                      String.format("%.2f", progress), processedSize, totalSize);
                }

                // 处理超大文件
                if (fileSize > LARGE_FILE_THRESHOLD) {
                  log.warn("发现超大文件：{}，大小：{} 字节（{}），使用流式处理",
                      path, fileSize, formatFileSize(fileSize));
                  compressLargeFile(path, relativePath, zos);
                } else {
                  // 普通文件：直接压缩
                  zos.putNextEntry(new ZipEntry(relativePath));
                  Files.copy(path, zos);
                  zos.closeEntry();
                }

                fileCount++;
              }
            } catch (IOException e) {
              errorCount++;
              log.error("压缩文件失败：{}，错误：{}", path, e.getMessage(), e);
              // 继续处理其他文件，不中断整个备份过程
            }
          }
        } catch (IOException e) {
          errorCount++;
          log.error("遍历目录失败：{}，错误：{}", currentDir, e.getMessage(), e);
        }
      }

      if (errorCount > 0) {
        log.warn("备份过程中发生 {} 个错误，但备份文件已创建", errorCount);
      }

      log.info("备份压缩包创建完成：{}，文件数：{}，总大小：{} 字节（{}），错误数：{}",
          outputFile, fileCount, processedSize, formatFileSize(processedSize), errorCount);
    }
  }

  /**
   * 流式压缩超大文件
   */
  private void compressLargeFile(Path filePath, String relativePath, ZipOutputStream zos)
      throws IOException {
    zos.putNextEntry(new ZipEntry(relativePath));

    try (FileInputStream fis = new FileInputStream(filePath.toFile())) {
      byte[] buffer = new byte[BUFFER_SIZE];
      int bytesRead;
      long totalRead = 0;

      while ((bytesRead = fis.read(buffer)) != -1) {
        zos.write(buffer, 0, bytesRead);
        totalRead += bytesRead;

        // 每处理100MB输出一次进度
        if (totalRead % (100 * 1024 * 1024) == 0) {
          log.debug("超大文件压缩进度：{}，已处理：{} 字节", relativePath, totalRead);
        }
      }
    }

    zos.closeEntry();
  }

  /**
   * 计算归档统计信息（总大小和文件数）
   */
  private ArchiveStats calculateArchiveStats(Path rootPath) throws IOException {
    long totalSize = 0;
    int fileCount = 0;

    Deque<Path> dirStack = new ArrayDeque<>();
    dirStack.push(rootPath);

    while (!dirStack.isEmpty()) {
      Path currentDir = dirStack.pop();

      try (DirectoryStream<Path> stream = Files.newDirectoryStream(currentDir)) {
        for (Path path : stream) {
          if (Files.isDirectory(path)) {
            dirStack.push(path);
          } else {
            try {
              totalSize += Files.size(path);
              fileCount++;
            } catch (IOException e) {
              log.debug("无法获取文件大小：{}", path, e);
            }
          }
        }
      } catch (IOException e) {
        log.debug("遍历目录失败：{}", currentDir, e);
      }
    }

    return new ArchiveStats(totalSize, fileCount);
  }

  /**
   * 获取压缩级别
   */
  private int getCompressionLevel() {
    try {
      Setting settings = settingManager.getSetting0(SettingKey.BACKUP_SETTINGS);
      BackupSettings backupSettings = nullSafe(settings.getBackupSettings(),
          getDefaultBackupSettings());

      CompressionLevel level = backupSettings.getCompressionLevel();
      if (level == null) {
        level = CompressionLevel.STANDARD; // 默认标准压缩
      }

      // 将CompressionLevel枚举转换为Deflater压缩级别
      switch (level) {
        case NONE:
          return Deflater.NO_COMPRESSION; // 0
        case FAST:
          return Deflater.BEST_SPEED; // 1
        case STANDARD:
          return Deflater.DEFAULT_COMPRESSION; // -1 (通常为6)
        case MAXIMUM:
          return Deflater.BEST_COMPRESSION; // 9
        default:
          return Deflater.DEFAULT_COMPRESSION;
      }
    } catch (Exception e) {
      log.warn("获取压缩级别配置失败，使用默认压缩级别：{}", e.getMessage());
      return Deflater.DEFAULT_COMPRESSION;
    }
  }

  /**
   * 格式化文件大小
   */
  private String formatFileSize(long bytes) {
    if (bytes < 1024) {
      return bytes + " B";
    } else if (bytes < 1024 * 1024) {
      return String.format("%.2f KB", bytes / 1024.0);
    } else if (bytes < 1024 * 1024 * 1024) {
      return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    } else {
      return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
  }

  /**
   * 归档统计信息
   */
  private static class ArchiveStats {

    private final long totalSize;
    private final int fileCount;

    public ArchiveStats(long totalSize, int fileCount) {
      this.totalSize = totalSize;
      this.fileCount = fileCount;
    }

    public long getTotalSize() {
      return totalSize;
    }

    public int getFileCount() {
      return fileCount;
    }
  }

  /**
   * 删除目录
   */
  public void deleteDirectory(Path directory) {
    try {
      if (Files.exists(directory)) {
        Files.walk(directory)
            .sorted((a, b) -> b.compareTo(a))
            .forEach(path -> {
              try {
                Files.delete(path);
              } catch (IOException e) {
                log.warn("删除文件失败：{}", path, e);
              }
            });
      }
    } catch (IOException e) {
      log.warn("删除目录失败：{}", directory, e);
    }
  }

  /**
   * 数据库连接信息
   */
  public static class DatabaseConnectionInfo {

    private String host;
    private String port;
    private String username;
    private String password;
    private String database;

    public String getHost() {
      return host;
    }

    public void setHost(String host) {
      this.host = host;
    }

    public String getPort() {
      return port;
    }

    public void setPort(String port) {
      this.port = port;
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public String getDatabase() {
      return database;
    }

    public void setDatabase(String database) {
      this.database = database;
    }
  }
}
