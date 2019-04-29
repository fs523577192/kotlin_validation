package org.firas.rdb_model.domain

import org.firas.rdb_model.bo.Table

/**
 * 
 * @author Wu Yuping
 */
data class TableCreation(val table: Table, val ifNotExists: Boolean = false)