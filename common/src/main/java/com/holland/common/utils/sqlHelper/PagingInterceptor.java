//package com.holland.common.utils.sqlHelper;
//
//import org.apache.ibatis.executor.statement.StatementHandler;
//import org.apache.ibatis.plugin.Interceptor;
//import org.apache.ibatis.plugin.Intercepts;
//import org.apache.ibatis.plugin.Invocation;
//import org.apache.ibatis.plugin.Signature;
//import org.apache.ibatis.session.ResultHandler;
//import org.springframework.stereotype.Component;
//
//import java.sql.Statement;
//import java.util.Properties;
//
//@Intercepts({
//        @Signature(method = "query", type = StatementHandler.class, args = {Statement.class, ResultHandler.class})
//})
//@Component
//public class PagingInterceptor implements Interceptor {
//    @Override
//    public Object intercept(Invocation invocation) throws Throwable {
//        return null;
//    }
//
//    @Override
//    public Object plugin(Object target) {
//        return Interceptor.super.plugin(target);
//    }
//
//    @Override
//    public void setProperties(Properties properties) {
//        Interceptor.super.setProperties(properties);
//    }
//}
