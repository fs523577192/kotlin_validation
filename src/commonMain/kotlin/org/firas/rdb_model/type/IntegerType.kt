package org.firas.rdb_model.type

import kotlin.reflect.KClass

/**
 * 32-bit integer
 *
 * @author Wu Yuping
 */
class IntegerType(val unsigned: Boolean = false): DbType() {

    override fun toKotlinType(): KClass<*> {
        return Int::class
    }
}