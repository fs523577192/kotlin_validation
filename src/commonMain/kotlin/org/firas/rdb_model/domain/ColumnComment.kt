package org.firas.rdb_model.domain

import org.firas.rdb_model.bo.Column

/**
 * 
 * @author Wu Yuping
 */
data class ColumnComment(val column: Column, val comment: String)