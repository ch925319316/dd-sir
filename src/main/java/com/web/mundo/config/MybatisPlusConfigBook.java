package com.web.mundo.config;


import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.web.mundo.dao", sqlSessionFactoryRef = "bookSqlSessionFactory")
public class MybatisPlusConfigBook {


    @Value("${spring.datasource.druid.book.url}")
    private String url;

    @Value("${spring.datasource.druid.book.username}")
    private String user;

    @Value("${spring.datasource.druid.book.password}")
    private String password;

    @Value("${spring.datasource.druid.book.driver-class-name}")
    private String driverClass;

    @Bean(name = "bookDataSource")
    public DataSource clusterDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(name = "bookTransactionManager")
    public DataSourceTransactionManager clusterTransactionManager() {
        return new DataSourceTransactionManager(clusterDataSource());
    }

    @Bean(name = "bookSqlSessionFactory")
    public SqlSessionFactory clusterSqlSessionFactory(@Qualifier("bookDataSource") DataSource clusterDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(clusterDataSource);
        return sessionFactory.getObject();
    }


}
