package org.firas.rdb_model.type

import kotlin.reflect.KClass

/**
 * 64-bit integer
 *
 * @author Wu Yuping
 */
class BigIntType(val unsigned: Boolean = false): DbType() {

    override fun toKotlinType(): KClass<*> {
        return Long::class
    }
}