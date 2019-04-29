package org.firas.rdb_model.type

import kotlin.reflect.KClass

/**
 * 二进制类型
 *
 * @author Wu Yuping
 */
class ClobType(): DbType() {

    override fun toKotlinType(): KClass<*> {
        return Int::class
    }
}
