package com.holland.gateway.conf;

import com.holland.common.spring.configuration.GlobalExceptionHandle;
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
}
