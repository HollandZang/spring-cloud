package com.holland.util

enum class CaseFormat(private val wordRange: ClosedRange<Char>, private val wordSeparator: String) {
    LOWER_HYPHEN('-'..'-', "-") {
        override fun format(wordIndex: Int, word: String) = word.toLowerCase()
    },
    LOWER_UNDERSCORE('_'..'_', "_") {
        override fun format(wordIndex: Int, word: String) = word.toLowerCase()
    },
    LOWER_CAMEL('A'..'Z', "") {
        override fun format(wordIndex: Int, word: String): String {
            return word.toCharArray().mapIndexed { index, c ->
                if (index == 0) {
                    if (wordIndex == 0) toLower(c)
                    else toUpper(c)
                } else toLower(c)
            }.joinToString("")
        }
    },
    UPPER_CAMEL('A'..'Z', "") {
        override fun format(wordIndex: Int, word: String): String {
            return word.toCharArray()
                .mapIndexed { index, c ->
                    if (index == 0) toUpper(c)
                    else toLower(c)
                }.joinToString("")
        }
    },
    UPPER_UNDERSCORE('_'..'_', "_") {
        override fun format(wordIndex: Int, word: String) = word.toUpperCase()
    }, ;

    abstract fun format(wordIndex: Int, word: String): String

    fun to(to: CaseFormat, s: String): String {
        val words = mutableListOf<String>()
        var start = 0
        s.forEachIndexed { index, c ->
            if (wordRange.contains(c)) {
                words.add(s.slice(start until index))
                start = when (this) {
                    LOWER_CAMEL, UPPER_CAMEL -> index
                    else -> index + 1
                }
            }
        }
        words.add(s.substring(start))

        return words.mapIndexed { index, it -> to.format(index, it) }
            .joinToString(to.wordSeparator)
    }

    protected fun toUpper(c: Char): Char {
        return if (('a'..'z').contains(c)) {
            c - 32
        } else c
    }

    protected fun toLower(c: Char): Char {
        return if (('A'..'Z').contains(c)) {
            c + 32
        } else c
    }
}