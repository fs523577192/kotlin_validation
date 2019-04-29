package org.firas.rdb_model.bo

import org.firas.rdb_model.dialect.DbDialect

/**
 * 
 * @author Wu Yuping
 */
class Database(val dbDialect: DbDialect, val name: String,
                    val attributes: Map<String, Any> = HashMap(),
                    var schemaMap: Map<String, Schema> = HashMap(),
                    var host: String? = null, var port: Int? = null) {

    override fun equals(other: Any?): Boolean {
        if (other !is Database) {
            return false
        }
        return this.dbDialect == other.dbDialect && this.name == other.name &&
                this.host == other.host && this.port == other.port
    }

    override fun hashCode(): Int {
        return this.dbDialect.hashCode() + this.name.hashCode() * 97 +
                this.host.hashCode() * 89 +
                this.port.hashCode() * 83
    }

    override fun toString(): String {
        return "Database{dbDialect=" + this.dbDialect.toString() +
                ", name=${this.name}, host=${this.host}, port=${this.port}, attributes=" +
                this.attributes.toString() + "}"
    }
}
