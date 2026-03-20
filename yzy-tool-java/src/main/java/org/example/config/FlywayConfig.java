package org.example.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.File;
import java.util.Map;

/**
 * @author yangzhenyu
 * @version 1.0
 * @description:
 * @date 2023/5/5 13:36
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableTransactionManagement
public class FlywayConfig {
    private final DataSource dataSource;

    // 是否启用 Flyway，true 启用，false 不启用
    @Value("${spring.flyway.enabled: false}")
    private Boolean FLYWAY_ENABLED;
    // 指定 SQL 脚本文件夹路径 这里写主库路径，然后在配置类中转换
    @Value("${spring.flyway.locations: classpath:db/master}")
    private String SQL_LOCATION;
    // 版本更新历史记录表
    @Value("${spring.flyway.table: yzy_db_version}")
    private String VERSION_TABLE;


    // 是否可以无序执行
    @Value("${spring.flyway.out-of-order: false}")
    private Boolean OUT_OF_ORDER;

    // 迁移前校验 SQL 文件是否存在问题
    @Value("${spring.flyway.validate-on-migrate: true}")
    private Boolean VALIDATE_ON_MIGRATE;

    // 编码格式，默认UTF-8
    @Value("${spring.flyway.encoding: UTF-8}")
    private String ENCODING;
    // 迁移sql脚本文件名称的前缀，默认V
    @Value("${spring.flyway.sql-migration-prefix: V}")
    private String SQL_MIGRATION_PREFIX;

    //迁移sql脚本文件名称的分隔符，默认2个下划线__
    @Value("${spring.flyway.sql-migration-separator: __}")
    private String SQL_MIGRATION_SEPARATOR ;

    // 迁移sql脚本文件名称的后缀
    @Value("${spring.flyway.sql-migration-suffixes: .sql}")
    private String SQL_MIGRATION_SUFFIXES ;

    // 非空数据库初始化Flyway时需要打开此开关进行Baseline操作 (如果数据库不是空表，需要设置成 true，否则启动报错)
    @Value("${spring.flyway.baseline-on-migrate: true}")
    private Boolean BASELINE_ON_MIGRATE;

    // 基础版本号  与 baseline-on-migrate: true 搭配使用
    @Value("${spring.flyway.baseline-version: 1}")
    private String BASELINE_VERSION;

    @Primary
    @Bean
    @PostConstruct
    public void migrateOrder() {

        if (FLYWAY_ENABLED) {
            log.info("调用数据库生成工具");
            SQL_LOCATION = SQL_LOCATION.split("/")[0]; // 多数据源的配置
            DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
            Map<String, DataSource> dataSources = ds.getDataSources();
            dataSources.forEach((k, v) -> {
                log.info("正在执行多数据源生成数据库文件 " + k);
                Flyway flyway = Flyway.configure()
                        .dataSource(v)
                        .locations(SQL_LOCATION + File.separator + k)
                        .baselineOnMigrate(BASELINE_ON_MIGRATE)
                        .table(VERSION_TABLE)
                        .outOfOrder(OUT_OF_ORDER)
                        .validateOnMigrate(VALIDATE_ON_MIGRATE)
                        .encoding(ENCODING)
                        .sqlMigrationPrefix(SQL_MIGRATION_PREFIX)
                        .sqlMigrationSeparator(SQL_MIGRATION_SEPARATOR)
                        .sqlMigrationSuffixes(SQL_MIGRATION_SUFFIXES)
                        .baselineVersion(BASELINE_VERSION)
                        .load();
                flyway.migrate();
            });
        }
    }
}
