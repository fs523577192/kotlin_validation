package org.firas.rdb_model.domain

import org.firas.rdb_model.bo.Column
import org.firas.rdb_model.type.DbType

/**
 * 
 * @author Wu Yuping
 */
data class ColumnModification(val column: Column, val dbType: DbType,
                              val nullable: Boolean, val defaultValue: String,
                              val onUpdateValue: String?)