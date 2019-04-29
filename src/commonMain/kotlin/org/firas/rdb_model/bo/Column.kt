package org.firas.rdb_model.bo

import org.firas.rdb_model.type.DbType

/**
 * 
 * @author Wu Yuping
 */
class Column(val dbType: DbType, val name: String,
                  val nullable: Boolean = true,
                  val defaultValue: String = "NULL",
                  val onUpdateValue: String? = null,
                  val comment: String = "", var table: Table? = null) {

    override fun equals(other: Any?): Boolean {
        if (other !is Column) {
            return false
        }
        return dbType == other.dbType && name == other.name &&
                table == other.table
    }

    override fun hashCode(): Int {
        return dbType.hashCode() + name.hashCode() * 97 +
                (if (null == table) 0 else table.hashCode()) * 89
    }
}