package org.k8scmp.common;

import com.alibaba.druid.pool.DruidDataSource;
import org.k8scmp.common.GlobalConstant;
import org.k8scmp.util.DatabaseType;
import org.k8scmp.util.StringUtils;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Created by feiliu206363 on 2017/3/27.
 */
@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = {"org.k8scmp.mapper"}, sqlSessionFactoryRef = "k8scmpSqlSessionFactory")
public class k8scmpConfiguration {

    @Bean(initMethod = "init", destroyMethod = "close", name = "k8scmpDataSource")
    @Primary
    public DataSource k8scmpDataSource() throws Exception {
        String mysqlHost = System.getenv(GlobalConstant.k8scmp_MYSQL_HOST);
        if (StringUtils.isBlank(mysqlHost)) {
            throw new Exception("env MYSQL_HOST do not set!");
        }
        String mysqlPort = System.getenv(GlobalConstant.k8scmp_MYSQL_PORT);
        if (StringUtils.isBlank(mysqlPort)) {
            mysqlPort = "3306";
        }
        String mysqlUsername = System.getenv(GlobalConstant.k8scmp_MYSQL_USERNAME);
        if (StringUtils.isBlank(mysqlUsername)) {
            mysqlUsername = "k8scmp";
        }
        String mysqlPassword = System.getenv(GlobalConstant.k8scmp_MYSQL_PASSWORD);
        if (StringUtils.isBlank(mysqlPassword)) {
            mysqlPassword = "k8scmp";
        }
        String mysqlDB = System.getenv(GlobalConstant.k8scmp_MYSQL_DB);
        if (StringUtils.isBlank(mysqlDB)) {
            mysqlDB = "k8scmp";
        }

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://" + mysqlHost + ":" + mysqlPort + "/" + mysqlDB
                + "?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true");
        dataSource.setPassword(mysqlPassword);
        dataSource.setUsername(mysqlUsername);
        dataSource.setMaxActive(80);
        dataSource.setMaxWait(50000);
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setMinEvictableIdleTimeMillis(20000);
        dataSource.setTimeBetweenEvictionRunsMillis(20000);
        dataSource.setInitialSize(5);
        dataSource.setMinIdle(1);
        dataSource.setFilters("stat,wall,log4j");
        return dataSource;
    }

    @Bean(name = "k8scmpTransactionManager")
    @Primary
    public DataSourceTransactionManager k8scmpTransactionManager() throws Exception {
        return new DataSourceTransactionManager(k8scmpDataSource());
    }

    @Bean(name = "k8scmpSqlSessionFactory")
    @Primary
    public SqlSessionFactoryBean sqlSessionFactory(@Qualifier("k8scmpDataSource") DataSource dataSource) throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        GlobalConstant.DATABASETYPE = DatabaseType.MYSQL;
        return sessionFactory;
    }

}
