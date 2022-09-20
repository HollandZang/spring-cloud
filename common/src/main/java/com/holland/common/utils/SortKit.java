package com.holland.common.utils;

import reactor.util.function.Tuple3;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SortKit {
    /**
     * @param list       带排序list
     * @param natural    是否升序
     * @param nullsFirst 是否把空值属性放最前面
     * @param suppliers  提供的多个要排序的属性
     * @param <U>        提供的要排序的属性，必须继承自接口 Comparable；
     *                   可以是数字(byte､short､int､long､float､double等,支持正数､负数､0)､char､String､java.util.Date
     * @return 排序后的list
     */
    @SafeVarargs
    public static <T, U extends Comparable<? super U>> List<T> sort(List<T> list, boolean natural, boolean nullsFirst, Function<T, U>... suppliers) {
        if (suppliers.length == 0) return list;

        Comparator<U> order = natural ? Comparator.naturalOrder() : Comparator.reverseOrder();
        Comparator<U> base = nullsFirst ? Comparator.nullsFirst(order) : Comparator.nullsLast(order);
        Comparator<T> comparing = Comparator.comparing(suppliers[0], base);

        for (int i = 1, suppliersLength = suppliers.length; i < suppliersLength; i++) {
            comparing = comparing.thenComparing(suppliers[i], base);
        }
        return list.stream().sorted(comparing).collect(Collectors.toList());
    }

    /**
     * @param list      带排序list
     * @param suppliers [提供的要排序的属性, 是否升序, 是否把空值属性放最前面]
     * @param <U>       提供的要排序的属性，必须继承自接口 Comparable；
     *                  可以是数字(byte､short､int､long､float､double等,支持正数､负数､0)､char､String､java.util.Date
     * @return 排序后的list
     */
    @SafeVarargs
    public static <T, U extends Comparable<? super U>> List<T> sort(List<T> list, Tuple3<Function<T, U>, Boolean, Boolean>... suppliers) {
        if (suppliers.length == 0) return list;

        Tuple3<Function<T, U>, Boolean, Boolean> supplier = suppliers[0];
        Comparator<U> order = supplier.getT2() ? Comparator.naturalOrder() : Comparator.reverseOrder();
        Comparator<U> base = supplier.getT3() ? Comparator.nullsFirst(order) : Comparator.nullsLast(order);
        Comparator<T> comparing = Comparator.comparing(supplier.getT1(), base);

        for (int i = 1, suppliersLength = suppliers.length; i < suppliersLength; i++) {
            order = supplier.getT2() ? Comparator.naturalOrder() : Comparator.reverseOrder();
            base = supplier.getT3() ? Comparator.nullsFirst(order) : Comparator.nullsLast(order);
            comparing = comparing.thenComparing(supplier.getT1(), base);
        }
        return list.stream().sorted(comparing).collect(Collectors.toList());
    }
}
