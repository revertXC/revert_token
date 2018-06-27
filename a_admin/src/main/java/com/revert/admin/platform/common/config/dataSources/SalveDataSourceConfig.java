package com.revert.admin.platform.common.config.dataSources;

import com.github.pagehelper.PageHelper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 从数据源
 */
//@Configuration
public class SalveDataSourceConfig {

    @Value("platform.master.dbType")
    private String dbType;

    private String mybatisScanXml = "classpath*:com/"+dbType+"";

    @Bean("salveDataSource")
    @Primary
    @ConfigurationProperties(prefix = "datasource.savle")
    public DataSource salveDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("jdbcTemplateSalve")
    @Primary
    public JdbcTemplate primaryJdbcTemplate(@Qualifier("salveDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


}
