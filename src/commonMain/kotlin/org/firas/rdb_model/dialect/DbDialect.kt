package org.firas.rdb_model.dialect

import org.firas.rdb_model.bo.*
import org.firas.rdb_model.domain.*
import org.firas.rdb_model.type.DbType
import kotlin.collections.*
import kotlin.js.JsName

/**
 * 
 * @author Wu Yuping
 */
abstract class DbDialect {

    /**
     * @return the SQL to check whether the DB connection is usable
     */
    @JsName("validateQuery")
    abstract fun validateQuery(): String

    @JsName("getNameQuote")
    abstract fun getNameQuote(): String

    @JsName("getCharset")
    abstract fun getCharset(): DbCharset

    @JsName("dbTypeToSQL")
    abstract fun dbTypeToSQL(dbType: DbType): String

    @JsName("columnToSQL")
    open fun columnToSQL(column: Column): String {
        val nameQuote = getNameQuote()
        val dbType = dbTypeToSQL(column.dbType)
        return "$nameQuote${column.name}$nameQuote $dbType " +
                (if (column.nullable) "" else "NOT ") +
                "NULL DEFAULT ${column.defaultValue} " +
                (if (null == column.onUpdateValue) "" else "ON UPDATE ${column.onUpdateValue}")
    }

    @JsName("columnCommentToSQL")
    abstract fun columnCommentToSQL(columnComment: ColumnComment): String

    @JsName("columnAdditionsToSQL")
    open fun columnAdditionsToSQL(columnAdditions: Collection<ColumnAddition>): List<String> {
        val nameQuote = getNameQuote()
        val result = ArrayList<String>(columnAdditions.size)
        var table: Table? = null
        for (columnAddition in columnAdditions) {
            val column = columnAddition.column
            if (null == table) {
                table = column.table ?: throw IllegalArgumentException("The columns should be in the same table")
            } else if (column.table != table) {
                throw IllegalArgumentException("The columns should be in the same table")
            }

            val schema = table.schema
            result.add("ALTER TABLE $nameQuote${schema!!.name}$nameQuote." +
                    "$nameQuote${table.name}$nameQuote ADD COLUMN " +
                    columnToSQL(column))
        }
        return result
    }

    @JsName("columnRenamesToSQL")
    abstract fun columnRenamesToSQL(columnRenames: Collection<ColumnRename>): List<String>

    @JsName("columnModificationsToSQL")
    open fun columnModificationsToSQL(columnModifications: Collection<ColumnModification>): List<String> {
        val nameQuote = getNameQuote()
        val result = ArrayList<String>(columnModifications.size)
        var table: Table? = null
        for (columnModification in columnModifications) {
            val column = columnModification.column
            if (null == table) {
                table = column.table ?: throw IllegalArgumentException("The columns should be in the same table")
            } else if (column.table != table) {
                throw IllegalArgumentException("The columns should be in the same table")
            }

            val schema = table.schema

            val newColumn = Column(columnModification.dbType, column.name,
                    columnModification.nullable, columnModification.defaultValue,
                    columnModification.onUpdateValue)

            result.add("ALTER TABLE $nameQuote${schema!!.name}$nameQuote." +
                    "$nameQuote${table.name}$nameQuote MODIFY COLUMN " +
                    columnToSQL(newColumn))
        }
        return result
    }

    @JsName("columnDropsToSQL")
    open fun columnDropsToSQL(columnDrops: Collection<ColumnDrop>): List<String> {
        val nameQuote = getNameQuote()
        val result = ArrayList<String>(columnDrops.size)
        var table: Table? = null
        for (columnDrop in columnDrops) {
            val column = columnDrop.column
            if (null == table) {
                table = column.table ?: throw IllegalArgumentException("The columns should be in the same table")
            } else if (column.table != table) {
                throw IllegalArgumentException("The columns should be in the same table")
            }

            val schema = table.schema
            result.add("ALTER TABLE $nameQuote${schema!!.name}$nameQuote." +
                    "$nameQuote${table.name}$nameQuote DROP COLUMN " +
                    "$nameQuote${column.name}$nameQuote")
        }
        return result
    }

    @JsName("tableCreationToSQL")
    open fun tableCreationToSQL(tableCreation: TableCreation): List<String> {
        val nameQuote = getNameQuote()

        val table = tableCreation.table
        if (table.columnMap.isNullOrEmpty()) {
            throw IllegalArgumentException("The table to be created (${table.name}) must have at least one column")
        }

        val result = ArrayList<String>()
        result.add("") // placeholder

        // 1. "create table"
        val schema = table.schema!!
        val builder = StringBuilder("CREATE TABLE $nameQuote${schema.name}$nameQuote." +
                "$nameQuote${table.name}$nameQuote " +
                if (tableCreation.ifNotExists) "IF NOT EXISTS (" else "("
        )

        // 2. column definitions
        val columnDefinitions = table.columnMap.values.joinToString(transform = { columnToSQL(it) })
        builder.append(columnDefinitions)

        // 3. primary key
        val primaryKeys = table.indexMap.values.filter { it.type == IndexType.PRIMARY }
        if (primaryKeys.size > 1) {
            throw IllegalArgumentException("The table to be created (${table.name}) cannot have multiple primary keys")
        } else if (primaryKeys.size == 1) {
            val primaryKey = primaryKeys[0]

            val columnList = primaryKey.columnList
            if (columnList.isNullOrEmpty()) {
                throw IllegalArgumentException("There is no column in the primary key definition " +
                        "of the table to be created (${table.name})")
            }

            builder.append(", PRIMARY KEY ")
            if (!primaryKey.name.isNullOrBlank()) {
                builder.append(nameQuote).append(primaryKey.name).append(nameQuote)
            }
            builder.append('(').append(columnList.joinToString(transform = {
                nameQuote + it.column.name + nameQuote +
                        (if (null != it.length) "(${it.length}) " else " ") +
                        it.direction
            })).append(')')
        }

        result[0] = builder.append(')').toString()

        // 4. indexes
        val normalKeys = table.indexMap.values.filter { IndexType.UNIQUE == it.type || IndexType.NORMAL == it.type }
        normalKeys.forEach { index ->
            result.add("CREATE " + (if (IndexType.UNIQUE == index.type) "UNIQUE " else "") + "INDEX " +
                    (if (index.name.isNullOrBlank()) "" else "$nameQuote${index.name}$nameQuote") +
                    " ON TABLE $nameQuote${schema.name}$nameQuote.$nameQuote${table.name}$nameQuote " +
                    '(' + index.columnList!!.joinToString(transform = {
                        nameQuote + it.column.name + nameQuote +
                                (if (null != it.length) "(${it.length}) " else " ") +
                                it.direction
                    }) + ')')
        }
        return result
    }

    @JsName("tableDropToSQL")
    open fun tableDropToSQL(tableDrop: TableDrop): String {
        val nameQuote = getNameQuote()

        val table = tableDrop.table
        val schema = table.schema!!
        return "DROP TABLE $nameQuote${schema.name}$nameQuote.$nameQuote${table.name}$nameQuote"
    }

    @JsName("fetchInfo")
    abstract fun fetchInfo(schema: Schema, userName: String, password: String): Schema
}
