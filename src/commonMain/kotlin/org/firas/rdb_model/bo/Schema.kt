package org.firas.rdb_model.bo

import kotlin.collections.*

/**
 * 
 * @author Wu Yuping
 */
class Schema(val name: String, var database: Database? = null,
                  var tableMap: Map<String, Table> = HashMap()) {

    override fun equals(other: Any?): Boolean {
        if (other !is Schema) {
            return false
        }
        return this.name == other.name && this.database == other.database
    }

    override fun hashCode(): Int {
        return this.name.hashCode() + this.database.hashCode() * 97
    }

    override fun toString(): String {
        val db = this.database?.toString()
        return "Schema{database=$db, name=${this.name}}"
    }
}