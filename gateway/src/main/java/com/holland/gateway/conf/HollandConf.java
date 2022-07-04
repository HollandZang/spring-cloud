package com.holland.gateway.conf;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.holland.common.spring.configuration.GlobalExceptionHandle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(GlobalExceptionHandle.class)
@Configuration
public class HollandConf {
//    @PostConstruct
//    public void init() {
//
//        final JDBCConnectionPool pool = new JDBCConnectionPool(DataSource.MYSQL, "localhost", "3306", "root", "root", "holland");
//
//        DbConf.INSTANCE.getM().put("holland", pool);
//
//        Generator.Companion.doGenerate(TestClass.class);
//    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
