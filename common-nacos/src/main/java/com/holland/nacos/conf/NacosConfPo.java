package com.holland.nacos.conf;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

public class NacosConfPo {
    public final String namespace;

    public final String group;

    public final String dataId;

    public final Field field;

    public NacosConfPo(String namespace, String group, String dataId, Field field) {
        this.namespace = namespace;
        this.group = group;
        this.dataId = dataId;
        this.field = field;
    }

    public static Set<NacosConfPo> genConfigs(String namespace, String group) {
        Set<NacosConfPo> set = new HashSet<>();
        for (Field field : NacosPropKit.INSTANCE.getDeclaredFields()) {
            if (!Modifier.isPublic(field.getModifiers())) continue;
            String n, d, g;

            NacosConf nacosConf = field.getAnnotation(NacosConf.class);
            //noinspection ConstantConditions
            n = findVal(nacosConf != null, nacosConf.namespace(), namespace, "public");
            //noinspection ConstantConditions
            g = findVal(nacosConf != null, nacosConf.group(), group, "DEFAULT_GROUP");
            //noinspection ConstantConditions
            d = findVal(nacosConf != null, nacosConf.dataId(), field.getName());

            if (notEmpty(n) && notEmpty(g) && notEmpty(d))
                set.add(new NacosConfPo(n, g, d, field));
            else
                throw new RuntimeException("nacos conf is missing something: " + new NacosConfPo(n, g, d, field));
        }
        return set;
    }

    private static String findVal(boolean b, String... args) {
        for (int i = b ? 0 : 1; i < args.length; i++) {
            String arg = args[i];
            if (notEmpty(arg)) return arg;
        }
        return null;
    }

    private static boolean notEmpty(String arg) {
        return arg != null && arg.length() != 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NacosConfPo that = (NacosConfPo) o;

        if (!Objects.equals(namespace, that.namespace)) return false;
        if (!Objects.equals(group, that.group)) return false;
        return Objects.equals(dataId, that.dataId);
    }

    @Override
    public int hashCode() {
        int result = namespace != null ? namespace.hashCode() : 0;
        result = 31 * result + (group != null ? group.hashCode() : 0);
        result = 31 * result + (dataId != null ? dataId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NacosConfPo.class.getSimpleName() + "[", "]")
                .add("namespace='" + namespace + "'")
                .add("group='" + group + "'")
                .add("dataId='" + dataId + "'")
                .toString();
    }
}
