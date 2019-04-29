package org.firas.rdb_model.dialect

import org.firas.rdb_model.bo.Column
import org.firas.rdb_model.bo.Database
import org.firas.rdb_model.bo.Schema
import org.firas.rdb_model.domain.ColumnAddition
import org.firas.rdb_model.domain.ColumnComment
import org.firas.rdb_model.domain.ColumnDrop
import org.firas.rdb_model.domain.ColumnModification
import org.firas.rdb_model.domain.ColumnRename
import org.firas.rdb_model.domain.TableCreation
import org.firas.rdb_model.type.DbType
import kotlin.collections.*

/**
 * 
 * @author Wu Yuping
 */
abstract class DbDialect {

    /**
     * @return the SQL to check whether the DB connection is usable
     */
    abstract fun validateQuery(): String

    abstract fun getNameQuote(): String

    abstract fun getCharset(): DbCharset

    abstract fun toSQL(dbType: DbType): String

    open fun toSQL(column: Column): String {
        val dbType = toSQL(column.dbType)
        val nameQuote = getNameQuote()
        return "$nameQuote${column.name}$nameQuote $dbType " +
                (if (column.nullable) "" else "NOT ") +
                "NULL DEFAULT ${column.defaultValue} " +
                (if (null == column.onUpdateValue) "" else "ON UPDATE ${column.onUpdateValue}")
    }

    abstract fun toSQL(columnComment: ColumnComment): String

    open fun toSQL(columnAddition: ColumnAddition): String {
        val column = columnAddition.column
        val table = column.table
        val schema = table!!.schema
        val nameQuote = getNameQuote()
        return "ALTER TABLE $nameQuote${schema!!.name}$nameQuote." +
                "$nameQuote${table.name}$nameQuote ADD COLUMN " +
                toSQL(column)
    }

    abstract fun toSQL(columnRename: ColumnRename): String

    open fun toSQL(columnModification: ColumnModification): String {
        val column = columnModification.column
        val newColumn = Column(columnModification.dbType, column.name,
                columnModification.nullable, columnModification.defaultValue,
                columnModification.onUpdateValue)
        val table = column.table
        val schema = table!!.schema
        val nameQuote = getNameQuote()
        return "ALTER TABLE $nameQuote${schema!!.name}$nameQuote." +
                "$nameQuote${table.name}$nameQuote MODIFY COLUMN " +
                toSQL(newColumn)
    }

    open fun toSQL(columnDrop: ColumnDrop): String {
        val column = columnDrop.column
        val table = column.table
        val schema = table!!.schema
        val nameQuote = getNameQuote()
        return "ALTER TABLE $nameQuote${schema!!.name}$nameQuote." +
                "$nameQuote${table.name}$nameQuote DROP COLUMN " +
                "$nameQuote${column.name}$nameQuote"
    }

    open fun toSQL(tableCreation: TableCreation): String {
        val table = tableCreation.table
        val schema = table.schema
        val nameQuote = getNameQuote()
        val builder = StringBuilder("CREATE TABLE $nameQuote${schema!!.name}$nameQuote." +
                "$nameQuote${table.name}$nameQuote " +
                if (tableCreation.ifNotExists) "IF NOT EXISTS (" else "("
        )

        val temp = table.columnMap.values.joinToString(transform = { toSQL(it) })
        return builder.append(temp).append(")").toString()
    }

    abstract fun fetchInfo(schema: Schema, userName: String, password: String): Schema
}
