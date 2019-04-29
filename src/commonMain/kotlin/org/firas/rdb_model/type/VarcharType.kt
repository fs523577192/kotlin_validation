package org.firas.rdb_model.type

import kotlin.reflect.KClass

/**
 * Variable-length (character) string
 *
 * @author Wu Yuping
 */
class VarcharType(val length: Int, val charset: String): DbType() {

    init {
        if (length <= 0) {
            throw IllegalArgumentException("The length property of Varchar " +
                    "is not a positive integer: $length")
        }
    }

    override fun toKotlinType(): KClass<*> {
        return String::class
    }
}