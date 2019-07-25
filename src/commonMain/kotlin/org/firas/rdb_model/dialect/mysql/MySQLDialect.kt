package org.firas.rdb_model.dialect.mysql

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
class MySQLDialect: org.firas.rdb_model.dialect.DbDialect {

    /**
     * @return the SQL to check whether the DB connection is usable
     */
    override fun validateQuery(): String {
        return "select 1"
    }

    override fun getNameQuote(): String {
        return "`"
    }

    override fun getCharset(): DbCharset {
        return MySQLCharset()
    }

    override fun toSQL(dbType: DbType): String {
        when (dbType) {
        }
    }

    override fun toSQL(column: Column): String {
        val dbType = toSQL(column.dbType)
        val nameQuote = getNameQuote()
        return "$nameQuote${column.name}$nameQuote $dbType " +
                (if (column.nullable) "" else "NOT ") +
                "NULL DEFAULT ${column.defaultValue} " +
                (if (null == column.onUpdateValue) "" else "ON UPDATE ${column.onUpdateValue}")
    }

    override fun toSQL(columnComment: ColumnComment): String {
        return ""
    }

    override fun toSQL(columnAdditions: Collection<ColumnAddition>): List<String> {
        val nameQuote = getNameQuote()
        val result = StringBuilder()
        var table: Table? = null
        for (columnAddition in columnAdditions) {
            val column = columnAddition.column
            if (null == table) {
                table = column.table ? throw IllegalArgumentException("The columns should be in the same table")
                val schema = table!!.schema
                result.append("ALTER TABLE $nameQuote${schema!!.name}$nameQuote." +
                        "$nameQuote${table.name}$nameQuote ")
            } else if (column.table != table) {
                throw IllegalArgumentException("The columns should be in the same table")
            }

            result.append("ADD COLUMN " + toSQL(column) + ", ")
        }
        return List.of(if (result.length > 2) result.substring(0, result.length - 2) else "")
    }

    override fun toSQL(columnRenames: Collection<ColumnRename>): List<String> {
    }

    override fun toSQL(columnModifications: Collection<ColumnModification>): List<String> {
        val nameQuote = getNameQuote()
        val result = ArrayList<String>(columnModifications.size)
        var table: Table? = null
        for (columnModification in columnModifications) {
            val column = columnModification.column
            if (null == table) {
                table = column.table ? throw IllegalArgumentException("The columns should be in the same table")
                val schema = table!!.schema
                result.append("ALTER TABLE $nameQuote${schema!!.name}$nameQuote." +
                        "$nameQuote${table.name}$nameQuote ")
            } else if (column.table != table) {
                throw IllegalArgumentException("The columns should be in the same table")
            }


            val newColumn = Column(columnModification.dbType, column.name,
                    columnModification.nullable, columnModification.defaultValue,
                    columnModification.onUpdateValue)

            result.add("MODIFY COLUMN " + toSQL(newColumn) + ", ")
        }
        return List.of(if (result.length > 2) result.substring(0, result.length - 2) else "")
    }

    override fun toSQL(columnDrops: Collection<ColumnDrop): List<String> {
        val nameQuote = getNameQuote()
        val result = ArrayList<String>(columnDrops.size)
        var table: Table? = null
        for (columnDrop in columnDrops) {
            val column = columnDrop.column
            if (null == table) {
                table = column.table ? throw IllegalArgumentException("The columns should be in the same table")
                val schema = table!!.schema
                result.append("ALTER TABLE $nameQuote${schema!!.name}$nameQuote." +
                        "$nameQuote${table.name}$nameQuote ")
            } else if (column.table != table) {
                throw IllegalArgumentException("The columns should be in the same table")
            }

            result.add("DROP COLUMN " + "$nameQuote${column.name}$nameQuote, ")
        }
        return List.of(if (result.length > 2) result.substring(0, result.length - 2) else "")
    }

    override fun toSQL(tableCreation: TableCreation): String {
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

    override fun fetchInfo(schema: Schema, userName: String, password: String): Schema {
    }
}
