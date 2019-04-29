package org.firas.rdb_model.type

import org.firas.datetime.LocalDateTime
import kotlin.reflect.KClass

/**
 *
 * @author Wu Yuping
 */
class DateTimeType(): DbType() {

    override fun toKotlinType(): KClass<*> {
        return LocalDateTime::class
    }
}
