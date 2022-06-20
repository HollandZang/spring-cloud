package com.holland.frame

enum class IncrementStrategy {
    AUTO {
        override fun getVal(): String {
            TODO("Not yet implemented")
        }
    },
    UUID {
        override fun getVal(): String {
            return java.util.UUID.randomUUID().toString()
        }
    },
    REDIS {
        override fun getVal(): String {
            TODO("Not yet implemented: incr")
        }
    },
    SNOWFLAKE {
        override fun getVal(): String {
            return ""
        }
    },
    TinyID {
        override fun getVal(): String {
            TODO("Not yet implemented")
        }
    },
    Uidgenerator {
        override fun getVal(): String {
            TODO("Not yet implemented")
        }
    },
    Leaf {
        override fun getVal(): String {
            TODO("Not yet implemented")
        }
    },
    ;

    abstract fun getVal(): String
}