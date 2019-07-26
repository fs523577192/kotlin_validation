package org.firas.rdb_model.dialect.mysql

import org.firas.rdb_model.bo.*
import org.firas.rdb_model.dialect.DbCharset
import org.firas.rdb_model.domain.ColumnAddition
import org.firas.rdb_model.domain.ColumnComment
import org.firas.rdb_model.domain.ColumnDrop
import org.firas.rdb_model.domain.ColumnModification
import org.firas.rdb_model.domain.ColumnRename
import org.firas.rdb_model.domain.TableCreation
import org.firas.rdb_model.type.*
import kotlin.collections.*
import kotlin.jvm.JvmStatic

/**
 * 
 * @author Wu Yuping
 */
class MySQLDialect private constructor(): org.firas.rdb_model.dialect.DbDialect() {

    companion object {
        @JvmStatic
        val instance = MySQLDialect()
    }

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
        return MySQLCharset.instance
    }

    override fun dbTypeToSQL(dbType: DbType): String {
        return when (dbType) {
            is IntegerType ->
                "INT" + if (dbType.unsigned) " UNSIGNED" else ""
            is VarcharType ->
                "VARCHAR(" + dbType.length + ") CHARSET " + dbType.charset
            is BigIntType ->
                "BIGINT" + if (dbType.unsigned) " UNSIGNED" else ""
            is SmallIntType ->
                "SMALLINT" + if (dbType.unsigned) " UNSIGNED" else ""
            is DoubleType ->
                "DOUBLE"
            is FloatType ->
                "FLOAT"
            is DecimalType ->
                if (dbType.precision > 65)
                    throw IllegalArgumentException("MySQL only support decimal precision " +
                            "that is not greater than 65: " + dbType.precision)
                else if (dbType.scale > dbType.precision)
                    throw IllegalArgumentException("The scale " + dbType.scale +
                            " is greater than the precision " + dbType.precision)
                else if (dbType.scale > 30)
                    throw IllegalArgumentException("MySQL only support decimal scale " +
                            "that is not greater than 30: " + dbType.scale)
                else "DECIMAL(" + dbType.precision + ", " + dbType.scale + ')'
            is DateTimeType ->
                if (dbType.fractionalSeconds <= 6) "DATETIME(" + dbType.fractionalSeconds + ')'
                else throw IllegalArgumentException("MySQL only support fractionalSeconds " +
                        "that is not greater than 6: " + dbType.fractionalSeconds)
            is ClobType ->
                "CLOB"
            is BlobType ->
                "BLOB"
            else ->
                throw UnsupportedDbTypeException(dbType, this)
        }
    }

    override fun columnToSQL(column: Column): String {
        val dbType = dbTypeToSQL(column.dbType)
        val nameQuote = getNameQuote()
        return "$nameQuote${column.name}$nameQuote $dbType " +
                (if (column.nullable) "" else "NOT ") +
                "NULL DEFAULT ${column.defaultValue}" +
                (if (null == column.onUpdateValue) "" else " ON UPDATE ${column.onUpdateValue}") +
                (if (column.comment.isNullOrEmpty()) "" else " COMMENT ${column.comment}")
    }

    override fun columnCommentToSQL(columnComment: ColumnComment): String {
        val nameQuote = getNameQuote()
        val column = columnComment.column
        val table = column.table!!
        val schema = table.schema!!
        return "ALTER TABLE $nameQuote${schema.name}$nameQuote." +
                "$nameQuote${table.name}$nameQuote " +
                "MODIFY COLUMN " + columnToSQL(column)
    }

    override fun columnAdditionsToSQL(columnAdditions: Collection<ColumnAddition>): List<String> {
        val nameQuote = getNameQuote()
        val result = StringBuilder()
        var table: Table? = null
        for (columnAddition in columnAdditions) {
            val column = columnAddition.column
            if (null == table) {
                table = column.table ?: throw IllegalArgumentException("The columns should be in the same table")
                val schema = table.schema!!
                result.append("ALTER TABLE $nameQuote${schema.name}$nameQuote." +
                        "$nameQuote${table.name}$nameQuote ")
            } else if (column.table != table) {
                throw IllegalArgumentException("The columns should be in the same table")
            }

            result.append("ADD COLUMN " + columnToSQL(column) + ", ")
        }
        return listOf(if (result.endsWith(", ")) result.substring(0, result.length - 2) else "")
    }

    override fun columnRenamesToSQL(columnRenames: Collection<ColumnRename>): List<String> {
        val nameQuote = getNameQuote()
        val result = StringBuilder()
        var table: Table? = null
        for (columnRename in columnRenames) {
            val column = columnRename.column
            if (null == table) {
                table = column.table ?: throw IllegalArgumentException("The columns should be in the same table")
                val schema = table.schema!!
                result.append("ALTER TABLE $nameQuote${schema.name}$nameQuote." +
                        "$nameQuote${table.name}$nameQuote ")
            } else if (column.table != table) {
                throw IllegalArgumentException("The columns should be in the same table")
            }


            val newColumn = Column(column.dbType, columnRename.newName,
                    column.nullable, column.defaultValue,
                    column.onUpdateValue, column.comment)

            result.append("CHANGE COLUMN $nameQuote${column.name}$nameQuote " + columnToSQL(newColumn) + ", ")
        }
        return listOf(if (result.endsWith(", ")) result.substring(0, result.length - 2) else "")
    }

    override fun columnModificationsToSQL(columnModifications: Collection<ColumnModification>): List<String> {
        val nameQuote = getNameQuote()
        val result = StringBuilder()
        var table: Table? = null
        for (columnModification in columnModifications) {
            val column = columnModification.column
            if (null == table) {
                table = column.table ?: throw IllegalArgumentException("The columns should be in the same table")
                val schema = table.schema!!
                result.append("ALTER TABLE $nameQuote${schema.name}$nameQuote." +
                        "$nameQuote${table.name}$nameQuote ")
            } else if (column.table != table) {
                throw IllegalArgumentException("The columns should be in the same table")
            }


            val newColumn = Column(columnModification.dbType, column.name,
                    columnModification.nullable, columnModification.defaultValue,
                    columnModification.onUpdateValue)

            result.append("MODIFY COLUMN " + columnToSQL(newColumn) + ", ")
        }
        return listOf(if (result.endsWith(", ")) result.substring(0, result.length - 2) else "")
    }

    override fun columnDropsToSQL(columnDrops: Collection<ColumnDrop>): List<String> {
        val nameQuote = getNameQuote()
        val result = StringBuilder()
        var table: Table? = null
        for (columnDrop in columnDrops) {
            val column = columnDrop.column
            if (null == table) {
                table = column.table ?: throw IllegalArgumentException("The columns should be in the same table")
                val schema = table.schema!!
                result.append("ALTER TABLE $nameQuote${schema.name}$nameQuote." +
                        "$nameQuote${table.name}$nameQuote ")
            } else if (column.table != table) {
                throw IllegalArgumentException("The columns should be in the same table")
            }

            result.append("DROP COLUMN " + "$nameQuote${column.name}$nameQuote, ")
        }
        return listOf(if (result.endsWith(", ")) result.substring(0, result.length - 2) else "")
    }

    override fun tableCreationToSQL(tableCreation: TableCreation): List<String> {
        val nameQuote = getNameQuote()

        val table = tableCreation.table
        if (table.columnMap.isNullOrEmpty()) {
            throw IllegalArgumentException("The table to be created must have at least one column")
        }

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

        // 4. indexes
        val normalKeys = table.indexMap.values.filter { IndexType.UNIQUE == it.type || IndexType.NORMAL == it.type }
        normalKeys.forEach { index ->
            builder.append(", ").append(if (IndexType.UNIQUE == index.type) "UNIQUE " else "").append("INDEX ")
                    .append(if (index.name.isNullOrBlank()) "" else "$nameQuote${index.name}$nameQuote")
                    .append('(').append(index.columnList!!.joinToString(transform = {
                        nameQuote + it.column.name + nameQuote +
                                (if (null != it.length) "(${it.length}) " else " ") +
                                it.direction
                    })).append(')')
        }

        builder.append(")")

        val engine: String? = table.attributes["engine"] as String?
        if (!engine.isNullOrBlank()) {
            builder.append(" ENGINE = $engine")
        }

        val defaultCharset: String? = table.attributes["defaultCharset"] as String?
        if (!defaultCharset.isNullOrBlank()) {
            builder.append(" DEFAULT CHARSET $defaultCharset")
        }
        return listOf(builder.toString())
    }

    override fun fetchInfo(schema: Schema, userName: String, password: String): Schema {
        TODO()
    }
}
