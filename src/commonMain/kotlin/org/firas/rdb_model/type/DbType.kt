package org.firas.rdb_model.type

/**
 * Data types
 *
 * @author Wu Yuping
 */
abstract class DbType {

    abstract fun toKotlinType(): kotlin.reflect.KClass<*>
}
