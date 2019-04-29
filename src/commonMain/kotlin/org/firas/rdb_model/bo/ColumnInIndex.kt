package org.firas.rdb_model.bo

/**
 * 
 * @author Wu Yuping
 */
class ColumnInIndex(val index: Index, val column: Column, val length: Int? = null,
                    val direction: SortDirection = SortDirection.ASC)
