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
import org.springframework.context.annotation.Configuration;
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
 * 主数据源
 */
@Configuration
public class MasterDataSourceConfig {
    private String type="master";


    @Value("platform.master.dbType")
    private String dbType;

    /** 该sqlSessionFactory 扫描 mybatis XML 文件地址*/
    private String mybatisScanXml = "classpath*:com/"+dbType+"/"+type+"/**/impI/*.xml";

    @Bean("masterDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("jdbcTemplate")
    @Primary
    public JdbcTemplate primaryJdbcTemplate(@Qualifier("masterDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * 根据数据源创建SqlSessionFactory
     */
    @Bean(name = "masterSqlSessionFactory")
    public SqlSessionFactory masterSqlSessionFactory(@Qualifier("masterDataSource") DataSource masterDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setEnvironment("masterSqlSessionFactory");
        sqlSessionFactoryBean.setDataSource(masterDataSource);// 指定数据源(这个必须有，否则报错)

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        Resource[] mapperLocations = resolver.getResources(mybatisScanXml);
        sqlSessionFactoryBean.setMapperLocations(mapperLocations);
        /**插件*/
        sqlSessionFactoryBean.setPlugins(new Interceptor[]{masterPagePlugin()});

        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBean.getObject();
        return sqlSessionFactory;
    }

    @Bean(name="masterTransactionManager")
    @Primary
    public DataSourceTransactionManager masterTransactionManager(@Qualifier("masterDataSource") DataSource masterDataSource) {
        return new DataSourceTransactionManager(masterDataSource);
    }

    @Bean(name="masterTransactionInterceptor")
    public TransactionInterceptor masterTransactionInterceptor(@Qualifier("masterTransactionManager") DataSourceTransactionManager masterTransactionManager,
                                                               NameMatchTransactionAttributeSource nameMatchTransactionAttributeSource) {
        TransactionInterceptor transactionInterceptor = new TransactionInterceptor();
        transactionInterceptor.setTransactionManager(masterTransactionManager);
        transactionInterceptor.setTransactionAttributeSource(nameMatchTransactionAttributeSource);
        return transactionInterceptor;
    }

    /**
     * 根据数据源创建对应的分页插件
     */
    private PageHelper masterPagePlugin(){

        PageHelper pageHelper = new PageHelper();

        Properties properties = new Properties();
        properties.setProperty("dialect",dbType);
        properties.setProperty("reasonable", "true");

        pageHelper.setProperties(properties);

        return pageHelper;
    }


}
