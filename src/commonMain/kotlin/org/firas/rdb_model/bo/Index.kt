package org.firas.rdb_model.bo

/**
 * 
 * @author Wu Yuping
 */
class Index(val type: IndexType, val name: String?,
                 columnList: List<ColumnInIndex>? = null) {

    var table: Table? = null

    var columnList: List<ColumnInIndex>? = columnList
        set(value) {
            if (null != value) {
                table = value[0].column.table
                if (value.any { column -> !column.column.table!!.equals(table) }) {
                    throw IllegalArgumentException("All the columns should be in the same table")
                }
            }
            field = value
        }
}