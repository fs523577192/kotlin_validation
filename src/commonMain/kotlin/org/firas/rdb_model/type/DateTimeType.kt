package org.firas.rdb_model.type

import org.firas.datetime.LocalDateTime
import kotlin.reflect.KClass

/**
 *
 * @author Wu Yuping
 */
class DateTimeType(val fractionalSeconds: Int): DbType() {

    init {
        if (this.fractionalSeconds < 0) {
            throw IllegalArgumentException("The fractionalSeconds must be a non-negative integer: $fractionalSeconds")
        }
    }

    override fun toKotlinType(): KClass<*> {
        return LocalDateTime::class
    }
}
