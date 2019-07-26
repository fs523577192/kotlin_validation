package org.firas.rdb_model.type

import org.firas.rdb_model.dialect.DbDialect

/**
 *
 * @author Wu Yuping
 */
class UnsupportedDbTypeException: RuntimeException {

    companion object {
        private fun getDefaultMessage(dbType: DbType, dbDialect: DbDialect): String {
            return dbDialect::class.simpleName + " does not support " + dbType::class.simpleName
        }
    }

    val dbType: DbType
    val dbDialect: DbDialect

    constructor(dbType: DbType, dbDialect: DbDialect):
            this(dbType, dbDialect, getDefaultMessage(dbType, dbDialect))

    constructor(dbType: DbType, dbDialect: DbDialect, message: String): super(message) {
        this.dbType = dbType
        this.dbDialect = dbDialect
    }

    constructor(dbType: DbType, dbDialect: DbDialect, cause: Throwable):
            this(dbType, dbDialect, getDefaultMessage(dbType, dbDialect), cause)

    constructor(dbType: DbType, dbDialect: DbDialect, message: String, cause: Throwable):
            super(message, cause) {
        this.dbType = dbType
        this.dbDialect = dbDialect
    }
}