package com.holland.common.utils;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SortKit {
    @SafeVarargs
    public static <T, U extends Comparable<? super U>> List<T> sort(List<T> list, boolean natural, boolean nullsFirst, Function<T, U>... suppliers) {
        if (suppliers.length == 0) return list;

        Comparator<U> order = natural ? Comparator.naturalOrder() : Comparator.reverseOrder();
        Comparator<U> base = nullsFirst ? Comparator.nullsFirst(order) : Comparator.nullsLast(order);
        Comparator<T> comparing = Comparator.comparing(suppliers[0], base);
        for (int i = 1, suppliersLength = suppliers.length; i < suppliersLength; i++) {
            Function<T, U> supplier = suppliers[i];
            comparing = comparing.thenComparing(supplier, base);
        }
        return list.stream().sorted(comparing).collect(Collectors.toList());
    }
}
