package org.firas.rdb_model.type

import org.firas.math.BigDecimal
import kotlin.reflect.KClass

/**
 *
 * @author Wu Yuping
 */
class DecimalType(): DbType() {

    override fun toKotlinType(): KClass<*> {
        return BigDecimal::class
    }
}