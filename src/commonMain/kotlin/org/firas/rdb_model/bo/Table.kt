package org.firas.rdb_model.bo

import kotlin.collections.*

/**
 * 
 * @author Wu Yuping
 */
data class Table(val name: String, val comment: String = "", var schema: Schema? = null,
                 val attributes: Map<String, Any> = HashMap(),
                 var columnMap: LinkedHashMap<String, Column> = LinkedHashMap(),
                 var indexMap: Map<String, Index> = HashMap()) {

    override fun equals(other: Any?): Boolean {
        if (other !is Table) {
            return false
        }
        return this.name == other.name && this.schema == other.schema
    }

    override fun hashCode(): Int {
        return this.name.hashCode() + 97 * this.schema.hashCode()
    }

    override fun toString(): String {
        return "Table{name=${this.name}, comment=${this.comment}, schema=" +
                this.schema?.toString() + ", attributes=" +
                this.attributes.toString() + "}"
    }
}