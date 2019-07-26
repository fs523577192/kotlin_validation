package org.firas.rdb_model.type

import org.firas.math.BigDecimal
import kotlin.reflect.KClass

/**
 *
 * @author Wu Yuping
 */
class DecimalType(val precision: Int, val scale: Int): DbType() {

    init {
        if (this.precision <= 0) {
            throw IllegalArgumentException("The precision must be a positive integer: $precision")
        }
        if (this.scale < 0) {
            throw IllegalArgumentException("The scale must be a non-negative integer: $scale")
        }
    }

    override fun toKotlinType(): KClass<*> {
        return BigDecimal::class
    }
}