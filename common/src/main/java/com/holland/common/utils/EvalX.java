package com.holland.common.utils;

import com.alibaba.fastjson.JSON;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EvalX {
    public static void main(String[] args) {
        final JSON json = (JSON) JSON.parse("{a:1,data:[{uid:\"157580\",activeCid:\"69\",activeChannel:\"kuaikan\",activeGid:\"1000\",activeIP:\"2882823743\",channelUid:\"91627840_3\",status:\"0\"}]}");
        final JsonX jsonX = new JsonX(json);

        final String expressions = "1==1 && s'data[0]uid=='157580' && 1==2";

        final Object exec = new EvalX().exec(expressions, jsonX);
        System.out.println(exec);
    }

    public <T> T exec(String expressions) {
        return exec(expressions, null);
    }

    public <T> T exec(String expressions, JsonX jsonX) {
        if (expressions == null || expressions.isEmpty()) return null;

        expressions = expressions.replaceAll(" +", "");
        final List<Triple> triples = getTriples(expressions, jsonX);
        if (triples.isEmpty()) {
            return (T) setIt(expressions.toCharArray(), jsonX);
        }
//        System.out.println(triples);
        Object val = getVal(triples);
//        System.out.println(val);
        return (T) val;
    }

    private List<Triple> getTriples(String expressions, JsonX jsonX) {
        final String reg = Arrays.stream(Keywords.values())
                .map(keywords -> keywords.reg)
                .collect(Collectors.joining("|"));
//        System.out.println(reg);
        final Pattern pattern = Pattern.compile(reg);
        final Matcher matcher = pattern.matcher(expressions);
        int i = 0;
        final char[] chars = expressions.toCharArray();
        final List<Triple> triples = new ArrayList<>();
        while (matcher.find()) {
            final int s = matcher.start();
            final int e = matcher.end();
            char[] left = new char[s - i];
            System.arraycopy(chars, i, left, 0, left.length);
            char[] middle = new char[e - s];
            System.arraycopy(chars, s, middle, 0, middle.length);

            if (triples.isEmpty()) triples.add(new Triple());
            final Triple triple = triples.get(triples.size() - 1);
            if (triple.action == null && triple.a == null) {
                triple.action = Keywords.find(middle);
                triple.setA(left, jsonX);
            } else {
                triple.setB(left, jsonX);
                final Triple next = new Triple();
                next.action = Keywords.find(middle);
                triples.add(next);
            }

            i = e;
        }
        if (i != 0) {
            char[] end = new char[chars.length - i];
            System.arraycopy(chars, i, end, 0, end.length);
            final Triple triple = triples.get(triples.size() - 1);
            triple.setB(end, jsonX);
        }
        return triples;
    }

    private Object getVal(List<Triple> triples) {
        Object a = null;
        final Stack<Object> stack = new Stack<>();
        for (Triple t : triples) {
            if (t.action.equals(Keywords.and) || t.action.equals(Keywords.or)) {
                stack.push(a);
                stack.push(t.action);
                a = t.b;
                continue;
            }

            final BiFunction<Object, Object, Object> action = t.action.getAction();
            a = action.apply(t.a == null ? a : t.a, t.b);
        }
        while (!stack.empty()) {
            final Object pre = stack.pop();
            if (pre instanceof Keywords) {
//                if (pre.equals(Keywords.not)) {
//                    a = ((Keywords) pre).getAction().apply(null, a);
//                } else {
                final Object preA = stack.pop();
                a = ((Keywords) pre).getAction().apply(preA, a);
//                }
            } else {
                throw new EvalXException("OUT OF EXPECTATION!");
            }
        }
        return a;
    }

    static Object setIt(char[] it, JsonX jsonX) {
        if (it[0] == '\'' && it[it.length - 1] == '\'') {
            char[] chars = new char[it.length - 2];
            System.arraycopy(it, 1, chars, 0, chars.length);
            return new String(chars);
        } else {
            for (char c : it) {
                if (!(Character.isDigit(c) || c == '.')) {
                    return jsonX.find(new String(it));
                }
            }
            return Double.valueOf(new String(it));
        }
    }

    private static class Triple {
        Keywords action;
        Object a;
        Object b;

        public void setA(char[] a, JsonX jsonX) {
            this.a = setIt(a, jsonX);
        }

        public void setB(char[] b, JsonX jsonX) {
            this.b = setIt(b, jsonX);
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", "[", "]")
                    .add("action=" + action)
                    .add("a=" + a)
                    .add("b=" + b)
                    .toString();
        }
    }

    private enum Keywords {
        eq("==", "==") {
            @Override
            BiFunction<Object, Object, Object> getAction(Object... args) {
                return (a, b) -> {
                    if (a instanceof Number && b instanceof Number) {
                        return ((Number) a).doubleValue() == ((Number) b).doubleValue();
                    } else {
                        return Objects.equals(a, b);
                    }
                };
            }
        },
        gt(">", ">") {
            @Override
            BiFunction<Object, Object, Object> getAction(Object... args) {
                return (a, b) -> towNumberOp(a, b, () -> {
                    final int i = BigDecimal.valueOf(((Number) a).doubleValue())
                            .compareTo(BigDecimal.valueOf(((Number) b).doubleValue()));
                    return i > 0;
                });
            }
        },
        ge(">=", ">=") {
            @Override
            BiFunction<Object, Object, Object> getAction(Object... args) {
                return (a, b) -> towNumberOp(a, b, () -> {
                    final int i = BigDecimal.valueOf(((Number) a).doubleValue())
                            .compareTo(BigDecimal.valueOf(((Number) b).doubleValue()));
                    return i >= 0;
                });
            }
        },
        lt("<", "<") {
            @Override
            BiFunction<Object, Object, Object> getAction(Object... args) {
                return (a, b) -> towNumberOp(a, b, () -> {
                    final int i = BigDecimal.valueOf(((Number) a).doubleValue())
                            .compareTo(BigDecimal.valueOf(((Number) b).doubleValue()));
                    return i < 0;
                });
            }
        },
        le("<=", "<=") {
            @Override
            BiFunction<Object, Object, Object> getAction(Object... args) {
                return (a, b) -> towNumberOp(a, b, () -> {
                    final int i = BigDecimal.valueOf(((Number) a).doubleValue())
                            .compareTo(BigDecimal.valueOf(((Number) b).doubleValue()));
                    return i <= 0;
                });
            }
        },
        ne("!=", "!=") {
            @Override
            BiFunction<Object, Object, Object> getAction(Object... args) {
                return (a, b) -> !Objects.equals(a, b);
            }
        },
        add("+", "\\+") {
            @Override
            BiFunction<Object, Object, Object> getAction(Object... args) {
                return (a, b) -> {
                    if (a instanceof Number && b instanceof Number) {
                        return BigDecimal.valueOf(((Number) a).doubleValue())
                                .add(BigDecimal.valueOf(((Number) b).doubleValue()))
                                .doubleValue();
                    } else {
                        return a != null ? a.toString() + b
                                : "null" + b;
                    }
                };
            }
        },
        minus("-", "-") {
            @Override
            BiFunction<Object, Object, Object> getAction(Object... args) {
                return (a, b) -> towNumberOp(a, b, () ->
                        BigDecimal.valueOf(((Number) a).doubleValue())
                                .min(BigDecimal.valueOf(((Number) b).doubleValue()))
                                .doubleValue());
            }
        },
        multiply("*", "\\*") {
            @Override
            BiFunction<Object, Object, Object> getAction(Object... args) {
                return (a, b) -> towNumberOp(a, b, () ->
                        BigDecimal.valueOf(((Number) a).doubleValue())
                                .multiply(BigDecimal.valueOf(((Number) b).doubleValue()))
                                .doubleValue());
            }
        },
        divide("/", "/") {
            @Override
            BiFunction<Object, Object, Object> getAction(Object... args) {
                final int scale = args.length >= 1 ? Integer.parseInt((String) args[0]) : 2;
                final RoundingMode roundingMode = args.length >= 2 ? (RoundingMode) args[1] : RoundingMode.HALF_UP;
                return (a, b) -> towNumberOp(a, b, () ->
                        BigDecimal.valueOf(((Number) a).doubleValue())
                                .divide(BigDecimal.valueOf(((Number) b).doubleValue()), scale, roundingMode)
                                .doubleValue());
            }
        },
        remainder("%", "%") {
            @Override
            BiFunction<Object, Object, Object> getAction(Object... args) {
                return (a, b) -> towNumberOp(a, b, () ->
                        BigDecimal.valueOf(((Number) a).doubleValue())
                                .remainder(BigDecimal.valueOf(((Number) b).doubleValue()))
                                .doubleValue());
            }
        },
        //        not("!", "!") {
//            @Override
//            BiFunction<Object, Object, Object> getAction(Object... args) {
//                return (a, b) -> towBoolOp(true, b, () -> !(Boolean) b);
//            }
//        },
        and("&&", "&&") {
            @Override
            BiFunction<Object, Object, Object> getAction(Object... args) {
                return (a, b) -> towBoolOp(a, b, () -> (Boolean) a && (Boolean) b);
            }
        },
        or("||", "\\|\\|") {
            @Override
            BiFunction<Object, Object, Object> getAction(Object... args) {
                return (a, b) -> towBoolOp(a, b, () -> (Boolean) a || (Boolean) b);
            }
        },
        ;

        final String key;
        final String reg;

        BiFunction<Object, Object, Object> getAction(Object... args) {
            throw new EvalXException("not impl exception");
        }

        Object towNumberOp(Object a, Object b, Supplier<Object> fn) {
            if (a instanceof Number && b instanceof Number) {
                return fn.get();
            } else {
                throw new EvalXException("[" + this.key + "] must be two numbers");
            }
        }

        Object towBoolOp(Object a, Object b, Supplier<Object> fn) {
            if (a instanceof Boolean && b instanceof Boolean) {
                return fn.get();
            } else {
                throw new EvalXException("[" + this.key + "] must be two boolean");
            }
        }

        Keywords(String key, String reg) {
            this.key = key;
            this.reg = reg;
        }

        static Keywords find(char[] key) {
            final String k = new String(key);
            for (Keywords value : Keywords.values()) {
                if (value.key.equals(k)) {
                    return value;
                }
            }
            throw new EnumConstantNotPresentException(Keywords.class, k);
        }
    }

    public static class EvalXException extends RuntimeException {
        public EvalXException() {
        }

        public EvalXException(String message) {
            super(message);
        }
    }
}
