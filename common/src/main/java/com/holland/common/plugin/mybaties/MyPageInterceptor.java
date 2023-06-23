package com.holland.common.plugin.mybaties;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * 用于接收瀑布流式请求的分页拦截器，例：请求10条，返回11条。通过size是否超过10来判断有没有下一页
 */
@Component
@Intercepts(
        @Signature(type = Executor.class, method = "query"
                , args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
)
public class MyPageInterceptor implements Interceptor {
    public final static ThreadLocal<MyPageHelper> helper = new ThreadLocal<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (helper.get() != null) {
            Object[] args = invocation.getArgs();
            MappedStatement ms = (MappedStatement) args[0];
            Object parameterObject = args[1];

            BoundSql boundSql = ms.getBoundSql(parameterObject);
            String sql = boundSql.getSql();

            MyPageHelper myPageHelper = helper.get();
            long pageNum = myPageHelper.getPageNum();
            long pageSize = myPageHelper.getPageSize();
            sql = "select t.* from (" + sql + ") t limit " + ((pageNum - 1) * pageSize) + "," + (pageSize + 1);

            BoundSql bs = new BoundSql(ms.getConfiguration(), sql, boundSql.getParameterMappings(), parameterObject);
            MappedStatement newMs = copyMappedStatement(ms, new BoundSqlSqlSource(bs));
            args[0] = newMs;
            args[5] = bs;
            helper.remove();
        }

        return invocation.proceed();
    }

    private MappedStatement copyMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());

        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length > 0) {
            builder.keyProperty(String.join(",", ms.getKeyProperties()));
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }

    public static class BoundSqlSqlSource implements SqlSource {
        private final BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
