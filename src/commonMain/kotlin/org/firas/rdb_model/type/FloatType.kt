package org.firas.rdb_model.type

import kotlin.reflect.KClass

/**
 * 32-bit floating point number
 *
 * @author Wu Yuping
 */
class FloatType(): DbType() {

    override fun toKotlinType(): KClass<*> {
        return Float::class
    }
}
