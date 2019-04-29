package org.firas.rdb_model.type

import kotlin.reflect.KClass

/**
 * binary data
 *
 * @author Wu Yuping
 */
class BlobType(): DbType() {

    override fun toKotlinType(): KClass<*> {
        return Int::class
    }
}
