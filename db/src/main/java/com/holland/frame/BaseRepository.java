package com.holland.frame;

import com.holland.DataSource;
import com.holland.JDBCConnectionPool;
import com.holland.frame.action.Select;
import com.holland.frame.action.Where;
import com.holland.frame.annotation.Table;
import com.holland.util.DbUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseRepository<T extends BaseRepository<T>> {
    private final Class<T> aClass;
    private final Table table;
    private final List<Field> fields;
    private final JDBCConnectionPool pool;
    private final String tableName;
    private final String tableFullName;
    private final DataSource dataSource;

    protected BaseRepository() {
        //noinspection unchecked
        this.aClass = (Class<T>) this.getClass();
        this.tableName = this.aClass.getSimpleName();
        final CacheClassReflect cache = Cache.INSTANCE.getClassReflect().get(this.aClass);
        if (cache != null) {
            this.table = cache.getTable();
            this.fields = cache.getFields();
        } else {
            this.table = this.aClass.getAnnotation(Table.class);
            this.fields = Arrays.stream(this.aClass.getDeclaredFields())
                    .map(Field::new)
                    .collect(Collectors.toList());
            Cache.INSTANCE.getClassReflect().put(this.aClass, new CacheClassReflect(this.table, this.fields));
        }

        this.pool = DbConf.INSTANCE.getM().get(table.datasource());
        this.dataSource = this.pool.getDataSource();
        this.tableFullName = dataSource.tableFullName(table, tableName);
    }

    protected List<T> find() {
        final int size = fields.size();
        final List<String> conditions = new ArrayList<>(size);
        final List<Object> v = new ArrayList<>(size);

        fields.forEach(field -> {
            field.getField().setAccessible(true);
            try {
                final Object value = field.getField().get(this);
                if (value != null) {
                    conditions.add(quoteName(field.getName()) + "=?");
                    v.add(value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        final String sql = "select * from " + tableFullName + " where " + String.join(" and ", conditions);
        return query(sql, v.toArray());
    }

    protected List<T> findByWhere(String where, Object... params) {
        final String sql = "select * from " + tableFullName + " where " + where;
        return query(sql, params);
    }

    protected void save() {
        final int size = fields.size();
        final List<String> k = new ArrayList<>(size);
        final List<Object> v = new ArrayList<>(size);

        fields.forEach(field -> {
            if (field.getPrimaryKey() != null) {
                if (field.getPrimaryKey().autoIncrement() == IncrementStrategy.AUTO) {
                    return;
                } else {
                    k.add(quoteName(field.getName()));
                    v.add(field.getPrimaryKey().autoIncrement().getVal());
                }
            }

            field.getField().setAccessible(true);
            try {
                final Object value = field.getField().get(this);
                if (value != null) {
                    k.add(quoteName(field.getName()));
                    v.add(value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        final StringBuilder sql = new StringBuilder("insert into ").append(tableFullName)
                .append(" (").append(String.join(",", k)).append(") ")
                .append(v.stream().map(o -> "?").collect(Collectors.joining(",", "values (", ")")));

        update(sql.toString(), v.toArray());
    }

    protected void update() {
        final int size = fields.size();
        final List<String> update = new ArrayList<>(size);
        final List<Object> updateV = new ArrayList<>(size);

        final List<String> where = new ArrayList<>(size);
        final List<Object> whereV = new ArrayList<>(size);
        fields.forEach(field -> {
            field.getField().setAccessible(true);
            try {
                final Object value = field.getField().get(this);
                if (field.getPrimaryKey() != null) {
                    where.add(quoteName(field.getName()) + "=?");
                    whereV.add(value);
                } else {
                    update.add(quoteName(field.getName()) + "=?");
                    updateV.add(value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        final StringBuilder sql = new StringBuilder("update ").append(tableFullName)
                .append("set ").append(String.join(",", update))
                .append(" where ").append(String.join(",", where));

        final Object[] params = new Object[update.size() + where.size()];
        for (int i = 0; i < updateV.size(); i++)
            params[i] = updateV.get(i);
        for (int i = 0; i < whereV.size(); i++)
            params[i + updateV.size()] = whereV.get(i);
        update(sql.toString(), params);
    }

    private String quoteName(String name) {
        return dataSource.quoteName(name);
    }

    public void update(String sql, Object... params) {
        //noinspection DuplicatedCode
        pool.use(connection -> {
            PreparedStatement s = null;
            try {
                s = connection.prepareStatement(sql);
                for (int i = 0; i < params.length; i++)
                    s.setObject(i + 1, params[i]);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return s;
        });
    }

    public List<T> query(String sql, Object... params) {
        //noinspection DuplicatedCode
        return pool.use(connection -> {
            PreparedStatement s = null;
            try {
                s = connection.prepareStatement(sql);
                for (int i = 0; i < params.length; i++)
                    s.setObject(i + 1, params[i]);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return s;
        }, resultSet -> DbUtil.INSTANCE.getResult(resultSet, aClass));
    }

    protected Select<T> select() {
        return select(null);
    }

    protected Select<T> select(String values) {
        return new Select<>(this, this.pool, this.aClass, this.fields, this.dataSource, this.tableName, values, table);
    }

    protected Where<T> where() {
        return select().where();
    }

    protected String toJson() {
        throw new RuntimeException("Not yet implemented");
    }

    protected T fromJson(String jsonStr) {
        throw new RuntimeException("Not yet implemented");
    }
}
