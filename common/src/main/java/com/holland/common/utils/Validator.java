package com.holland.common.utils;

import java.util.Formatter;

public interface Validator {
    Validator notEmpty();

    Validator lenLT(int maxLen);

    Validator lenGE(int minLen);


    class ParameterException extends RuntimeException {
        private ParameterException(String message) {
            super(message);
        }

        public static void accept(String str, Object... args) {
            throw new ParameterException(new Formatter().format(str, args).toString());
        }
    }
}
