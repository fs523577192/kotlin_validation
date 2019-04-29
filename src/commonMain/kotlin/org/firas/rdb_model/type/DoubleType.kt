package org.firas.rdb_model.type

import kotlin.reflect.KClass

/**
 * 64-bit floating point number
 *
 * @author Wu Yuping
 */
class DoubleType(): DbType() {

    override fun toKotlinType(): KClass<*> {
        return Double::class
    }
}
