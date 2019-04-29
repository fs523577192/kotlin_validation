package org.firas.rdb_model.type

import kotlin.reflect.KClass

/**
 * 16-bit integer
 *
 * @author Wu Yuping
 */
class SmallIntType(val unsigned: Boolean = false): DbType() {

    override fun toKotlinType(): KClass<*> {
        return Short::class
    }
}