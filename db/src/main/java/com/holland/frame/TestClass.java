package com.holland.frame;

import com.holland.frame.annotation.Column;
import com.holland.frame.annotation.Index;
import com.holland.frame.annotation.PrimaryKey;
import com.holland.frame.annotation.Table;

@Table(comment = "test class1", datasource = "holland", db = "holland")
public class TestClass extends BaseRepository<TestClass> {
    @PrimaryKey(autoIncrement = IncrementStrategy.AUTO)
    @Column(dbType = "int")
    public Integer id;

    @Index(indexName = "u_f1")
    @Index(indexName = "u_f1_f2")
    @Column(dbType = "varchar", comment = "f1", length = 255L, defaultVal = "'a1sd'")
    public String field1;

    @Index(indexName = "u_f1_f2")
    @Column(dbType = "float", comment = "f23", notNull = false, defaultVal = "123.12", length = 5L, scale = 2)
    public Double field2;

    @Override
    public String toString() {
        return "TestClass{" +
                "id=" + id +
                ", field1='" + field1 + '\'' +
                ", field2=" + field2 +
                '}';
    }
}
