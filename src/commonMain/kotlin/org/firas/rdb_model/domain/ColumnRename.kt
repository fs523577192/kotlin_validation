package org.firas.rdb_model.domain

import org.firas.rdb_model.bo.Column

/**
 * 
 * @author Wu Yuping
 */
data class ColumnRename(val column: Column, val newName: String)