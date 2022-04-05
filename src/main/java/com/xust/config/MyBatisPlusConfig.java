package com.xust.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Description: TODO
 * @Author YunShuaiWei
 * @Date 2021/4/19 15:51
 * @Version
 **/
@MapperScan("com.xust.dao")
@EnableTransactionManagement
@Configuration
public class MyBatisPlusConfig {
    /**
     * 分页插件
     **/
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }
}
