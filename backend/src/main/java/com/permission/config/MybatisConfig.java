package com.permission.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Set;

/**
 * MyBatis配置类
 * 
 * @author system
 * @since 2024-01-01
 */
@Configuration
@MapperScan("com.permission.mapper")
public class MybatisConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        
        // 设置MyBatis配置
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCacheEnabled(true);
        configuration.setLazyLoadingEnabled(true);
        
        Set<String> lazyLoadTriggerMethods = new HashSet<>();
        lazyLoadTriggerMethods.add("equals");
        lazyLoadTriggerMethods.add("clone");
        lazyLoadTriggerMethods.add("hashCode");
        lazyLoadTriggerMethods.add("toString");
        configuration.setLazyLoadTriggerMethods(lazyLoadTriggerMethods);
        
        sessionFactory.setConfiguration(configuration);
        
        // 设置映射文件路径
        sessionFactory.setMapperLocations(
            new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml")
        );
        
        return sessionFactory.getObject();
    }
}
